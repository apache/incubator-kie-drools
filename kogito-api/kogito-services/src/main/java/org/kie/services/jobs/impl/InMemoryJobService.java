/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.services.jobs.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.Model;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.Processes;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryJobService implements JobsService, AutoCloseable {

    public static final String IN_MEMORY_JOB_SERVICE_POOL_SIZE_PROPERTY = "kogito.in-memory.job-service.pool-size";
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryJobService.class);
    protected static final String TRIGGER = "timer";

    protected final ScheduledThreadPoolExecutor scheduler;
    protected final UnitOfWorkManager unitOfWorkManager;

    protected ConcurrentHashMap<String, ScheduledFuture<?>> scheduledJobs = new ConcurrentHashMap<>();
    private final Processes processes;

    private static ConcurrentHashMap<Processes, InMemoryJobService> INSTANCE = new ConcurrentHashMap<>();

    protected InMemoryJobService(Processes processes, UnitOfWorkManager unitOfWorkManager) {
        this.processes = processes;
        this.unitOfWorkManager = unitOfWorkManager;
        this.scheduler = new ScheduledThreadPoolExecutor(Integer.parseInt(System.getProperty(IN_MEMORY_JOB_SERVICE_POOL_SIZE_PROPERTY, "10")));
    }

    public static InMemoryJobService get(final Processes processes, final UnitOfWorkManager unitOfWorkManager) {
        Objects.requireNonNull(processes);
        Objects.requireNonNull(unitOfWorkManager);
        return INSTANCE.computeIfAbsent(processes, (k) -> new InMemoryJobService(processes, unitOfWorkManager));
    }

    @Override
    public String scheduleProcessJob(ProcessJobDescription description) {
        LOGGER.debug("ScheduleProcessJob: {}", description);
        ScheduledFuture<?> future;
        if (description.expirationTime().repeatInterval() != null) {
            future = scheduler.scheduleAtFixedRate(repeatableProcessJobByDescription(description), calculateDelay(description), description.expirationTime().repeatInterval(), TimeUnit.MILLISECONDS);
        } else {
            future = scheduler.schedule(processJobByDescription(description), calculateDelay(description), TimeUnit.MILLISECONDS);
        }
        scheduledJobs.put(description.id(), future);
        return description.id();
    }

    @Override
    public String scheduleProcessInstanceJob(ProcessInstanceJobDescription description) {
        ScheduledFuture<?> future;
        if (description.expirationTime().repeatInterval() != null) {
            future = scheduler.scheduleAtFixedRate(
                    getSignalProcessInstanceCommand(description, false, description.expirationTime().repeatLimit()),
                    calculateDelay(description), description.expirationTime().repeatInterval(), TimeUnit.MILLISECONDS);
        } else {
            future = scheduler.schedule(getSignalProcessInstanceCommand(description, true, 1), calculateDelay(description),
                    TimeUnit.MILLISECONDS);
        }
        scheduledJobs.put(description.id(), future);
        return description.id();
    }

    public Runnable getSignalProcessInstanceCommand(ProcessInstanceJobDescription description, boolean remove, int limit) {
        return new SignalProcessInstanceOnExpiredTimer(description.id(), description
                .processInstanceId(), description.processId(), remove, limit);
    }

    @Override
    public boolean cancelJob(String id) {
        return cancelJob(id, true);
    }

    public boolean cancelJob(String id, boolean force) {
        LOGGER.debug("Cancel Job: {}", id);
        if (scheduledJobs.containsKey(id)) {
            return scheduledJobs.remove(id).cancel(force);
        }
        return false;
    }

    @Override
    public ZonedDateTime getScheduledTime(String id) {
        if (scheduledJobs.containsKey(id)) {
            ScheduledFuture<?> scheduled = scheduledJobs.get(id);
            long remainingTime = scheduled.getDelay(TimeUnit.MILLISECONDS);
            if (remainingTime > 0) {
                return ZonedDateTime.from(Instant.ofEpochMilli(System.currentTimeMillis() + remainingTime));
            }
        }
        return null;
    }

    protected long calculateDelay(JobDescription description) {
        long delay = Duration.between(ZonedDateTime.now(), description.expirationTime().get()).toMillis();
        if (delay <= 0) {
            return 1;
        }
        return delay;
    }

    protected Runnable processJobByDescription(ProcessJobDescription description) {
        return new StartProcessOnExpiredTimer(description.id(), description.process(), true, -1);
    }

    protected Runnable repeatableProcessJobByDescription(ProcessJobDescription description) {
        return new StartProcessOnExpiredTimer(description.id(), description.process(), false, description.expirationTime().repeatLimit());
    }

    private class SignalProcessInstanceOnExpiredTimer implements Runnable {

        private final String id;
        private boolean removeAtExecution;
        private String processInstanceId;
        private Integer limit;
        private String processId;

        private SignalProcessInstanceOnExpiredTimer(String id, String processInstanceId, String processId, boolean removeAtExecution, Integer limit) {
            this.id = id;
            this.processInstanceId = processInstanceId;
            this.removeAtExecution = removeAtExecution;
            this.limit = limit;
            this.processId = processId;
        }

        @Override
        public void run() {
            try {
                LOGGER.info("Job {} started", id);
                Process<? extends Model> process = processes.processById(processId);
                limit--;
                Boolean executed = new TriggerJobCommand(processInstanceId, id, limit, process, unitOfWorkManager).execute();
                if (limit == 0 || !executed) {
                    cancelJob(id, false);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (removeAtExecution) {
                    cancelJob(id, true);
                }
            }
        }
    }

    private class StartProcessOnExpiredTimer implements Runnable {

        private final String id;

        private boolean removeAtExecution;
        @SuppressWarnings("rawtypes")
        private org.kie.kogito.process.Process process;

        private Integer limit;

        private StartProcessOnExpiredTimer(String id, org.kie.kogito.process.Process<?> process, boolean removeAtExecution, Integer limit) {
            this.id = id;
            this.process = process;
            this.removeAtExecution = removeAtExecution;
            this.limit = limit;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            try {
                LOGGER.debug("Job {} started", id);
                UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                    org.kie.kogito.process.ProcessInstance<?> pi = process.createInstance(process.createModel());
                    if (pi != null) {
                        pi.start(TRIGGER, null);
                    }
                    return null;
                });
                limit--;
                if (limit == 0) {
                    cancelJob(id, false);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (removeAtExecution) {
                    cancelJob(id, true);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        scheduledJobs.clear();
        scheduler.shutdown();
    }
}

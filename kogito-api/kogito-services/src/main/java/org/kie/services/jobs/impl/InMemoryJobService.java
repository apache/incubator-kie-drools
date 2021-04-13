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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.timer.TimerInstance;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryJobService implements JobsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryJobService.class);
    private static final String TRIGGER = "timer";

    protected final ScheduledThreadPoolExecutor scheduler;
    protected final KogitoProcessRuntime processRuntime;
    protected final UnitOfWorkManager unitOfWorkManager;

    protected ConcurrentHashMap<String, ScheduledFuture<?>> scheduledJobs = new ConcurrentHashMap<>();

    public InMemoryJobService(KogitoProcessRuntime processRuntime, UnitOfWorkManager unitOfWorkManager) {
        this(1, processRuntime, unitOfWorkManager);
    }

    public InMemoryJobService(int threadPoolSize, KogitoProcessRuntime processRuntime, UnitOfWorkManager unitOfWorkManager) {
        this.scheduler = new ScheduledThreadPoolExecutor(threadPoolSize);
        this.processRuntime = processRuntime;
        this.unitOfWorkManager = unitOfWorkManager;
    }

    @Override
    public String scheduleProcessJob(ProcessJobDescription description) {
        LOGGER.debug("ScheduleProcessJob: {}", description);
        ScheduledFuture<?> future = null;
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
        ScheduledFuture<?> future = null;
        if (description.expirationTime().repeatInterval() != null) {
            future = scheduler.scheduleAtFixedRate(new SignalProcessInstanceOnExpiredTimer(description.id(), description.processInstanceId(), false, description.expirationTime().repeatLimit()),
                    calculateDelay(description), description.expirationTime().repeatInterval(), TimeUnit.MILLISECONDS);
        } else {
            future = scheduler.schedule(new SignalProcessInstanceOnExpiredTimer(description.id(), description
                    .processInstanceId(), true, 1), calculateDelay(description), TimeUnit.MILLISECONDS);
        }
        scheduledJobs.put(description.id(), future);
        return description.id();
    }

    @Override
    public boolean cancelJob(String id) {
        LOGGER.debug("Cancel Job: {}", id);
        if (scheduledJobs.containsKey(id)) {
            return scheduledJobs.remove(id).cancel(true);
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
        return Duration.between(ZonedDateTime.now(), description.expirationTime().get()).toMillis();
    }

    protected Runnable processJobByDescription(ProcessJobDescription description) {
        if (description.process() != null) {
            return new StartProcessOnExpiredTimer(description.id(), description.process(), true, -1);
        } else {
            return new LegacyStartProcessOnExpiredTimer(description.id(), description.processId(), true, -1);
        }
    }

    protected Runnable repeatableProcessJobByDescription(ProcessJobDescription description) {
        if (description.process() != null) {
            return new StartProcessOnExpiredTimer(description.id(), description.process(), false, description.expirationTime().repeatLimit());
        } else {
            return new LegacyStartProcessOnExpiredTimer(description.id(), description.processId(), false, description.expirationTime().repeatLimit());
        }
    }

    private class SignalProcessInstanceOnExpiredTimer implements Runnable {

        private final String id;
        private boolean removeAtExecution;
        private String processInstanceId;
        private Integer limit;

        private SignalProcessInstanceOnExpiredTimer(String id, String processInstanceId, boolean removeAtExecution, Integer limit) {
            this.id = id;
            this.processInstanceId = processInstanceId;
            this.removeAtExecution = removeAtExecution;
            this.limit = limit;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("Job {} started", id);
                UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                    KogitoProcessInstance pi = processRuntime.getProcessInstance(processInstanceId);
                    if (pi != null) {
                        String[] ids = id.split("_");
                        limit--;
                        pi.signalEvent("timerTriggered", TimerInstance.with(Long.valueOf(ids[1]), id, limit));
                        if (limit == 0) {
                            cancel(id);
                        }
                    } else {
                        // since owning process instance does not exist cancel timers
                        cancel(id);
                    }

                    return null;
                });
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (removeAtExecution) {
                    scheduledJobs.remove(id);
                }
            }
        }
    }

    private void cancel(String timerId) {
        ScheduledFuture<?> timer = scheduledJobs.remove(timerId);
        if (timer != null) {
            timer.cancel(false);
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
                    scheduledJobs.remove(id).cancel(false);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (removeAtExecution) {
                    scheduledJobs.remove(id);
                }
            }
        }
    }

    private class LegacyStartProcessOnExpiredTimer implements Runnable {

        private final String id;

        private boolean removeAtExecution;
        private String processId;

        private Integer limit;

        private LegacyStartProcessOnExpiredTimer(String id, String processId, boolean removeAtExecution, Integer limit) {
            this.id = id;
            this.processId = processId;
            this.removeAtExecution = removeAtExecution;
            this.limit = limit;
        }

        @Override
        public void run() {
            try {
                LOGGER.debug("Job {} started", id);
                UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                    KogitoProcessInstance pi = processRuntime.createProcessInstance(processId, null);
                    if (pi != null) {
                        processRuntime.startProcessInstance(pi.getStringId(), TRIGGER);
                    }

                    return null;
                });
                limit--;
                if (limit == 0) {
                    scheduledJobs.remove(id).cancel(false);
                }
                LOGGER.debug("Job {} completed", id);
            } finally {
                if (removeAtExecution) {
                    scheduledJobs.remove(id);
                }
            }
        }
    }
}

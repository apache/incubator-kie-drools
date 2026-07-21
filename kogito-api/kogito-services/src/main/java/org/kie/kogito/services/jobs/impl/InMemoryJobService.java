/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.services.jobs.impl;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryJobService implements JobsService, AutoCloseable {

    public static final String IN_MEMORY_JOB_SERVICE_POOL_SIZE_PROPERTY = "kogito.in-memory.job-service.pool-size";
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryJobService.class);

    protected ScheduledExecutorService scheduler;

    protected ConcurrentHashMap<String, ScheduledFuture<?>> scheduledJobs;
    protected List<JobExecutorFactory> jobExecutorFactories;

    public InMemoryJobService() {
        this(new ScheduledThreadPoolExecutor(Integer.parseInt(System.getProperty(IN_MEMORY_JOB_SERVICE_POOL_SIZE_PROPERTY, "10"))));
    }

    public InMemoryJobService(ScheduledExecutorService scheduler) {
        this.scheduledJobs = new ConcurrentHashMap<>();
        this.jobExecutorFactories = new ArrayList<>();
        this.scheduler = scheduler;
    }

    public InMemoryJobService registerJobExecutorFactory(JobExecutorFactory jobExecutorFactory) {
        Iterator<JobExecutorFactory> iterator = this.jobExecutorFactories.iterator();
        while (iterator.hasNext()) {
            JobExecutorFactory factory = iterator.next();
            if (factory.types().containsAll(jobExecutorFactory.types())) {
                iterator.remove();
            }
        }
        this.jobExecutorFactories.add(jobExecutorFactory);
        return this;
    }

    @Override
    public String scheduleJob(JobDescription jobDescription) {
        LOGGER.debug("ScheduleProcessJob: {}", jobDescription);
        return findJobExecutorFactory(jobDescription).map(jobExecutorFactory -> {
            long delay = calculateDelay(jobDescription);
            Long interval = jobDescription.expirationTime().repeatInterval();
            String jobId = jobDescription.id();
            scheduledJobs.put(jobId,
                    interval != null ? scheduler.scheduleAtFixedRate(jobExecutorFactory.createNewRepeteableRunnable(this, jobDescription), delay, interval, TimeUnit.MILLISECONDS)
                            : scheduler.schedule(jobExecutorFactory.createNewRunnable(this, jobDescription), delay, TimeUnit.MILLISECONDS));
            return jobId;
        }).orElseThrow(() -> new IllegalArgumentException("Could not schedule ProcessInstanceJobDescription " + jobDescription + ". No job executor factory provided"));
    }

    private Optional<JobExecutorFactory> findJobExecutorFactory(JobDescription jobDescription) {
        return jobExecutorFactories.stream().filter(factory -> factory.accept(jobDescription)).findFirst();
    }

    @Override
    public boolean cancelJob(String id) {
        return cancelJob(id, false);
    }

    public boolean cancelJob(String id, boolean force) {
        LOGGER.debug("Cancel Job: {}", id);
        ScheduledFuture<?> future = scheduledJobs.remove(id);
        return future != null && !future.isDone() && future.cancel(force);
    }

    @Override
    public String rescheduleJob(JobDescription jobDescription) {
        LOGGER.debug("Reschedule Job: {}", jobDescription.id());
        if (cancelJob(jobDescription.id())) {
            return scheduleJob(jobDescription);
        }
        return "Job reschedule failed";
    }

    protected long calculateDelay(JobDescription description) {
        long delay = Duration.between(ZonedDateTime.now(), description.expirationTime().get()).toMillis();
        return delay <= 0 ? 1 : delay;
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("closing in memory job service");
        scheduler.shutdownNow();
        scheduledJobs.values().forEach(v -> v.cancel(true));
        scheduledJobs.clear();
    }

    public void clearJobExecutorFactories() {
        scheduledJobs.values().forEach(v -> v.cancel(true));
        scheduledJobs.clear();
        jobExecutorFactories.clear();
    }
}

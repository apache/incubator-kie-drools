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
        ScheduledFuture<?> future;
        long delay = calculateDelay(jobDescription);
        Long interval = jobDescription.expirationTime().repeatInterval();
        Optional<JobExecutorFactory> jobExecutorFactoryFound = findJobExecutorFactory(jobDescription);

        if (jobExecutorFactoryFound.isEmpty()) {
            throw new IllegalArgumentException("Could not schedule " + jobDescription + ". No job executor factory provided");
        }

        JobExecutorFactory jobExecutorFactory = jobExecutorFactoryFound.get();

        if (interval != null) {
            future = scheduler.scheduleAtFixedRate(jobExecutorFactory.createNewRepeteableRunnable(this, jobDescription), delay, interval, TimeUnit.MILLISECONDS);
        } else {
            future = scheduler.schedule(jobExecutorFactory.createNewRunnable(this, jobDescription), delay, TimeUnit.MILLISECONDS);
        }
        scheduledJobs.put(jobDescription.id(), future);
        return jobDescription.id();
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
        if (scheduledJobs.containsKey(id)) {
            ScheduledFuture<?> future = scheduledJobs.remove(id);
            if (!future.isDone()) {
                return future.cancel(force);
            }
        }
        return false;
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
        if (delay <= 0) {
            return 1;
        }
        return delay;
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("closing in memory job service");
        scheduledJobs.clear();
        scheduledJobs.forEach((k, v) -> v.cancel(true));
        scheduler.shutdownNow();
    }

    public void clearJobExecutorFactories() {
        jobExecutorFactories.clear();
    }
}

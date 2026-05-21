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
package org.kie.kogito.jobs.service.scheduler.impl;

import java.util.Objects;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.executor.JobExecutorResolver;
import org.kie.kogito.jobs.service.job.DelegateJob;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsContext;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.timer.Trigger;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class TimerDelegateJobScheduler extends BaseTimerJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerDelegateJobScheduler.class);

    private JobExecutorResolver jobExecutorResolver;

    private VertxTimerServiceScheduler delegate;

    protected TimerDelegateJobScheduler() {
    }

    @Inject
    public TimerDelegateJobScheduler(ReactiveJobRepository jobRepository,
            @ConfigProperty(name = "kogito.jobs-service.backoffRetryMillis", defaultValue = "1000") long backoffRetryMillis,
            @ConfigProperty(name = "kogito.jobs-service.maxIntervalLimitToRetryMillis", defaultValue = "60000") long maxIntervalLimitToRetryMillis,
            @ConfigProperty(name = "kogito.jobs-service.schedulerChunkInMinutes", defaultValue = "10") long schedulerChunkInMinutes,
            @ConfigProperty(name = "kogito.jobs-service.schedulerMinTimerDelayInMillis", defaultValue = "1000") long schedulerMinTimerDelayInMillis,
            @ConfigProperty(name = "kogito.jobs-service.forceExecuteExpiredJobs", defaultValue = "true") boolean forceExecuteExpiredJobs,
            @ConfigProperty(name = "kogito.jobs-service.forceExecuteExpiredJobsOnServiceStart", defaultValue = "true") boolean forceExecuteExpiredJobsOnServiceStart,
            JobExecutorResolver jobExecutorResolver, VertxTimerServiceScheduler delegate) {
        super(jobRepository, backoffRetryMillis, maxIntervalLimitToRetryMillis, schedulerChunkInMinutes, schedulerMinTimerDelayInMillis, forceExecuteExpiredJobs,
                forceExecuteExpiredJobsOnServiceStart);
        LOGGER.info(
                "Creating JobScheduler with backoffRetryMillis={}, maxIntervalLimitToRetryMillis={}, schedulerChunkInMinutes={}, schedulerMinTimerDelayInMillis={}, forceExecuteExpiredJobs={}, forceExecuteExpiredJobsOnServiceStart={}",
                backoffRetryMillis, maxIntervalLimitToRetryMillis, schedulerChunkInMinutes, schedulerMinTimerDelayInMillis, forceExecuteExpiredJobs, forceExecuteExpiredJobsOnServiceStart);
        this.jobExecutorResolver = jobExecutorResolver;
        this.delegate = delegate;
    }

    @Override
    public PublisherBuilder<ManageableJobHandle> doSchedule(JobDetails job, Trigger trigger) {
        LOGGER.debug("Job Scheduling job: {}, trigger: {}", job, trigger);
        ManageableJobHandle jobHandle = delegate.scheduleJob(new DelegateJob(jobExecutorResolver, this),
                new JobDetailsContext(job), trigger);
        return ReactiveStreams.of(jobHandle);
    }

    @Override
    public Publisher<ManageableJobHandle> doCancel(JobDetails scheduledJob) {
        return ReactiveStreams
                .of(scheduledJob)
                .map(JobDetails::getScheduledId)
                .filter(Objects::nonNull)
                .map(scheduledId -> {
                    ManageableJobHandle handle = new ManageableJobHandle(scheduledId);
                    handle.setCancel(delegate.removeJob(handle));
                    return handle;
                })
                .buildRs();
    }

    /**
     * Removes only the programed in-memory timers.
     */
    public void unscheduleTimers() {
        LOGGER.debug("Removing in-memory scheduled timers");
        super.getScheduledJobs().forEach(record -> {
            boolean removed = delegate.removeJob(new ManageableJobHandle(record.getHandleId()));
            LOGGER.debug("Vertex timer: {} for jobId: {}, was removed: {}", record.getHandleId(), record.getJobId(), removed);
            super.unregisterScheduledJob(JobDetails.builder().id(record.getJobId()).build());
        });
    }
}

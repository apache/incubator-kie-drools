/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.jobs.service.scheduler.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.executor.JobExecutorResolver;
import org.kie.kogito.jobs.service.job.DelegateJob;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsContext;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.jobs.service.stream.AvailableStreams;
import org.kie.kogito.jobs.service.stream.JobStreams;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.kie.kogito.timer.Trigger;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class TimerDelegateJobScheduler extends BaseTimerJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerDelegateJobScheduler.class);

    private JobExecutorResolver jobExecutorResolver;

    private VertxTimerServiceScheduler delegate;

    private JobStreams jobStreams;

    protected TimerDelegateJobScheduler() {
    }

    @Inject
    public TimerDelegateJobScheduler(ReactiveJobRepository jobRepository,
            @ConfigProperty(name = "kogito.jobs-service.backoffRetryMillis") long backoffRetryMillis,
            @ConfigProperty(name = "kogito.jobs-service.maxIntervalLimitToRetryMillis") long maxIntervalLimitToRetryMillis,
            @ConfigProperty(name = "kogito.jobs-service.schedulerChunkInMinutes") long schedulerChunkInMinutes,
            @ConfigProperty(name = "kogito.jobs-service.forceExecuteExpiredJobs") boolean forceExecuteExpiredJobs,
            JobExecutorResolver jobExecutorResolver, VertxTimerServiceScheduler delegate,
            JobStreams jobStreams) {
        super(jobRepository, backoffRetryMillis, maxIntervalLimitToRetryMillis, schedulerChunkInMinutes, forceExecuteExpiredJobs);
        this.jobExecutorResolver = jobExecutorResolver;
        this.delegate = delegate;
        this.jobStreams = jobStreams;
    }

    @Override
    public PublisherBuilder<ManageableJobHandle> doSchedule(JobDetails job, Optional<Trigger> trigger) {
        LOGGER.debug("Job Scheduling {}", job);
        return ReactiveStreams
                .of(job)
                .map(j -> delegate.scheduleJob(new DelegateJob(jobExecutorResolver, jobStreams), new JobDetailsContext(j),
                        trigger.orElse(j.getTrigger())));
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

    //Stream Processors

    @Incoming(AvailableStreams.JOB_ERROR_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage<Boolean> jobErrorProcessor(JobExecutionResponse response) {
        LOGGER.warn("Error received {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(this::handleJobExecutionError, response)
                .findFirst()
                .run()
                .thenApply(Optional::isPresent)
                .exceptionally(e -> {
                    LOGGER.error("Error handling error {}", response, e);
                    return false;
                });
    }

    @Incoming(AvailableStreams.JOB_SUCCESS_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage<Boolean> jobSuccessProcessor(JobExecutionResponse response) {
        LOGGER.debug("Success received to be processed {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(this::handleJobExecutionSuccess, response)
                .findFirst()
                .run()
                .thenApply(Optional::isPresent)
                .exceptionally(e -> {
                    LOGGER.error("Error handling error {}", response, e);
                    return false;
                });
    }
}

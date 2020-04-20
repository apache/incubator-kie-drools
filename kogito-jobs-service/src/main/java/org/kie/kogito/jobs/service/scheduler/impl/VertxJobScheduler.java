/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.scheduler.impl;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.vertx.axle.core.Vertx;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.BaseTimerJobScheduler;
import org.kie.kogito.jobs.service.stream.AvailableStreams;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job Scheduler based on Vert.x engine.
 */
@ApplicationScoped
public class VertxJobScheduler extends BaseTimerJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxJobScheduler.class);

    @Inject
    Vertx vertx;

    protected VertxJobScheduler() {
    }

    public VertxJobScheduler(Vertx vertx, JobExecutor jobExecutor, ReactiveJobRepository jobRepository,
                             long backoffRetryMillis,
                             long maxIntervalLimitToRetryMillis) {
        super(jobExecutor, jobRepository, backoffRetryMillis, maxIntervalLimitToRetryMillis);
        this.vertx = vertx;
    }

    @Override
    public PublisherBuilder<String> doSchedule(Duration delay, Job job) {
        LOGGER.debug("Job Scheduling {}", job);
        return ReactiveStreams
                .of(job)
                .map(j -> setTimer(delay, j))
                .map(String::valueOf);
    }

    @Override
    public PublisherBuilder<String> doPeriodicSchedule(Duration interval, Job job) {
        LOGGER.debug("Job Periodic Scheduling {}", job);
        return ReactiveStreams
                .of(job)
                .map(j -> setPeriodicTimer(interval, j))
                .map(String::valueOf);
    }

    private long setTimer(Duration delay, Job job) {
        return vertx.setTimer(delay.toMillis(), scheduledId -> execute(job));
    }

    private long setPeriodicTimer(Duration interval, Job job) {
        return vertx.setPeriodic(interval.toMillis(), scheduledId -> execute(job));
    }

    @Override
    public Publisher<Boolean> doCancel(ScheduledJob scheduledJob) {
        return ReactiveStreams
                .of(scheduledJob)
                .map(ScheduledJob::getScheduledId)
                .filter(Objects::nonNull)
                .map(Long::valueOf)
                .map(vertx::cancelTimer)
                .buildRs();
    }

    //Stream Processors

    @Incoming(AvailableStreams.JOB_ERROR_EVENTS)
    public CompletionStage jobErrorProcessor(JobExecutionResponse error) {
        LOGGER.warn("Error received {}", error);
        return handleJobExecutionError(error)
                .findFirst()
                .run();
    }

    @Incoming(AvailableStreams.JOB_SUCCESS_EVENTS)
    public CompletionStage jobSuccessProcessor(JobExecutionResponse response) {
        LOGGER.debug("Success received to be processed {}", response);
        return handleJobExecutionSuccess(response)
                .findFirst()
                .run();
    }
}
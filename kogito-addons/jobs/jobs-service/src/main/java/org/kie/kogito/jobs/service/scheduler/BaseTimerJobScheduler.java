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

package org.kie.kogito.jobs.service.scheduler;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base reactive Job Scheduler that performs the fundamental operations and let to the concrete classes to
 * implement the scheduling actions.
 */
public abstract class BaseTimerJobScheduler implements ReactiveJobScheduler<ScheduledJob> {

    private Logger logger = LoggerFactory.getLogger(BaseTimerJobScheduler.class);

    @Inject
    JobExecutor jobExecutor;

    @Inject
    ReactiveJobRepository jobRepository;

    @Override
    public Publisher<ScheduledJob> schedule(Job job) {
        return ReactiveStreams
                //1- check if the job is already scheduled
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMapCompletionStage(exists -> exists
                        ? cancel(job.getId()).thenApply(Objects::nonNull)
                        : CompletableFuture.completedFuture(Boolean.TRUE))
                .filter(Boolean.TRUE::equals)
                //2- calculate the delay (when the job should be executed)
                .map(checked -> job.getExpirationTime())
                .map(expirationTime -> Duration.between(ZonedDateTime.now(ZoneId.of("UTC")), expirationTime))
                //3- schedule the job
                .map(delay -> doSchedule(delay, job))
                .flatMapRsPublisher(p -> p)
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p)
                .buildRs();
    }

    public abstract Publisher<ScheduledJob> doSchedule(Duration delay, Job job);

    protected CompletionStage<Job> execute(Job job) {
        logger.info("Job executed ! {}", job);
        return jobExecutor.execute(job);
    }

    @Override
    public CompletionStage<ScheduledJob> cancel(String jobId) {
        logger.debug("Cancel Job Scheduling {}", jobId);
        return ReactiveStreams
                .fromCompletionStageNullable(jobRepository.get(jobId))
                .flatMapRsPublisher(this::doCancel)
                .filter(Boolean.TRUE::equals)
                .map(r -> jobRepository.delete(jobId))
                .findFirst()
                .run()
                .thenCompose(job -> job.orElseThrow(()-> new RuntimeException("Failed to cancel job scheduling " + jobId)));
    }

    public abstract Publisher<Boolean> doCancel(ScheduledJob scheduledJob);
}
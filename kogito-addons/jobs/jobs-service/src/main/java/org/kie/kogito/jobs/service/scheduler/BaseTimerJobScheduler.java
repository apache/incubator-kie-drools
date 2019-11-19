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
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base reactive Job Scheduler that performs the fundamental operations and let to the concrete classes to
 * implement the scheduling actions.
 */
public abstract class BaseTimerJobScheduler implements ReactiveJobScheduler<ScheduledJob> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTimerJobScheduler.class);

    @ConfigProperty(name = "kogito.job-service.backoffRetryMillis", defaultValue = "1000")
    long backoffRetryMillis;

    @ConfigProperty(name = "kogito.job-service.maxIntervalLimitToRetryMillis", defaultValue = "60000")
    long maxIntervalLimitToRetryMillis;

    @Inject
    JobExecutor jobExecutor;

    @Inject
    ReactiveJobRepository jobRepository;

    @Override
    public Publisher<ScheduledJob> schedule(Job job) {
        LOGGER.debug("Scheduling {}", job);
        return ReactiveStreams
                //1- check if the job is already scheduled
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> exists
                        ? handleExistingJob(job)
                        : ReactiveStreams.of(Boolean.TRUE))
                .filter(Boolean.TRUE::equals)
                //2- calculate the delay (when the job should be executed)
                .map(checked -> job.getExpirationTime())
                .map(expirationTime -> calculateDelay(expirationTime))
                //3- schedule the job
                .map(delay -> doSchedule(delay, job))
                .flatMapRsPublisher(p -> p)
                .map(scheduleId -> ScheduledJob
                        .builder()
                        .job(job)
                        .scheduledId(scheduleId)
                        .status(JobStatus.SCHEDULED)
                        .build())
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p)
                .buildRs();
    }

    private PublisherBuilder<Boolean> handleExistingJob(Job job) {
        //always returns true, canceling in case the job is already schedule
        return ReactiveStreams.fromCompletionStage(jobRepository.get(job.getId()))
                //handle scheduled and retry cases
                .flatMap(
                        j -> {
                            switch (j.getStatus()) {
                                case SCHEDULED:
                                    return handleExpirationTime(j)
                                            .map(CompletableFuture::completedFuture)
                                            .map(this::cancel);
                                case RETRY:
                                    return handleRetry(CompletableFuture.completedFuture(j));
                                default:
                                    //empty to break the stream processing
                                    return ReactiveStreams.empty();
                            }
                        })
                .map(j -> Boolean.TRUE)
                .onErrorResumeWith(t -> ReactiveStreams.empty());
    }

    private Duration calculateDelay(ZonedDateTime expirationTime) {
        return Duration.between(DateUtil.now(), expirationTime);
    }

    @Override
    public PublisherBuilder<ScheduledJob> handleJobExecutionSuccess(JobExecutionResponse response) {
        return ReactiveStreams.of(response)
                .map(JobExecutionResponse::getJobId)
                .flatMapCompletionStage(jobRepository::get)
                .map(scheduledJob -> ScheduledJob
                        .builder()
                        .of(scheduledJob)
                        .status(JobStatus.EXECUTED)
                        .build())
                //final state, removing the job
                .map(ScheduledJob::getJob)
                .map(Job::getId)
                .flatMapCompletionStage(jobRepository::delete);
    }

    private boolean notExpired(ZonedDateTime expirationTime) {
        return !isExpired(expirationTime);
    }

    private boolean isExpired(ZonedDateTime expirationTime) {
        final Duration limit = Duration.ofMillis(maxIntervalLimitToRetryMillis);
        return calculateDelay(expirationTime).plus(limit).isNegative();
    }

    private PublisherBuilder<ScheduledJob> handleExpirationTime(ScheduledJob scheduledJob) {
        return ReactiveStreams.of(scheduledJob)
                .map(ScheduledJob::getJob)
                .map(Job::getExpirationTime)
                .flatMapCompletionStage(time -> isExpired(time)
                        ? handleExpiredJob(scheduledJob)
                        : CompletableFuture.completedFuture(scheduledJob));
    }

    /**
     * Retries to schedule the job execution with a backoff time of {@link BaseTimerJobScheduler#backoffRetryMillis}
     * between retries and a limit of max interval of {@link BaseTimerJobScheduler#maxIntervalLimitToRetryMillis}
     * to retry, after this interval it the job it the job is not successfully executed it will remain in error
     * state, with no more retries.
     * @param errorResponse
     * @return
     */
    @Override
    public PublisherBuilder<ScheduledJob> handleJobExecutionError(JobExecutionResponse errorResponse) {
        return handleRetry(jobRepository.get(errorResponse.getJobId()));
    }

    private PublisherBuilder<ScheduledJob> handleRetry(CompletionStage<ScheduledJob> futureJob) {
        return ReactiveStreams.fromCompletionStage(futureJob)
                .flatMap(scheduledJob -> handleExpirationTime(scheduledJob)
                        .map(ScheduledJob::getStatus)
                        .filter(s -> !JobStatus.ERROR.equals(s))
                        .map(time -> doSchedule(Duration.ofMillis(backoffRetryMillis), scheduledJob.getJob()))
                        .flatMapRsPublisher(p -> p)
                        .map(scheduleId -> ScheduledJob
                                .builder()
                                .of(scheduledJob)
                                .scheduledId(scheduleId)
                                .status(JobStatus.RETRY)
                                .incrementRetries()
                                .build())
                        .map(jobRepository::save)
                        .flatMapCompletionStage(p -> p));
    }

    private CompletionStage<ScheduledJob> handleExpiredJob(ScheduledJob scheduledJob) {
        return Optional.of(ScheduledJob.builder()
                                   .of(scheduledJob)
                                   .status(JobStatus.ERROR)
                                   .build())
                //final state, removing the job
                .map(j -> Optional.of(j)
                        .map(ScheduledJob::getJob)
                        .map(Job::getId)
                        .map(id -> jobRepository
                                .delete(id)
                                .thenApply(deleted -> j)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(null);
    }

    public abstract Publisher<String> doSchedule(Duration delay, Job job);

    protected CompletionStage<Job> execute(Job job) {
        LOGGER.info("Job executed ! {}", job);
        return jobExecutor.execute(job);
    }

    public CompletionStage<ScheduledJob> cancel(CompletionStage<ScheduledJob> futureJob) {
        return ReactiveStreams
                .fromCompletionStageNullable(futureJob)
                .peek(job -> LOGGER.debug("Cancel Job Scheduling {}", job))
                .filter(scheduledJob -> JobStatus.SCHEDULED.equals(scheduledJob.getStatus()))
                .flatMap(scheduledJob -> ReactiveStreams.of(scheduledJob)
                        .flatMapRsPublisher(this::doCancel)
                        .filter(Boolean.TRUE::equals)
                        .map(c -> ScheduledJob
                                .builder()
                                .of(scheduledJob)
                                .status(JobStatus.CANCELED)
                                .build())
                        //final state, removing the job
                        .map(ScheduledJob::getJob)
                        .map(Job::getId)
                        .flatMapCompletionStage(jobRepository::delete))
                .findFirst()
                .run()
                .thenApply(job -> job.orElse(null));
    }

    @Override
    public CompletionStage<ScheduledJob> cancel(String jobId) {
        return cancel(jobRepository.get(jobId));
    }

    public abstract Publisher<Boolean> doCancel(ScheduledJob scheduledJob);
}
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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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

    @ConfigProperty(name = "kogito.jobs-service.backoffRetryMillis")
    long backoffRetryMillis;

    @ConfigProperty(name = "kogito.jobs-service.maxIntervalLimitToRetryMillis")
    long maxIntervalLimitToRetryMillis;

    /**
     * Flag to allow and force a job with expirationTime in the past to be executed immediately. If false an
     * exception will be thrown.
     */
    @ConfigProperty(name = "kogito.jobs-service.forceExecuteExpiredJobs")
    Optional<Boolean> forceExecuteExpiredJobs;

    /**
     * The current chunk size  in minutes the scheduler handles, it is used to keep a limit number of jobs scheduled
     * in the in-memory scheduler.
     */
    @ConfigProperty(name = "kogito.jobs-service.schedulerChunkInMinutes")
    long schedulerChunkInMinutes;

    @Inject
    JobExecutor jobExecutor;

    @Inject
    ReactiveJobRepository jobRepository;

    private final Map<String, ZonedDateTime> schedulerControl;

    protected BaseTimerJobScheduler() {
        this(null, null, 0, 0);
    }

    public BaseTimerJobScheduler(JobExecutor jobExecutor,
                                 ReactiveJobRepository jobRepository,
                                 long backoffRetryMillis,
                                 long maxIntervalLimitToRetryMillis) {
        this.jobExecutor = jobExecutor;
        this.jobRepository = jobRepository;
        this.backoffRetryMillis = backoffRetryMillis;
        this.maxIntervalLimitToRetryMillis = maxIntervalLimitToRetryMillis;
        this.schedulerControl = new ConcurrentHashMap<>();
    }

    @Override
    public Publisher<ScheduledJob> schedule(Job job) {
        LOGGER.debug("Scheduling {}", job);
        return ReactiveStreams
                //check if the job is already scheduled and persisted
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? handleExistingJob(job)
                        : ReactiveStreams.of(job))
                .flatMap(j -> isOnCurrentSchedulerChunk(job)
                        //in case the job is on the current bulk, proceed with scheduling process
                        ? doJobScheduling(job)
                        //in case the job is not on the current bulk, just save it to be scheduled later
                        : ReactiveStreams.fromCompletionStage(jobRepository.save(ScheduledJob.builder()
                                                                                         .job(job)
                                                                                         .status(JobStatus.SCHEDULED)
                                                                                         .build())))
                .buildRs();
    }

    /**
     * Performs the given job scheduling process on the scheduler, after all the validations already made.
     * @param job to be scheduled
     * @return
     */
    private PublisherBuilder<ScheduledJob> doJobScheduling(Job job) {
        return ReactiveStreams.of(job)
                //calculate the delay (when the job should be executed)
                .map(current -> job.getExpirationTime())
                .map(this::calculateDelay)
                .peek(delay -> Optional
                        .of(delay.isNegative())
                        .filter(Boolean.FALSE::equals)
                        .orElseThrow(() -> new RuntimeException("The expirationTime should be greater than current " +
                                                                        "time")))
                //schedule the job on the scheduler
                .map(delay -> schedule(delay, job))
                .flatMap(p -> p)
                .map(scheduleId -> ScheduledJob
                        .builder()
                        .job(job)
                        .scheduledId(scheduleId)
                        .status(JobStatus.SCHEDULED)
                        .build())
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p);
    }

    /**
     * Check if it should be scheduled (on the current chunk) or saved to be scheduled later.
     * @return
     */
    private boolean isOnCurrentSchedulerChunk(Job job) {
        return job.getExpirationTime().isBefore(DateUtil.now().plusMinutes(schedulerChunkInMinutes));
    }

    private PublisherBuilder<ScheduledJob> handleExistingJob(Job job) {
        //always returns true, canceling in case the job is already schedule
        return ReactiveStreams.fromCompletionStage(jobRepository.get(job.getId()))
                //handle scheduled and retry cases
                .flatMap(
                        j -> {
                            switch (j.getStatus()) {
                                case SCHEDULED:
                                    return handleExpirationTime(j)
                                            .map(scheduled -> ScheduledJob.builder()
                                                    .of(scheduled)
                                                    .status(JobStatus.CANCELED)
                                                    .build())
                                            .map(CompletableFuture::completedFuture)
                                            .flatMapCompletionStage(this::cancel)
                                            .map(deleted -> j);
                                case RETRY:
                                    return handleRetry(CompletableFuture.completedFuture(j));
                                default:
                                    //empty to break the stream processing
                                    return ReactiveStreams.empty();
                            }
                        })
                .onErrorResumeWith(t -> ReactiveStreams.empty());
    }

    private Duration calculateDelay(ZonedDateTime expirationTime) {
        //in case forceExecuteExpiredJobs is true, execute the job immediately (1ms)
        return Optional.of(Duration.between(DateUtil.now(), expirationTime))
                .filter(d -> !d.isNegative())
                .orElse(forceExecuteExpiredJobs
                                .filter(Boolean.TRUE::equals)
                                .map(f -> Duration.ofSeconds(1))
                                .orElse(Duration.ofSeconds(-1)));
    }

    private boolean validLimit(ScheduledJob job) {
        return Optional.of(job)
                .map(Job::getRepeatLimit)
                .filter(limit -> job.getExecutionCounter() < limit)
                .isPresent();
    }

    private boolean wasPeriodicScheduled(ScheduledJob job) {
        //jobs was executed more than once
        return Optional.ofNullable(job)
                .filter(j -> j.getExecutionCounter() > 1)
                .isPresent();
    }

    public PublisherBuilder<ScheduledJob> handleJobExecutionSuccess(ScheduledJob futureJob) {
        return ReactiveStreams.of(futureJob)
                .map(job -> ScheduledJob.builder().of(job).incrementExecutionCounter().build())
                //check if it is a repeatable job
                .flatMap(job -> job.hasInterval()
                        .filter(interval -> !wasPeriodicScheduled(job))
                        //schedule periodic job for the first time only
                        .map(Duration::ofMillis)
                        .map(interval -> periodicSchedule(interval, job)
                                .map(scheduledId -> ScheduledJob
                                        .builder()
                                        .of(job)
                                        .scheduledId(scheduledId)
                                        .expirationTime(DateUtil.now().plus(interval))
                                        .status(JobStatus.SCHEDULED)
                                        .build())
                                .flatMapCompletionStage(jobRepository::save))
                        //handle already periodic scheduled (after the first time)
                        .orElseGet(() -> ReactiveStreams.fromCompletionStage(
                                job.hasInterval()
                                        //handle already periodic scheduled job
                                        .map(interval -> Optional.of(job)
                                                .filter(this::wasPeriodicScheduled)
                                                .filter(this::validLimit)
                                                .map(s -> ScheduledJob
                                                        .builder()
                                                        .of(job)
                                                        .expirationTime(DateUtil.now().plus(Duration.ofMillis(interval)))
                                                        .build())
                                                .map(jobRepository::save)
                                                .orElse(null))
                                        //if completed set status as EXECUTED
                                        .orElseGet(() -> CompletableFuture.completedFuture(
                                                ScheduledJob
                                                        .builder()
                                                        .of(job)
                                                        .status(JobStatus.EXECUTED)
                                                        .build())))))
                //final state EXECUTED, removing the job
                .filter(job -> JobStatus.EXECUTED.equals(job.getStatus()))
                .flatMap(job -> ReactiveStreams.fromCompletionStage(cancel(CompletableFuture.completedFuture(job))));
    }

    @Override
    public PublisherBuilder<ScheduledJob> handleJobExecutionSuccess(JobExecutionResponse response) {
        return ReactiveStreams.of(response)
                .map(JobExecutionResponse::getJobId)
                .flatMapCompletionStage(jobRepository::get)
                .flatMap(this::handleJobExecutionSuccess);
    }

    private boolean isExpired(ZonedDateTime expirationTime) {
        final Duration limit = Duration.ofMillis(maxIntervalLimitToRetryMillis);
        return calculateDelay(expirationTime).plus(limit).isNegative();
    }

    private PublisherBuilder<ScheduledJob> handleExpirationTime(ScheduledJob scheduledJob) {
        return ReactiveStreams.of(scheduledJob)
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
                        .map(time -> schedule(Duration.ofMillis(backoffRetryMillis), scheduledJob))
                        .flatMap(p -> p)
                        .map(scheduleId -> ScheduledJob
                                .builder()
                                .of(scheduledJob)
                                .scheduledId(scheduleId)
                                .status(JobStatus.RETRY)
                                .incrementRetries()
                                .build())
                        .map(jobRepository::save)
                        .flatMapCompletionStage(p -> p))
                .peek(job -> LOGGER.debug("Retry executed {}", job));
    }

    private CompletionStage<ScheduledJob> handleExpiredJob(ScheduledJob scheduledJob) {
        return Optional.of(ScheduledJob.builder()
                                   .of(scheduledJob)
                                   .status(JobStatus.ERROR)
                                   .build())
                //final state, removing the job
                .map(j -> jobRepository
                        .delete(j)
                        .thenApply(deleted -> {
                            LOGGER.warn("Retry limit exceeded for job{}", j);
                            return j;
                        }))
                .orElse(null);
    }

    private PublisherBuilder<String> schedule(Duration delay, Job job) {
        return doSchedule(delay, job)
                .peek(registerScheduledJob(job));
    }

    private PublisherBuilder<String> periodicSchedule(Duration delay, Job job) {
        return doPeriodicSchedule(delay, job)
                .peek(registerScheduledJob(job));
    }

    private Consumer<String> registerScheduledJob(Job job) {
        return s -> schedulerControl.put(job.getId(), DateUtil.now());
    }

    public abstract PublisherBuilder<String> doSchedule(Duration delay, Job job);

    public abstract PublisherBuilder<String> doPeriodicSchedule(Duration delay, Job job);

    protected CompletionStage<ScheduledJob> execute(Job job) {
        LOGGER.debug("Executing job ! {}", job);
        return jobExecutor.execute(jobRepository.get(job.getId()))
                .whenComplete((j, t) -> unregisterScheduledJob(job));
    }

    private ZonedDateTime unregisterScheduledJob(Job job) {
        return schedulerControl.remove(job.getId());
    }

    public CompletionStage<ScheduledJob> cancel(CompletionStage<ScheduledJob> futureJob) {
        return ReactiveStreams
                .fromCompletionStageNullable(futureJob)
                .peek(job -> LOGGER.debug("Cancel Job Scheduling {}", job))
                .flatMap(scheduledJob -> Optional.ofNullable(scheduledJob.getScheduledId())
                        .map(id -> ReactiveStreams
                                .fromPublisher(this.doCancel(scheduledJob))
                                .map(b -> scheduledJob))
                        .orElse(ReactiveStreams.of(scheduledJob)))
                //final state, removing the job
                .flatMapCompletionStage(jobRepository::delete)
                .findFirst()
                .run()
                .thenApply(job -> job.orElse(null));
    }

    @Override
    public CompletionStage<ScheduledJob> cancel(String jobId) {
        return cancel(jobRepository
                              .get(jobId)
                              .thenApply(scheduledJob -> Optional
                                      .ofNullable(scheduledJob)
                                      .map(j -> ScheduledJob
                                              .builder()
                                              .of(j)
                                              .status(JobStatus.CANCELED)
                                              .build())
                                      .orElse(null)));
    }

    public abstract Publisher<Boolean> doCancel(ScheduledJob scheduledJob);

    @Override
    public Optional<ZonedDateTime> scheduled(String jobId) {
        return Optional.ofNullable(schedulerControl.get(jobId));
    }

    public void setForceExecuteExpiredJobs(boolean forceExecuteExpiredJobs) {
        this.forceExecuteExpiredJobs = Optional.of(forceExecuteExpiredJobs);
    }
}

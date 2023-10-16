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
package org.kie.kogito.jobs.service.scheduler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.service.exception.InvalidScheduleTimeException;
import org.kie.kogito.jobs.service.exception.JobServiceException;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.JobHandle;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

/**
 * Base reactive Job Scheduler that performs the fundamental operations and let to the concrete classes to
 * implement the scheduling actions.
 */
public abstract class BaseTimerJobScheduler implements ReactiveJobScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTimerJobScheduler.class);

    long backoffRetryMillis;

    long maxIntervalLimitToRetryMillis;

    /**
     * Flag to allow and force a job with expirationTime in the past to be executed immediately. If false an
     * exception will be thrown.
     */
    Optional<Boolean> forceExecuteExpiredJobs;

    /**
     * The current chunk size in minutes the scheduler handles, it is used to keep a limit number of jobs scheduled
     * in the in-memory scheduler.
     */
    long schedulerChunkInMinutes;

    private ReactiveJobRepository jobRepository;

    private final Map<String, ZonedDateTime> schedulerControl;

    protected BaseTimerJobScheduler() {
        this(null, 0, 0, 0, null);
    }

    protected BaseTimerJobScheduler(ReactiveJobRepository jobRepository,
            long backoffRetryMillis,
            long maxIntervalLimitToRetryMillis,
            long schedulerChunkInMinutes,
            Boolean forceExecuteExpiredJobs) {
        this.jobRepository = jobRepository;
        this.backoffRetryMillis = backoffRetryMillis;
        this.maxIntervalLimitToRetryMillis = maxIntervalLimitToRetryMillis;
        this.schedulerControl = new ConcurrentHashMap<>();
        this.schedulerChunkInMinutes = schedulerChunkInMinutes;
        this.forceExecuteExpiredJobs = Optional.ofNullable(forceExecuteExpiredJobs);
    }

    @Override
    public Publisher<JobDetails> schedule(JobDetails job) {
        LOGGER.debug("Scheduling {}", job);
        return ReactiveStreams
                //check if the job is already scheduled and persisted
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? handleExistingJob(job).map(existingJob -> Pair.of(exists, existingJob))
                        : ReactiveStreams.of(Pair.of(exists, job)))
                .flatMap(pair -> isOnCurrentSchedulerChunk(job)
                        //in case the job is on the current bulk, proceed with scheduling process
                        ? doJobScheduling(job, pair.getLeft())
                        //in case the job is not on the current bulk, just save it to be scheduled later
                        : ReactiveStreams.fromCompletionStage(jobRepository.save(jobWithStatus(job, JobStatus.SCHEDULED))))
                .buildRs();
    }

    @Override
    public PublisherBuilder<JobDetails> reschedule(String id, Trigger trigger) {
        return ReactiveStreams.fromCompletionStageNullable(jobRepository.merge(id, JobDetails.builder().trigger(trigger).build()))
                .peek(this::doCancel)
                .map(this::schedule)
                .flatMapRsPublisher(j -> j);
    }

    private JobDetails jobWithStatus(JobDetails job, JobStatus status) {
        return JobDetails.builder().of(job).status(status).build();
    }

    private JobDetails jobWithStatusAndHandle(JobDetails job, JobStatus status, ManageableJobHandle handle) {
        return JobDetails.builder().of(job).status(status).scheduledId(String.valueOf(handle.getId())).build();
    }

    /**
     * Performs the given job scheduling process on the scheduler, after all the validations already made.
     *
     * @param job to be scheduled
     * @return
     */
    private PublisherBuilder<JobDetails> doJobScheduling(JobDetails job, boolean exists) {
        return ReactiveStreams.of(job)
                //calculate the delay (when the job should be executed)
                .map(current -> job.getTrigger().hasNextFireTime())
                .map(DateUtil::fromDate)
                .map(this::calculateDelay)
                .peek(delay -> Optional
                        .of(delay.isNegative())
                        .filter(Boolean.FALSE::equals)
                        .orElseThrow(() -> new InvalidScheduleTimeException("The expirationTime should be greater than current " +
                                "time")))
                // new jobs in current bulk must be stored in the repository before we proceed to schedule, the same as
                // way as we do with new jobs that aren't. In this way we provide the same pattern for both cases.
                // https://issues.redhat.com/browse/KOGITO-8513
                .flatMap(delay -> !exists
                        ? ReactiveStreams.fromCompletionStage(jobRepository.save(jobWithStatus(job, JobStatus.SCHEDULED)))
                        : ReactiveStreams.fromCompletionStage(CompletableFuture.completedFuture(job)))
                //schedule the job on the scheduler
                .flatMap(j -> scheduleRegistering(job, Optional.empty()))
                .map(handle -> jobWithStatusAndHandle(job, JobStatus.SCHEDULED, handle))
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p);
    }

    /**
     * Check if it should be scheduled (on the current chunk) or saved to be scheduled later.
     *
     * @return
     */
    private boolean isOnCurrentSchedulerChunk(JobDetails job) {
        return DateUtil.fromDate(job.getTrigger().hasNextFireTime()).isBefore(DateUtil.now().plusMinutes(schedulerChunkInMinutes));
    }

    private PublisherBuilder<JobDetails> handleExistingJob(JobDetails job) {
        //always returns true, canceling in case the job is already schedule
        return ReactiveStreams.fromCompletionStage(jobRepository.get(job.getId()))
                //handle scheduled and retry cases
                .flatMap(
                        j -> {
                            switch (j.getStatus()) {
                                case SCHEDULED:
                                    return handleExpirationTime(j)
                                            .map(scheduled -> jobWithStatus(scheduled, JobStatus.CANCELED))
                                            .map(CompletableFuture::completedFuture)
                                            .flatMapCompletionStage(this::cancel)
                                            .map(deleted -> j);
                                case RETRY:
                                    return handleRetry(CompletableFuture.completedFuture(j))
                                            .flatMap(retryJob -> ReactiveStreams.empty());
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

    public PublisherBuilder<JobDetails> handleJobExecutionSuccess(JobDetails futureJob) {
        return ReactiveStreams.of(futureJob)
                .map(job -> JobDetails.builder().of(job).incrementExecutionCounter().build())
                //calculate the next programmed fire time if any
                .peek(job -> job.getTrigger().nextFireTime())
                .flatMapCompletionStage(jobRepository::save)
                //check if it is a repeatable job
                .flatMap(job -> Optional
                        .ofNullable(job.getTrigger())
                        .filter(trigger -> Objects.nonNull(trigger.hasNextFireTime()))
                        .map(time -> doJobScheduling(job, true))
                        //in case the job should not be executed anymore (there is no nextFireTime)
                        .orElseGet(() -> ReactiveStreams.of(jobWithStatus(job, JobStatus.EXECUTED))))
                //final state EXECUTED, removing the job, it is not kept on the repository
                .filter(j -> JobStatus.EXECUTED.equals(j.getStatus()))
                .flatMap(j -> ReactiveStreams.fromCompletionStage(cancel(CompletableFuture.completedFuture(j))));
    }

    @Override
    public PublisherBuilder<JobDetails> handleJobExecutionSuccess(JobExecutionResponse response) {
        return ReactiveStreams.of(response.getJobId())
                .flatMapCompletionStage(this::readJob)
                .flatMap(jobDetails -> jobDetails.map(this::handleJobExecutionSuccess)
                        .orElseThrow(() -> new JobServiceException("Job: " + response.getJobId() + " was not found in database.")));
    }

    private CompletionStage<Optional<JobDetails>> readJob(String jobId) {
        return jobRepository.get(jobId)
                .thenCompose(jobDetails -> CompletableFuture.completedFuture(Optional.ofNullable(jobDetails)));
    }

    private boolean isExpired(ZonedDateTime expirationTime, int retries) {
        final Duration limit =
                Duration.ofMillis(maxIntervalLimitToRetryMillis)
                        .minus(Duration.ofMillis(retries * backoffRetryMillis));
        return calculateDelay(expirationTime).plus(limit).isNegative();
    }

    private PublisherBuilder<JobDetails> handleExpirationTime(JobDetails scheduledJob) {
        return ReactiveStreams.of(scheduledJob)
                .map(JobDetails::getTrigger)
                .map(Trigger::hasNextFireTime)
                .map(DateUtil::fromDate)
                .flatMapCompletionStage(time -> isExpired(time, scheduledJob.getRetries())
                        ? handleExpiredJob(scheduledJob)
                        : CompletableFuture.completedFuture(scheduledJob));
    }

    /**
     * Retries to schedule the job execution with a backoff time of {@link BaseTimerJobScheduler#backoffRetryMillis}
     * between retries and a limit of max interval of {@link BaseTimerJobScheduler#maxIntervalLimitToRetryMillis}
     * to retry, after this interval it the job it the job is not successfully executed it will remain in error
     * state, with no more retries.
     *
     * @param errorResponse
     * @return
     */
    @Override
    public PublisherBuilder<JobDetails> handleJobExecutionError(JobExecutionResponse errorResponse) {
        return handleRetry(jobRepository.get(errorResponse.getJobId()));
    }

    private PublisherBuilder<JobDetails> handleRetry(CompletionStage<JobDetails> futureJob) {
        return ReactiveStreams.fromCompletionStage(futureJob)
                .flatMap(scheduledJob -> handleExpirationTime(scheduledJob)
                        .map(JobDetails::getStatus)
                        .filter(s -> !JobStatus.ERROR.equals(s))
                        .map(s -> scheduleRegistering(scheduledJob, Optional.of(getRetryTrigger())))
                        .flatMap(p -> p)
                        .map(scheduleId -> JobDetails.builder()
                                .of(jobWithStatusAndHandle(scheduledJob, JobStatus.RETRY, scheduleId))
                                .incrementRetries()
                                .build())
                        .map(jobRepository::save)
                        .flatMapCompletionStage(p -> p))
                .peek(job -> LOGGER.debug("Retry executed {}", job));
    }

    private PointInTimeTrigger getRetryTrigger() {
        return new PointInTimeTrigger(DateUtil.now().plus(backoffRetryMillis,
                ChronoUnit.MILLIS).toInstant().toEpochMilli(), null, null);
    }

    private CompletionStage<JobDetails> handleExpiredJob(JobDetails scheduledJob) {
        return Optional.of(jobWithStatus(scheduledJob, JobStatus.ERROR))
                //final state, removing the job
                .map(j -> jobRepository
                        .delete(j)
                        .thenApply(deleted -> {
                            unregisterScheduledJob(j);
                            LOGGER.warn("Retry limit exceeded for job{}", j);
                            return j;
                        }))
                .orElse(null);
    }

    private PublisherBuilder<ManageableJobHandle> scheduleRegistering(JobDetails job, Optional<Trigger> trigger) {
        return doSchedule(job, trigger)
                .peek(registerScheduledJob(job));
    }

    private Consumer<JobHandle> registerScheduledJob(JobDetails job) {
        return s -> schedulerControl.put(job.getId(), DateUtil.now());
    }

    public abstract PublisherBuilder<ManageableJobHandle> doSchedule(JobDetails job, Optional<Trigger> trigger);

    private ZonedDateTime unregisterScheduledJob(JobDetails job) {
        return schedulerControl.remove(job.getId());
    }

    public CompletionStage<JobDetails> cancel(CompletionStage<JobDetails> futureJob) {
        return Uni.createFrom().completionStage(futureJob)
                .onItem().invoke(job -> LOGGER.debug("Cancel Job Scheduling {}", job))
                .chain(scheduledJob -> Optional.ofNullable(scheduledJob.getScheduledId())
                        .map(id -> Uni.createFrom().publisher(this.doCancel(scheduledJob))
                                .onItem().transform(b -> scheduledJob))
                        .orElse(Uni.createFrom().item(scheduledJob)))
                //final state, removing the job
                .chain(job -> Uni.createFrom().completionStage(jobRepository.delete(job)))
                .onItem().invoke(this::unregisterScheduledJob)
                .convert().toCompletionStage();
    }

    @Override
    public CompletionStage<JobDetails> cancel(String jobId) {
        return cancel(jobRepository
                .get(jobId)
                .thenApply(scheduledJob -> Optional
                        .ofNullable(scheduledJob)
                        .map(j -> jobWithStatus(j, JobStatus.CANCELED))
                        .orElse(null)));
    }

    public abstract Publisher<ManageableJobHandle> doCancel(JobDetails scheduledJob);

    @Override
    public Optional<ZonedDateTime> scheduled(String jobId) {
        return Optional.ofNullable(schedulerControl.get(jobId));
    }

    public void setForceExecuteExpiredJobs(boolean forceExecuteExpiredJobs) {
        this.forceExecuteExpiredJobs = Optional.of(forceExecuteExpiredJobs);
    }
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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

import static mutiny.zero.flow.adapters.AdaptersToFlow.publisher;
import static org.kie.kogito.jobs.service.utils.ModelUtil.jobWithStatus;
import static org.kie.kogito.jobs.service.utils.ModelUtil.jobWithStatusAndHandle;

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
    boolean forceExecuteExpiredJobs;

    /**
     * Flag to allow that jobs that might have overdue during an eventual service shutdown should be fired at the
     * next service start.
     */
    boolean forceExecuteExpiredJobsOnServiceStart;

    /**
     * The current chunk size in minutes the scheduler handles, it is used to keep a limit number of jobs scheduled
     * in the in-memory scheduler.
     */
    long schedulerChunkInMinutes;

    private ReactiveJobRepository jobRepository;

    private final Map<String, SchedulerControlRecord> schedulerControl;

    protected static class SchedulerControlRecord {
        private final String jobId;
        private final long handleId;
        private final ZonedDateTime scheduledTime;

        public SchedulerControlRecord(String jobId, long handleId, ZonedDateTime scheduledTime) {
            this.jobId = jobId;
            this.handleId = handleId;
            this.scheduledTime = scheduledTime;
        }

        public String getJobId() {
            return jobId;
        }

        public long getHandleId() {
            return handleId;
        }

        public ZonedDateTime getScheduledTime() {
            return scheduledTime;
        }
    }

    protected BaseTimerJobScheduler() {
        this(null, 0, 0, 0, true, true);
    }

    protected BaseTimerJobScheduler(ReactiveJobRepository jobRepository,
            long backoffRetryMillis,
            long maxIntervalLimitToRetryMillis,
            long schedulerChunkInMinutes,
            boolean forceExecuteExpiredJobs,
            boolean forceExecuteExpiredJobsOnServiceStart) {
        this.jobRepository = jobRepository;
        this.backoffRetryMillis = backoffRetryMillis;
        this.maxIntervalLimitToRetryMillis = maxIntervalLimitToRetryMillis;
        this.schedulerControl = new ConcurrentHashMap<>();
        this.schedulerChunkInMinutes = schedulerChunkInMinutes;
        this.forceExecuteExpiredJobs = forceExecuteExpiredJobs;
        this.forceExecuteExpiredJobsOnServiceStart = forceExecuteExpiredJobsOnServiceStart;
    }

    /**
     * Executed from the API to reflect client invocations.
     */
    @Override
    public Publisher<JobDetails> schedule(JobDetails job) {
        LOGGER.debug("Scheduling job: {}", job);
        return ReactiveStreams
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? handleExistingJob(job)
                        : ReactiveStreams.of(job))
                .flatMap(handled -> isOnCurrentSchedulerChunk(job)
                        // in case the job is on the current bulk, proceed with scheduling process.
                        ? doJobScheduling(job)
                        // in case the job is not on the current bulk, just save it to be scheduled later.
                        : ReactiveStreams.fromCompletionStage(jobRepository.save(jobWithStatus(job, JobStatus.SCHEDULED))))
                .buildRs();
    }

    /**
     * Internal use, executed by the periodic loader only. Jobs processed by this method belongs to the current chunk.
     */
    @Override
    public Publisher<JobDetails> internalSchedule(JobDetails job, boolean onServiceStart) {
        LOGGER.debug("Internal Scheduling, onServiceStart: {}, job: {}", onServiceStart, job);
        return ReactiveStreams
                .fromCompletionStage(jobRepository.exists(job.getId()))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? handleInternalSchedule(job, onServiceStart)
                        : handleInternalScheduleDeletedJob(job))
                .buildRs();
    }

    @Override
    public PublisherBuilder<JobDetails> reschedule(String id, Trigger trigger) {
        return ReactiveStreams.fromCompletionStageNullable(jobRepository.merge(id, JobDetails.builder().trigger(trigger).build()))
                .peek(this::doCancel)
                .map(this::schedule)
                .flatMapRsPublisher(j -> j);
    }

    /**
     * Performs the given job scheduling process on the scheduler, after all the validations already made.
     */
    private PublisherBuilder<JobDetails> doJobScheduling(JobDetails job) {
        return ReactiveStreams.of(job)
                //calculate the delay (when the job should be executed)
                .map(current -> job.getTrigger().hasNextFireTime())
                .map(DateUtil::fromDate)
                .map(this::calculateDelay)
                .peek(delay -> Optional
                        .of(delay.isNegative())
                        .filter(Boolean.FALSE::equals)
                        .orElseThrow(() -> new InvalidScheduleTimeException(
                                String.format("The expirationTime: %s, for job: %s should be greater than current time: %s.",
                                        job.getTrigger().hasNextFireTime(), job.getId(), ZonedDateTime.now()))))
                .flatMap(delay -> ReactiveStreams.fromCompletionStage(jobRepository.save(jobWithStatus(job, JobStatus.SCHEDULED))))
                //schedule the job in the scheduler
                .flatMap(j -> scheduleRegistering(job, job.getTrigger()))
                .map(handle -> jobWithStatusAndHandle(job, JobStatus.SCHEDULED, handle))
                .map(scheduledJob -> jobRepository.save(scheduledJob))
                .flatMapCompletionStage(p -> p);
    }

    /**
     * Check if the job should be scheduled on the current chunk or saved to be scheduled later.
     */
    private boolean isOnCurrentSchedulerChunk(JobDetails job) {
        return DateUtil.fromDate(job.getTrigger().hasNextFireTime()).isBefore(DateUtil.now().plusMinutes(schedulerChunkInMinutes));
    }

    private PublisherBuilder<JobDetails> handleExistingJob(JobDetails job) {
        return ReactiveStreams.fromCompletionStage(jobRepository.get(job.getId()))
                .flatMap(
                        currentJob -> {
                            switch (currentJob.getStatus()) {
                                case SCHEDULED:
                                case RETRY:
                                    // cancel the job.
                                    return ReactiveStreams.fromCompletionStage(
                                            cancel(CompletableFuture.completedFuture(jobWithStatus(currentJob, JobStatus.CANCELED))));
                                default:
                                    // uncommon, break the stream processing
                                    return ReactiveStreams.empty();
                            }
                        })
                .onErrorResumeWith(t -> ReactiveStreams.empty());
    }

    private PublisherBuilder<JobDetails> handleInternalSchedule(JobDetails job, boolean onStart) {
        unregisterScheduledJob(job);
        switch (job.getStatus()) {
            case SCHEDULED:
                Duration delay = calculateRawDelay(DateUtil.fromDate(job.getTrigger().hasNextFireTime()));
                if (delay.isNegative() && onStart && !forceExecuteExpiredJobsOnServiceStart) {
                    return ReactiveStreams.fromCompletionStage(handleExpiredJob(job));
                } else {
                    // other cases of potential overdue are because of slow processing of the jobs service, or the user
                    // configured to fire overdue triggers at service startup. Always schedule.
                    PublisherBuilder<JobDetails> preSchedule;
                    if (job.getScheduledId() != null) {
                        // cancel the existing timer if any.
                        preSchedule = ReactiveStreams.fromPublisher(doCancel(job)).flatMap(jobHandle -> ReactiveStreams.of(job));
                    } else {
                        preSchedule = ReactiveStreams.of(job);
                    }
                    return preSchedule.flatMap(j -> scheduleRegistering(job, job.getTrigger()))
                            .map(handle -> jobWithStatusAndHandle(job, JobStatus.SCHEDULED, handle))
                            .map(scheduledJob -> jobRepository.save(scheduledJob))
                            .flatMapCompletionStage(p -> p);
                }
            case RETRY:
                return handleRetry(CompletableFuture.completedFuture(job));
            default:
                // by definition there are no more cases, only SCHEDULED and RETRY cases are picked by the loader.
                return ReactiveStreams.of(job);
        }
    }

    private PublisherBuilder<JobDetails> handleInternalScheduleDeletedJob(JobDetails job) {
        LOGGER.warn("Job was removed from database: {}.", job);
        return ReactiveStreams.of(job);
    }

    private Duration calculateDelay(ZonedDateTime expirationTime) {
        Duration delay = Duration.between(DateUtil.now(), expirationTime);
        if (!delay.isNegative()) {
            return delay;
        }
        //in case forceExecuteExpiredJobs is true, execute the job immediately.
        return forceExecuteExpiredJobs ? Duration.ofSeconds(1) : Duration.ofSeconds(-1);
    }

    private Duration calculateRawDelay(ZonedDateTime expirationTime) {
        return Duration.between(DateUtil.now(), expirationTime);
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
                        .map(time -> doJobScheduling(job))
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
                        .map(s -> scheduleRegistering(scheduledJob, getRetryTrigger()))
                        .flatMap(p -> p)
                        .map(registeredJobHandle -> JobDetails.builder()
                                .of(jobWithStatusAndHandle(scheduledJob, JobStatus.RETRY, registeredJobHandle))
                                .incrementRetries()
                                .build())
                        .map(jobRepository::save)
                        .flatMapCompletionStage(p -> p))
                .peek(job -> LOGGER.debug("Retry executed {}", job))
                .onError(errorHandler -> LOGGER.error("Failed to retrieve job due to {}", errorHandler.getMessage()));
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

    private PublisherBuilder<ManageableJobHandle> scheduleRegistering(JobDetails job, Trigger trigger) {
        return doSchedule(job, trigger)
                .peek(registerScheduledJob(job));
    }

    protected Consumer<JobHandle> registerScheduledJob(JobDetails job) {
        return handle -> schedulerControl.put(job.getId(), new SchedulerControlRecord(job.getId(), handle.getId(), DateUtil.now()));
    }

    public abstract PublisherBuilder<ManageableJobHandle> doSchedule(JobDetails job, Trigger trigger);

    protected SchedulerControlRecord unregisterScheduledJob(JobDetails job) {
        return schedulerControl.remove(job.getId());
    }

    protected Collection<SchedulerControlRecord> getScheduledJobs() {
        return new ArrayList<>(schedulerControl.values());
    }

    public CompletionStage<JobDetails> cancel(CompletionStage<JobDetails> futureJob) {
        return Uni.createFrom().completionStage(futureJob)
                .onItem().invoke(job -> LOGGER.debug("Cancel Job Scheduling {}", job))
                .chain(scheduledJob -> Optional.ofNullable(scheduledJob.getScheduledId())
                        .map(id -> Uni.createFrom().publisher(publisher(this.doCancel(scheduledJob)))
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
        SchedulerControlRecord record = schedulerControl.get(jobId);
        return Optional.ofNullable(record != null ? record.getScheduledTime() : null);
    }

    public void setForceExecuteExpiredJobs(boolean forceExecuteExpiredJobs) {
        this.forceExecuteExpiredJobs = forceExecuteExpiredJobs;
    }
}

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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.JobBuilder;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.reactivestreams.Publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.model.JobStatus.CANCELED;
import static org.kie.kogito.jobs.service.model.JobStatus.SCHEDULED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseTimerJobSchedulerTest {

    public static final String JOB_ID = UUID.randomUUID().toString();
    public static final String SCHEDULED_ID = "3";

    @Mock
    public JobExecutor jobExecutor;

    @Mock
    public ReactiveJobRepository jobRepository;

    public CompletionStage<ScheduledJob> scheduled;

    @Captor
    private ArgumentCaptor<Duration> delayCaptor;

    @Captor
    private ArgumentCaptor<ScheduledJob> scheduleCaptor;

    @Captor
    private ArgumentCaptor<CompletionStage<ScheduledJob>> scheduleCaptorFuture;

    public Job job;

    public ScheduledJob scheduledJob;

    public JobExecutionResponse errorResponse;

    public ZonedDateTime expirationTime;

    @BeforeEach
    public void setUp() {
        tested().schedulerChunkInMinutes = 5;
        tested().forceExecuteExpiredJobs = Optional.of(Boolean.FALSE);
        //expiration on the current scheduler chunk
        expirationTime = DateUtil.now().plusMinutes(tested().schedulerChunkInMinutes - 1);

        job = JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(expirationTime)
                .build();

        scheduledJob = ScheduledJob.builder().job(job).status(SCHEDULED).build();
        scheduled = CompletableFuture.completedFuture(scheduledJob);
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduled);
        lenient().when(jobExecutor.execute(any())).thenReturn(scheduled);

        errorResponse = JobExecutionResponse.builder()
                .jobId(JOB_ID)
                .message("error")
                .now()
                .build();
    }

    public abstract BaseTimerJobScheduler tested();

    @Test
    void testScheduleNotExistingJob() {
        when(jobRepository.exists(JOB_ID)).thenReturn(CompletableFuture.completedFuture(false));
        Publisher<ScheduledJob> schedule = tested().schedule(job);
        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(job));
        subscribeOn(schedule);
        verify(tested()).doSchedule(delayCaptor.capture(), eq(job));
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob scheduledJob = scheduleCaptor.getValue();
        assertThat(scheduledJob.getScheduledId()).isEqualTo(SCHEDULED_ID);
        assertThat(scheduledJob.getId()).isEqualTo(JOB_ID);
        assertThat(scheduledJob.getStatus()).isEqualTo(SCHEDULED);
    }

    @Test
    void testScheduleExistingJob() {
        testExistingJob(false, SCHEDULED);
    }

    @Test
    void testScheduleExistingJobExpired() {
        testExistingJob(true, SCHEDULED);
    }

    private void testExistingJob(boolean expired, JobStatus jobStatus) {
        job = Optional
                .of(expired)
                .filter(Boolean.TRUE::equals)
                .map(e -> JobBuilder
                        .builder()
                        .id(JOB_ID)
                        .expirationTime(DateUtil.now().minusDays(1))
                        .build())
                .orElse(job);

        scheduledJob = ScheduledJob.builder().status(jobStatus).job(job).build();

        when(jobRepository.exists(JOB_ID)).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<ScheduledJob> scheduledJobCompletableFuture = CompletableFuture.completedFuture(scheduledJob);

        lenient().when(jobRepository.delete(JOB_ID)).thenReturn(scheduledJobCompletableFuture);
        lenient().when(jobRepository.delete(any(ScheduledJob.class))).thenReturn(scheduledJobCompletableFuture);
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduledJobCompletableFuture);

        Publisher<ScheduledJob> schedule = tested().schedule(job);

        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(job));

        subscribeOn(schedule);

        verify(jobRepository, expired || SCHEDULED.equals(jobStatus) ? atLeastOnce() : never()).delete(any(ScheduledJob.class));
        verify(tested(), expired ? never() : times(1)).doSchedule(delayCaptor.capture(), eq(job));
        verify(jobRepository, expired ? never() : times(1)).save(scheduleCaptor.capture());

        //assert always a scheduled job is canceled (periodic or not)
        Optional.ofNullable(jobStatus)
                .filter(SCHEDULED::equals)
                .ifPresent(s -> {
                    verify(tested()).cancel(scheduleCaptorFuture.capture());
                    try {
                        ScheduledJob value = scheduleCaptorFuture.getValue().toCompletableFuture().get(1, TimeUnit.MILLISECONDS);
                        assertThat(value.getId()).isEqualTo(scheduledJob.getId());
                        assertThat(value.getStatus()).isEqualTo(CANCELED);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        if (!expired) {
            ScheduledJob returnedScheduledJob = scheduleCaptor.getValue();
            assertThat(returnedScheduledJob.getScheduledId()).isEqualTo(SCHEDULED_ID);
            assertThat(returnedScheduledJob.getId()).isEqualTo(JOB_ID);
            assertThat(returnedScheduledJob.getStatus()).isEqualTo(scheduledJob.getStatus());
        }
    }

    @Test
    void testScheduleExistingJobRetryExpired() {
        testExistingJob(true, JobStatus.RETRY);
    }

    @Test
    void testScheduleExistingJobRetry() {
        testExistingJob(false, JobStatus.RETRY);
    }

    @Test
    void testScheduleExistingJobPeriodic() {
        job = createPeriodicJob();
        testExistingJob(false, SCHEDULED);
    }

    @Test
    void testHandleJobExecutionSuccess() {
        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(tested()).cancel(scheduleCaptorFuture.capture());
    }

    @Test
    void testHandleJobExecutionSuccessPeriodicFirstExecution() {
        job = createPeriodicJob();

        scheduledJob = ScheduledJob.builder().job(job).status(SCHEDULED).build();

        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(tested()).doPeriodicSchedule(delayCaptor.capture(), scheduleCaptor.capture());
    }

    private Job createPeriodicJob() {
        return JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(expirationTime)
                .repeatLimit(10)
                .repeatInterval(2l)
                .build();
    }

    @Test
    void testHandleJobExecutionSuccessPeriodic() {
        job = createPeriodicJob();

        scheduledJob = ScheduledJob.builder().job(job).status(SCHEDULED).build();

        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob scheduleCaptorValue = scheduleCaptor.getValue();
        assertThat(scheduleCaptorValue.getStatus()).isEqualTo(SCHEDULED);
        assertThat(scheduleCaptorValue.getExecutionCounter()).isEqualTo(1);
    }

    @Test
    void testHandleJobExecutionErrorWithRetry() {
        PublisherBuilder<ScheduledJob> scheduledJobPublisher = tested().handleJobExecutionError(errorResponse);

        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(scheduledJob));
        subscribeOn(scheduledJobPublisher.buildRs());
        verify(tested()).doSchedule(delayCaptor.capture(), eq(scheduledJob));

        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob saved = scheduleCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(JobStatus.RETRY);
    }

    @Test
    void testHandleJobExecutionErrorFinal() {
        scheduledJob = ScheduledJob.builder().of(scheduledJob).status(JobStatus.ERROR).build();
        when(jobRepository.get(JOB_ID)).thenReturn(CompletableFuture.completedFuture(scheduledJob));

        PublisherBuilder<ScheduledJob> scheduledJobPublisher = tested().handleJobExecutionError(errorResponse);

        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(scheduledJob));
        subscribeOn(scheduledJobPublisher.buildRs());
        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(scheduledJob));
    }

    protected <T> Consumer<T> dummyCallback() {
        return t -> {
        };
    }

    @Test
    void testExecute() {
        tested().execute(job);
        verify(jobRepository).get(JOB_ID);
        verify(jobExecutor).execute(scheduled);
    }

    @Test
    void testCancel() {
        tested().cancel(JOB_ID);
        verify(jobRepository).get(JOB_ID);
        verify(tested()).cancel(scheduleCaptorFuture.capture());
        scheduleCaptorFuture.getValue()
                .thenAccept(j -> assertThat(j.getStatus()).isEqualTo(JobStatus.CANCELED));
    }

    @Test
    void testCancelScheduledJob() {
        scheduledJob = ScheduledJob.builder().job(job).status(SCHEDULED).scheduledId("1").build();
        when(tested().doCancel(scheduledJob)).thenReturn(ReactiveStreams.of(true).buildRs());

        tested().cancel(CompletableFuture.completedFuture(scheduledJob));
        verify(tested()).doCancel(scheduledJob);
        verify(jobRepository).delete(scheduledJob);
    }

    @Test
    void testCancelNotScheduledJob() {
        tested().cancel(scheduled);
        verify(tested(), never()).doCancel(scheduledJob);
        verify(jobRepository).delete(scheduledJob);
    }

    @Test
    void testScheduleOutOfCurrentChunk() {
        expirationTime = DateUtil.now().plusMinutes(tested().schedulerChunkInMinutes + 10);

        job = JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(expirationTime)
                .build();

        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        subscribeOn(tested().schedule(job));

        verify(tested(), never()).doSchedule(any(Duration.class), eq(job));
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob current = scheduleCaptor.getValue();
        assertThat(current.getId()).isEqualTo(JOB_ID);
        assertThat(current.getStatus()).isEqualTo(SCHEDULED);
        assertThat(current.getScheduledId()).isNull();
    }

    @Test
    void testScheduleInCurrentChunk() {
        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        subscribeOn(tested().schedule(job));

        verify(tested()).doSchedule(any(Duration.class), eq(job));
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob current = scheduleCaptor.getValue();
        assertThat(current.getId()).isEqualTo(JOB_ID);
        assertThat(current.getStatus()).isEqualTo(SCHEDULED);
        assertThat(current.getScheduledId()).isNotNull();
    }

    @Test
    void testScheduled() {
        testExistingJob(false, SCHEDULED);
        Optional<ZonedDateTime> scheduled = tested().scheduled(JOB_ID);
        assertThat(scheduled).isNotNull();
        assertThat(scheduled.isPresent()).isTrue();
    }

    private Disposable subscribeOn(Publisher<ScheduledJob> schedule) {
        return Flowable.fromPublisher(schedule).subscribe(dummyCallback(), dummyCallback());
    }

    @Test
    void testForceExpiredJobToBeExecuted() {
        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        job = JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(DateUtil.now().minusHours(1))
                .repeatLimit(1)
                .repeatInterval(1l)
                .build();

        //testing with forcing disabled
        subscribeOn(tested().schedule(job));
        verify(tested(), never()).doSchedule(any(Duration.class), eq(job));

        //testing with forcing enabled
        tested().forceExecuteExpiredJobs = Optional.of(Boolean.TRUE);
        subscribeOn(tested().schedule(job));
        verify(tested(), times(1)).doSchedule(any(Duration.class), eq(job));
    }
}
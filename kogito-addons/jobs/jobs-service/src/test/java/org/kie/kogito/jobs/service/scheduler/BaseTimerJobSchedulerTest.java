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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.jetbrains.annotations.NotNull;
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
import static org.mockito.ArgumentMatchers.eq;
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

    @BeforeEach
    public void setUp() {

        job = JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(DateUtil.now().plusMinutes(10))
                .build();

        scheduledJob = ScheduledJob.builder().job(job).status(JobStatus.SCHEDULED).build();
        scheduled = CompletableFuture.completedFuture(scheduledJob);
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduled);

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
        Flowable.fromPublisher(schedule).subscribe(dummyCallback(), dummyCallback());
        verify(tested()).doSchedule(delayCaptor.capture(), eq(job));
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob scheduledJob = scheduleCaptor.getValue();
        assertThat(scheduledJob.getScheduledId()).isEqualTo(SCHEDULED_ID);
        assertThat(scheduledJob.getId()).isEqualTo(JOB_ID);
        assertThat(scheduledJob.getStatus()).isEqualTo(JobStatus.SCHEDULED);
    }

    @Test
    void testScheduleExistingJob() {
        testExistingJob(false, JobStatus.SCHEDULED);
    }

    @Test
    void testScheduleExistingJobExpired() {
        testExistingJob(true, JobStatus.SCHEDULED);
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
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduledJobCompletableFuture);

        Publisher<ScheduledJob> schedule = tested().schedule(job);

        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(job));

        Flowable.fromPublisher(schedule).subscribe(dummyCallback(), dummyCallback());

        verify(jobRepository, expired ? times(1) : never()).delete(JOB_ID);
        verify(tested(), expired ? never() : times(1)).doSchedule(delayCaptor.capture(), eq(job));
        verify(jobRepository, expired ? never() : times(1)).save(scheduleCaptor.capture());

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
        testExistingJob(false, JobStatus.SCHEDULED);
    }

    @Test
    void testHandleJobExecutionSuccess() {
        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        Flowable.fromPublisher(executionSuccess.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(tested()).cancel(scheduleCaptorFuture.capture());
    }

    @Test
    void testHandleJobExecutionSuccessPeriodicFirstExecution() {
        job = createPeriodicJob();

        scheduledJob = ScheduledJob.builder().job(job).status(JobStatus.SCHEDULED).build();

        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        Flowable.fromPublisher(executionSuccess.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(tested()).doPeriodicSchedule(delayCaptor.capture(), scheduleCaptor.capture());
    }

    private Job createPeriodicJob() {
        return JobBuilder.builder()
                .id(JOB_ID)
                .expirationTime(DateUtil.now().plusMinutes(10))
                .repeatLimit(10)
                .repeatInterval(2l)
                .build();
    }

    @Test
    void testHandleJobExecutionSuccessPeriodic() {
        job = createPeriodicJob();

        scheduledJob = ScheduledJob.builder().job(job).status(JobStatus.PERIODIC_SCHEDULED).build();

        PublisherBuilder<ScheduledJob> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        Flowable.fromPublisher(executionSuccess.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(jobRepository).save(scheduleCaptor.capture());
        ScheduledJob scheduleCaptorValue = scheduleCaptor.getValue();
        assertThat(scheduleCaptorValue.getStatus()).isEqualTo(JobStatus.PERIODIC_SCHEDULED);
        assertThat(scheduleCaptorValue.getExecutionCounter()).isEqualTo(2);
    }

    @Test
    void testHandleJobExecutionErrorWithRetry() {
        PublisherBuilder<ScheduledJob> scheduledJobPublisher = tested().handleJobExecutionError(errorResponse);

        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(scheduledJob));
        Flowable.fromPublisher(scheduledJobPublisher.buildRs()).subscribe(dummyCallback(), dummyCallback());
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
        Flowable.fromPublisher(scheduledJobPublisher.buildRs()).subscribe(dummyCallback(), dummyCallback());
        verify(tested(), never()).doSchedule(delayCaptor.capture(), eq(scheduledJob));
    }

    @NotNull
    private <T> Consumer<T> dummyCallback() {
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
        verify(tested()).cancel(scheduled);
    }

    @Test
    void testCancelScheduledJob() {
        when(tested().doCancel(scheduledJob)).thenReturn(ReactiveStreams.of(true).buildRs());

        tested().cancel(scheduled);
        verify(tested()).doCancel(scheduledJob);
        verify(jobRepository).delete(JOB_ID);
    }
}
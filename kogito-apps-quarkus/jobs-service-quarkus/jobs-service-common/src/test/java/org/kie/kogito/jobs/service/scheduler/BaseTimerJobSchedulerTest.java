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

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.exception.JobServiceException;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ManageableJobHandle;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.reactivestreams.Publisher;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import static mutiny.zero.flow.adapters.AdaptersToFlow.publisher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.jobs.service.model.JobStatus.CANCELED;
import static org.kie.kogito.jobs.service.model.JobStatus.SCHEDULED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("java:S5786")
public abstract class BaseTimerJobSchedulerTest {

    public static final String JOB_ID = UUID.randomUUID().toString();
    public static final String SCHEDULED_ID = "3";

    @Mock
    public JobExecutor jobExecutor;

    @Mock
    public ReactiveJobRepository jobRepository;

    public CompletionStage<JobDetails> scheduled;

    @Captor
    private ArgumentCaptor<Trigger> delayCaptor;

    @Captor
    private ArgumentCaptor<JobDetails> scheduleCaptor;

    @Captor
    private ArgumentCaptor<CompletionStage<JobDetails>> scheduleCaptorFuture;

    public JobDetails scheduledJob;

    public JobExecutionResponse errorResponse;

    public JobExecutionResponse successResponse;

    public ZonedDateTime expirationTime;

    public Trigger trigger;

    @BeforeEach
    public void setUp() {
        tested().schedulerChunkInMinutes = 5;
        tested().forceExecuteExpiredJobs = false;
        //expiration on the current scheduler chunk
        expirationTime = DateUtil.now().plusMinutes(tested().schedulerChunkInMinutes - 1);
        errorResponse = JobExecutionResponse.builder()
                .jobId(JOB_ID)
                .message("error")
                .now()
                .build();
        successResponse = JobExecutionResponse.builder()
                .jobId(JOB_ID)
                .message("sucess")
                .now()
                .build();

        trigger = new PointInTimeTrigger(expirationTime.toInstant().toEpochMilli(), null, null);
        scheduledJob = JobDetails.builder().id(JOB_ID).trigger(trigger).status(SCHEDULED).build();
        scheduled = CompletableFuture.completedFuture(scheduledJob);
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduled);
        lenient().when(jobRepository.save(any(JobDetails.class))).thenAnswer(a -> CompletableFuture.completedFuture(a.getArgument(0)));
        lenient().when(jobExecutor.execute(any())).thenReturn(Uni.createFrom().item(successResponse));
    }

    public abstract BaseTimerJobScheduler tested();

    @Test
    void testScheduleNotExistingJob() {
        when(jobRepository.exists(JOB_ID)).thenReturn(CompletableFuture.completedFuture(false));
        Publisher<JobDetails> schedule = tested().schedule(scheduledJob);
        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        subscribeOn(schedule);
        verify(tested()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        verify(jobRepository, times(2)).save(scheduleCaptor.capture());
        JobDetails scheduledJob = scheduleCaptor.getValue();
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
        scheduledJob = Optional
                .of(expired)
                .filter(Boolean.TRUE::equals)
                .map(e -> JobDetails.builder()
                        .status(jobStatus)
                        .id(JOB_ID)
                        .trigger(new PointInTimeTrigger(System.currentTimeMillis() - 1, null, null))
                        .build())
                .orElse(JobDetails.builder().of(scheduledJob).status(jobStatus).build());

        when(jobRepository.exists(JOB_ID)).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<JobDetails> scheduledJobCompletableFuture = CompletableFuture.completedFuture(scheduledJob);

        lenient().when(jobRepository.delete(JOB_ID)).thenReturn(scheduledJobCompletableFuture);
        lenient().when(jobRepository.delete(any(JobDetails.class))).thenReturn(scheduledJobCompletableFuture);
        lenient().when(jobRepository.get(JOB_ID)).thenReturn(scheduledJobCompletableFuture);

        Publisher<JobDetails> schedule = tested().schedule(scheduledJob);

        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());

        subscribeOn(schedule);

        verify(jobRepository, expired || SCHEDULED.equals(jobStatus) ? atLeastOnce() : never()).delete(any(JobDetails.class));
        verify(tested(), expired ? never() : times(1)).doSchedule(eq(scheduledJob), delayCaptor.capture());
        verify(jobRepository, expired ? never() : times(2)).save(scheduleCaptor.capture());

        //assert always a scheduled job is canceled (periodic or not)
        Optional.ofNullable(jobStatus)
                .filter(SCHEDULED::equals)
                .ifPresent(s -> {
                    verify(tested()).cancel(scheduleCaptorFuture.capture());
                    try {
                        JobDetails value = scheduleCaptorFuture.getValue().toCompletableFuture().get(1, TimeUnit.MILLISECONDS);
                        assertThat(value.getId()).isEqualTo(scheduledJob.getId());
                        assertThat(value.getStatus()).isEqualTo(CANCELED);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        if (!expired) {
            JobDetails returnedJobDetails = scheduleCaptor.getValue();
            assertThat(returnedJobDetails.getScheduledId()).isEqualTo(SCHEDULED_ID);
            assertThat(returnedJobDetails.getId()).isEqualTo(JOB_ID);
            assertThat(returnedJobDetails.getStatus()).isEqualTo(scheduledJob.getStatus());
        }
    }

    @Test
    void testScheduleExistingJobRetryExpired() {
        testExistingJob(true, JobStatus.RETRY);
    }

    @Test
    void testScheduleExistingJobRetry() {
        testExistingJob(false, SCHEDULED);
    }

    @Test
    void testScheduleExistingJobPeriodic() {
        scheduledJob = createPeriodicJob();
        testExistingJob(false, SCHEDULED);
    }

    @Test
    void testHandleJobExecutionSuccess() {
        PublisherBuilder<JobDetails> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(tested()).cancel(scheduleCaptorFuture.capture());
    }

    @Test
    void testHandleJobExecutionSuccessPeriodicFirstExecution() {
        scheduledJob = createPeriodicJob();

        PublisherBuilder<JobDetails> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(tested()).doSchedule(scheduleCaptor.capture(), delayCaptor.capture());
    }

    private JobDetails createPeriodicJob() {
        return JobDetails.builder()
                .id(JOB_ID)
                .trigger(new SimpleTimerTrigger(DateUtil.toDate(expirationTime.toOffsetDateTime()), 1, ChronoUnit.MILLIS, 10, null))
                .status(SCHEDULED)
                .build();
    }

    @Test
    void testHandleJobExecutionSuccessPeriodic() {
        scheduledJob = createPeriodicJob();

        PublisherBuilder<JobDetails> executionSuccess = tested().handleJobExecutionSuccess(scheduledJob);
        verify(tested(), never()).cancel(scheduleCaptorFuture.capture());

        subscribeOn(executionSuccess.buildRs());
        verify(jobRepository, times(3)).save(scheduleCaptor.capture());
        JobDetails scheduleCaptorValue = scheduleCaptor.getValue();
        assertThat(scheduleCaptorValue.getStatus()).isEqualTo(SCHEDULED);
        assertThat(scheduleCaptorValue.getExecutionCounter()).isEqualTo(1);
    }

    @Test
    void testHandleJobExecutionErrorWithRetry() {
        PublisherBuilder<JobDetails> scheduledJobPublisher = tested().handleJobExecutionError(errorResponse);

        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        subscribeOn(scheduledJobPublisher.buildRs());
        verify(tested()).doSchedule(eq(scheduledJob), delayCaptor.capture());

        verify(jobRepository).save(scheduleCaptor.capture());
        JobDetails saved = scheduleCaptor.getValue();
        assertThat(saved.getStatus()).isEqualTo(JobStatus.RETRY);
    }

    @Test
    void testHandleJobExecutionErrorFinal() {
        scheduledJob = JobDetails.builder().of(scheduledJob).status(JobStatus.ERROR).build();
        when(jobRepository.get(JOB_ID)).thenReturn(CompletableFuture.completedFuture(scheduledJob));

        PublisherBuilder<JobDetails> scheduledJobPublisher = tested().handleJobExecutionError(errorResponse);

        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        subscribeOn(scheduledJobPublisher.buildRs());
        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());
    }

    protected <T> Consumer<T> dummyCallback() {
        return t -> {
        };
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
    void testCancelJobDetails() {
        scheduledJob = JobDetails.builder().of(scheduledJob).status(SCHEDULED).scheduledId("1").build();
        when(tested().doCancel(scheduledJob)).thenReturn(ReactiveStreams.of(new ManageableJobHandle(true)).buildRs());

        tested().cancel(CompletableFuture.completedFuture(scheduledJob));
        verify(tested()).doCancel(scheduledJob);
        verify(jobRepository).delete(scheduledJob);
    }

    @Test
    void testCancelNotJobDetails() {
        tested().cancel(scheduled);
        verify(tested(), never()).doCancel(scheduledJob);
        verify(jobRepository).delete(scheduledJob);
    }

    @Test
    void testScheduleOutOfCurrentChunk() {
        expirationTime = DateUtil.now().plusMinutes(tested().schedulerChunkInMinutes + 10);

        scheduledJob = JobDetails.builder()
                .of(scheduledJob)
                .trigger(new PointInTimeTrigger(expirationTime.toInstant().toEpochMilli(), null, null))
                .build();

        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        subscribeOn(tested().schedule(scheduledJob));

        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        verify(jobRepository).save(scheduleCaptor.capture());
        JobDetails current = scheduleCaptor.getValue();
        assertThat(current.getId()).isEqualTo(JOB_ID);
        assertThat(current.getStatus()).isEqualTo(SCHEDULED);
        assertThat(current.getScheduledId()).isNull();
    }

    @Test
    void testScheduleInCurrentChunk() {
        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        subscribeOn(tested().schedule(scheduledJob));

        verify(tested()).doSchedule(eq(scheduledJob), delayCaptor.capture());
        verify(jobRepository, times(2)).save(scheduleCaptor.capture());
        JobDetails current = scheduleCaptor.getValue();
        assertThat(current.getId()).isEqualTo(JOB_ID);
        assertThat(current.getStatus()).isEqualTo(SCHEDULED);
        assertThat(current.getScheduledId()).isNotNull();
    }

    @Test
    void testScheduled() {
        testExistingJob(false, SCHEDULED);
        Optional<ZonedDateTime> scheduled = tested().scheduled(JOB_ID);
        assertThat(scheduled).isNotNull().isPresent();
    }

    private void subscribeOn(Publisher<JobDetails> schedule) {
        Multi.createFrom()
                .publisher(publisher(schedule))
                .subscribe()
                .with(dummyCallback(), dummyCallback());
    }

    @Test
    void testForceExpiredJobToBeExecuted() {
        when(jobRepository.exists(any())).thenReturn(CompletableFuture.completedFuture(Boolean.FALSE));

        scheduledJob = JobDetails.builder()
                .of(scheduledJob)
                .trigger(new SimpleTimerTrigger(DateUtil.toDate(OffsetDateTime.now().minusHours(1)), 1, ChronoUnit.MILLIS, 0, null))
                .build();

        //testing with forcing disabled
        subscribeOn(tested().schedule(scheduledJob));
        verify(tested(), never()).doSchedule(eq(scheduledJob), delayCaptor.capture());

        //testing with forcing enabled
        tested().forceExecuteExpiredJobs = true;
        subscribeOn(tested().schedule(scheduledJob));
        verify(tested(), times(1)).doSchedule(eq(scheduledJob), delayCaptor.capture());
    }

    @Test
    void testRescheduleAndMerge() {
        ZonedDateTime newTime = DateUtil.now().plusMinutes(1);
        PointInTimeTrigger newTrigger = new PointInTimeTrigger(newTime.toInstant().toEpochMilli(), null, null);
        JobDetails jobToMerge =
                JobDetails.builder()
                        .trigger(newTrigger)
                        .build();
        JobDetails merged = JobDetails.builder().of(scheduledJob).merge(jobToMerge).build();
        when(jobRepository.merge(JOB_ID, jobToMerge)).thenReturn(CompletableFuture.completedFuture(merged));
        subscribeOn(tested().reschedule(JOB_ID, newTrigger).buildRs());
        verify(tested()).doCancel(merged);
        verify(tested()).schedule(merged);
    }

    @Test
    void handleJobExecutionSuccess() throws Exception {
        scheduledJob = JobDetails.builder().id(JOB_ID).trigger(trigger).status(SCHEDULED).build();
        doReturn(CompletableFuture.completedFuture(scheduledJob)).when(jobRepository).get(JOB_ID);
        doReturn(CompletableFuture.completedFuture(scheduledJob)).when(jobRepository).delete(any(JobDetails.class));
        JobExecutionResponse response = new JobExecutionResponse("execution successful", "200", ZonedDateTime.now(), JOB_ID);

        Optional<JobDetails> result = tested().handleJobExecutionSuccess(response)
                .findFirst()
                .run()
                .toCompletableFuture()
                .get();

        verify(jobRepository).delete(scheduleCaptor.capture());
        JobDetails deletedJob = scheduleCaptor.getValue();
        assertThat(deletedJob).isNotNull();
        assertThat(deletedJob.getId()).isEqualTo(JOB_ID);
        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(JOB_ID);
    }

    @Test
    void handleJobExecutionSuccessJobNotFound() {
        scheduledJob = JobDetails.builder().id(JOB_ID).trigger(trigger).status(SCHEDULED).build();
        doReturn(CompletableFuture.completedFuture(null)).when(jobRepository).get(JOB_ID);
        JobExecutionResponse response = new JobExecutionResponse("execution successful", "200", ZonedDateTime.now(), JOB_ID);

        assertThatThrownBy(() -> tested().handleJobExecutionSuccess(response)
                .findFirst()
                .run()
                .toCompletableFuture()
                .get())
                        .hasCauseInstanceOf(JobServiceException.class)
                        .hasMessageContaining("Job: %s was not found in database.", JOB_ID);
        verify(jobRepository, never()).delete(JOB_ID);
        verify(jobRepository, never()).delete(any(JobDetails.class));
        verify(jobRepository, never()).save(any());
    }
}

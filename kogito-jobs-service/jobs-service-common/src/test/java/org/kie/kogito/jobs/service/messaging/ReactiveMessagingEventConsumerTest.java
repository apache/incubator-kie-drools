/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.messaging;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.exception.JobServiceException;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.job.JobDetails;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactiveMessagingEventConsumerTest {

    private static final String JOB_ID = "JOB_ID";
    private static final String INTERNAL_ERROR = "Internal error";
    private static final String JOB_QUERY_ERROR = "Job query error";

    @Mock
    private TimerDelegateJobScheduler scheduler;

    @Mock
    private ReactiveJobRepository jobRepository;

    @Mock
    private Message<JobCloudEvent<?>> message;

    private ReactiveMessagingEventConsumer eventConsumer;

    @Captor
    private ArgumentCaptor<Throwable> errorCaptor;

    @BeforeEach
    void setUp() {
        CompletionStage<Void> ackCompletionState = CompletableFuture.completedFuture(null);
        lenient().doReturn(ackCompletionState).when(message).ack();
        CompletionStage<Void> nackCompletionStage = CompletableFuture.completedFuture(null);
        lenient().doReturn(nackCompletionStage).when(message).nack(any());
        eventConsumer = new ReactiveMessagingEventConsumer();
        eventConsumer.scheduler = scheduler;
        eventConsumer.jobRepository = jobRepository;
    }

    @Test
    void onCreateProcessInstanceJobWithNonExistingJobSuccessful() {
        prepareCreateProcessInstanceJobWithExistingJobResult(null);
        executeSuccessfulScheduledJobExecution();
    }

    @Test
    void onCreateProcessInstanceJobWithExistingScheduledJobSuccessful() {
        JobDetails existingJob = JobDetails.builder()
                .id(JOB_ID)
                .status(JobStatus.SCHEDULED)
                .build();
        prepareCreateProcessInstanceJobWithExistingJobResult(existingJob);
        executeSuccessfulScheduledJobExecution();
    }

    @Test
    void onCreateProcessInstanceJobWithExistingRetryJobSuccessful() {
        onCreateProcessInstanceJobExistingNonScheduledSuccessful(JobStatus.RETRY);
    }

    @Test
    void onCreateProcessInstanceJobWithExistingCanceledJobSuccessful() {
        onCreateProcessInstanceJobExistingNonScheduledSuccessful(JobStatus.CANCELED);
    }

    @Test
    void onCreateProcessInstanceJobWithExistingErrorJobSuccessful() {
        onCreateProcessInstanceJobExistingNonScheduledSuccessful(JobStatus.ERROR);
    }

    @Test
    void onCreateProcessInstanceJobWithExistingExecutedJobSuccessful() {
        onCreateProcessInstanceJobExistingNonScheduledSuccessful(JobStatus.EXECUTED);
    }

    @Test
    void onCreateProcessInstanceJobWithJobQueryError() {
        Job job = new Job();
        job.setId(JOB_ID);
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .job(job)
                .build();
        doReturn(event).when(message).getPayload();

        CompletionStage<JobDetails> queryJobStage = CompletableFuture.failedFuture(new Exception(JOB_QUERY_ERROR));
        doReturn(queryJobStage).when(jobRepository).get(JOB_ID);

        executeFailedExecution(JOB_QUERY_ERROR);
        verify(scheduler, never()).schedule(any());
    }

    @Test
    void onCreateProcessInstanceJobWithJobScheduleError() {
        Job job = new Job();
        job.setId(JOB_ID);
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .job(job)
                .build();
        doReturn(event).when(message).getPayload();

        JobDetails existingJob = JobDetails.builder()
                .id(JOB_ID)
                .status(JobStatus.SCHEDULED)
                .build();
        CompletionStage<JobDetails> queryJobStage = CompletableFuture.completedStage(existingJob);
        doReturn(queryJobStage).when(jobRepository).get(JOB_ID);

        CompletionStage<JobDetails> createJobFailingStage = CompletableFuture.failedStage(new Exception(INTERNAL_ERROR));
        Publisher<JobDetails> schedulePublisher = ReactiveStreams.fromCompletionStage(createJobFailingStage).buildRs();
        doReturn(schedulePublisher).when(scheduler).schedule(any());

        executeFailedExecution(INTERNAL_ERROR);
        verify(scheduler).schedule(any());
    }

    private void onCreateProcessInstanceJobExistingNonScheduledSuccessful(JobStatus nonScheduledStatus) {
        JobDetails existingJob = JobDetails.builder()
                .id(JOB_ID)
                .status(nonScheduledStatus)
                .build();
        prepareCreateProcessInstanceJobWithExistingJobResult(existingJob);
        executeSuccessfulNonScheduledJobExecution();
    }

    private void prepareCreateProcessInstanceJobWithExistingJobResult(JobDetails existingJobResult) {
        Job job = new Job();
        job.setId(JOB_ID);
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .job(job)
                .build();
        doReturn(event).when(message).getPayload();

        CompletionStage<JobDetails> queryJobStage = CompletableFuture.completedFuture(existingJobResult);
        doReturn(queryJobStage).when(jobRepository).get(JOB_ID);

        JobDetails createdJob = JobDetails.builder().build();
        Publisher<JobDetails> schedulePublisher = ReactiveStreams.of(createdJob).buildRs();
        lenient().doReturn(schedulePublisher).when(scheduler).schedule(any());
    }

    private void executeSuccessfulScheduledJobExecution() {
        executeSuccessfulExecution();
        verify(scheduler).schedule(any());
    }

    private void executeSuccessfulNonScheduledJobExecution() {
        executeSuccessfulExecution();
        verify(scheduler, never()).schedule(any());
    }

    @Test
    void onCancelJobWithNonExistingJobSuccessful() {
        prepareCancelJobWithExistingJob(null);
        executeSuccessfulCancelJob();
    }

    @Test
    void onCancelJobWithExistingJobSuccessful() {
        JobDetails jobDetails = JobDetails.builder()
                .id(JOB_ID)
                .build();
        prepareCancelJobWithExistingJob(jobDetails);
        executeSuccessfulCancelJob();
    }

    private void prepareCancelJobWithExistingJob(JobDetails existingJob) {
        CancelJobRequestEvent event = CancelJobRequestEvent.builder()
                .jobId(JOB_ID)
                .build();
        doReturn(event).when(message).getPayload();

        CompletionStage<JobDetails> completionStage = CompletableFuture.completedFuture(existingJob);
        doReturn(completionStage).when(scheduler).cancel(JOB_ID);
    }

    private void executeSuccessfulCancelJob() {
        executeSuccessfulExecution();
        verify(scheduler).cancel(JOB_ID);
    }

    @Test
    void onCancelJobWithError() {
        CancelJobRequestEvent event = CancelJobRequestEvent.builder()
                .jobId(JOB_ID)
                .build();
        doReturn(event).when(message).getPayload();

        CompletionStage<JobDetails> completionStage = CompletableFuture.failedFuture(new Exception(INTERNAL_ERROR));
        doReturn(completionStage).when(scheduler).cancel(JOB_ID);

        executeFailedExecution(INTERNAL_ERROR);
        verify(scheduler).cancel(JOB_ID);
    }

    private void executeSuccessfulExecution() {
        eventConsumer.onKogitoServiceRequest(message)
                .subscribe().with(callback -> {
                }, Assertions::fail);
        verify(message).ack();
        verify(message, never()).nack(any());
    }

    private void executeFailedExecution(String withErrorMessage) {
        eventConsumer.onKogitoServiceRequest(message)
                .subscribe().with(callback -> {
                }, Assertions::fail);
        verify(message, never()).ack();
        verify(message).nack(errorCaptor.capture());
        assertThat(errorCaptor.getValue())
                .isNotNull()
                .isInstanceOf(JobServiceException.class)
                .hasMessageContaining(withErrorMessage);
    }
}

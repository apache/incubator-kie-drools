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
package org.kie.kogito.jobs.messaging.quarkus;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.ExpirationTime;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsServiceException;
import org.kie.kogito.jobs.api.JobCallbackPayload;
import org.kie.kogito.jobs.descriptors.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientJsonPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractReactiveMessagingJobsServiceTest<T extends AbstractReactiveMessagingJobsService> {

    protected static final URI SERVICE_URI = URI.create("http://myService.com:8080");
    protected static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    protected static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    protected static final String PROCESS_ID = "PROCESS_ID";
    protected static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    protected static final Integer PRIORITY = 0;
    protected static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    protected static final ExpirationTime EXPIRATION_TIME = ExactExpirationTime.of("2020-03-21T10:15:30+01:00");
    protected static final String TIMER_ID = "TIMER_ID";
    protected static final String JOB_ID = "JOB_ID";
    protected static final String SERIALIZED_EVENT = "SERIALIZED_EVENT";
    protected static final String SERIALIZED_SECOND_EVENT = "SERIALIZED_SECOND_EVENT";
    protected static final String JOB_ID_STRING = "JOB_ID_STRING";
    private static final String CALLBACK_ENDPOINT = SERVICE_URI + "/management/jobs/" + PROCESS_ID
            + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + TIMER_ID;
    protected static final String ERROR = "ERROR";
    protected static final String FATAL_ERROR = "FATAL_ERROR";
    protected static final JsonNode JSON_PAYLOAD = new ObjectMapper().valueToTree(new JobCallbackPayload(JOB_ID));

    protected EmitterMock eventsEmitter;

    @Captor
    protected ArgumentCaptor<Message<String>> messageCaptor;

    @Mock
    protected ObjectMapper objectMapper;

    @Captor
    protected ArgumentCaptor<CreateJobEvent> createEventCaptor;

    @Captor
    protected ArgumentCaptor<DeleteJobEvent> deleteEventCaptor;

    protected T jobsService;

    @BeforeEach
    protected void setUp() {
        this.eventsEmitter = spy(new EmitterMock());
        this.jobsService = createJobsService(SERVICE_URI, objectMapper, eventsEmitter);
    }

    protected abstract T createJobsService(URI serviceUrl, ObjectMapper objectMapper, Emitter<String> eventsEmitter);

    @Test
    protected void scheduleProcessInstanceJobSuccessful() throws Exception {
        JobDescription description = mockProcessInstanceJobDescription();
        CreateJobEvent expectedEvent = mockExpectedCreateJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateJobEvent.class));
        doReturn(JSON_PAYLOAD).when(objectMapper).valueToTree(any(JobCallbackPayload.class));

        jobsService.scheduleJob(description);

        verifyCreateJobEventWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFailure() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateJobEvent expectedEvent = mockExpectedCreateJobEvent();
        doReturn(JSON_PAYLOAD).when(objectMapper).valueToTree(any(JobCallbackPayload.class));
        executeScheduleProcessInstanceJobWithFailure(description);
        verifyCreateJobEventWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFailureAndContinue() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateJobEvent expectedEvent = mockExpectedCreateJobEvent();
        doReturn(JSON_PAYLOAD).when(objectMapper).valueToTree(any(JobCallbackPayload.class));
        // First execution fails
        executeScheduleProcessInstanceJobWithFailure(description);

        // Clear the errors and produce a second execution that must work fine.
        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CreateJobEvent.class));
        jobsService.scheduleJob(description);

        verifyCreateJobEventWasCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeScheduleProcessInstanceJobWithFailure(ProcessInstanceJobDescription description) throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateJobEvent.class));
        RuntimeException nackError = new RuntimeException(ERROR);
        eventsEmitter.setNackError(nackError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.scheduleJob(description))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(nackError);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFatalFailure() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateJobEvent expectedEvent = mockExpectedCreateJobEvent();
        doReturn(JSON_PAYLOAD).when(objectMapper).valueToTree(any(JobCallbackPayload.class));
        executeScheduleProcessInstanceJobWithFataFailure(description);
        verifyCreateJobEventWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFatalFailureAndContinue() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateJobEvent expectedEvent = mockExpectedCreateJobEvent();
        doReturn(JSON_PAYLOAD).when(objectMapper).valueToTree(any(JobCallbackPayload.class));
        // First execution fails
        executeScheduleProcessInstanceJobWithFataFailure(description);

        // Clear the errors and produce a second execution that must work fine.
        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CreateJobEvent.class));
        jobsService.scheduleJob(description);

        verifyCreateJobEventWasCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeScheduleProcessInstanceJobWithFataFailure(ProcessInstanceJobDescription description) throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateJobEvent.class));
        RuntimeException fatalError = new RuntimeException(FATAL_ERROR);
        eventsEmitter.setFatalError(fatalError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.scheduleJob(description))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(fatalError);
    }

    @Test
    protected void cancelJobSuccessful() throws Exception {
        DeleteJobEvent expectedEvent = mockExpectedDeleteJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));

        jobsService.cancelJob(JOB_ID_STRING);

        verifyDeleteJobEventCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFailure() throws Exception {
        DeleteJobEvent expectedEvent = mockExpectedDeleteJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        verifyDeleteJobEventCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFailureAndContinue() throws Exception {
        DeleteJobEvent expectedEvent = mockExpectedDeleteJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));
        jobsService.cancelJob(JOB_ID_STRING);

        verifyDeleteJobEventCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeCancelJobWithFailure(String jobId) {
        RuntimeException nackError = new RuntimeException(ERROR);
        eventsEmitter.setNackError(nackError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.cancelJob(jobId))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(nackError);
    }

    @Test
    protected void cancelJobWithFatalFailure() throws Exception {
        DeleteJobEvent expectedEvent = mockExpectedDeleteJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));

        executeCancelJobWithFatalFailure(JOB_ID_STRING);

        verifyDeleteJobEventCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFatalFailureAndContinue() throws Exception {
        DeleteJobEvent expectedEvent = mockExpectedDeleteJobEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(DeleteJobEvent.class));
        jobsService.cancelJob(JOB_ID_STRING);

        verifyDeleteJobEventCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeCancelJobWithFatalFailure(String jobId) {
        RuntimeException fatalError = new RuntimeException(ERROR);
        eventsEmitter.setFatalError(fatalError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.cancelJob(jobId))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(fatalError);
    }

    protected ProcessInstanceJobDescription mockProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.newProcessInstanceJobDescriptionBuilder()
                .id(JOB_ID)
                .timerId(TIMER_ID)
                .expirationTime(EXPIRATION_TIME)
                .priority(PRIORITY)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .nodeInstanceId(NODE_INSTANCE_ID)
                .build();
    }

    protected CreateJobEvent mockExpectedCreateJobEvent() {
        Job job = Job.builder()
                .id(JOB_ID)
                .correlationId(JOB_ID)
                .retry(null)
                .schedule(TimerSchedule.builder()
                        .startTime(EXPIRATION_TIME.get().toOffsetDateTime())
                        .repeatCount(EXPIRATION_TIME.repeatLimit())
                        .delay(EXPIRATION_TIME.repeatInterval())
                        .delayUnit(TemporalUnit.MILLIS)
                        .build())
                .recipient(HttpRecipient.builder().forJsonPayload()
                        .payload(HttpRecipientJsonPayloadData.from(JSON_PAYLOAD))
                        .url(CALLBACK_ENDPOINT)
                        .header("processInstanceId", PROCESS_INSTANCE_ID)
                        .header("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
                        .header("processId", PROCESS_ID)
                        .header("rootProcessId", ROOT_PROCESS_ID)
                        .header("nodeInstanceId", NODE_INSTANCE_ID)
                        .header("Content-Type", "application/json")
                        .build())
                .build();

        return CreateJobEvent.builder()
                .source(SERVICE_URI)
                .job(job)
                .build();
    }

    protected DeleteJobEvent mockExpectedDeleteJobEvent() {
        return DeleteJobEvent.builder()
                .lookupId(JobLookupId.fromId(JOB_ID_STRING))
                .source(SERVICE_URI)
                .build();
    }

    protected void assertEquals(CreateJobEvent event, CreateJobEvent expected) {
        assertThat(event).isNotNull();
        assertThat(expected).isNotNull();
        assertThat(event.getSource()).isEqualTo(expected.getSource());
        Job job = event.getData();
        assertThat(job.getRetry()).isNull();
        assertThat(job.getId()).isEqualTo(expected.getData().getId());
        assertThat(job.getCorrelationId()).isEqualTo(expected.getData().getCorrelationId());

        assertThat(job.getRecipient()).hasSameClassAs(expected.getData().getRecipient());
        HttpRecipient recipient = (HttpRecipient) job.getRecipient();
        HttpRecipient expectedRecipient = (HttpRecipient) expected.getData().getRecipient();
        assertThat(recipient.getUrl()).isEqualTo(expectedRecipient.getUrl());
        assertThat(recipient.getMethod()).isEqualTo(expectedRecipient.getMethod());
        assertThat(recipient.getPayload()).isEqualTo(expectedRecipient.getPayload());
        assertThat(recipient.getQueryParams()).isEqualTo(expectedRecipient.getQueryParams());
        assertThat(recipient.getHeaders()).isEqualTo(expectedRecipient.getHeaders());

        assertThat(job.getSchedule()).hasSameClassAs(expected.getData().getSchedule());
        TimerSchedule schedule = (TimerSchedule) job.getSchedule();
        TimerSchedule expectedSchedule = (TimerSchedule) expected.getData().getSchedule();
        assertThat(schedule.getStartTime()).isEqualTo(expectedSchedule.getStartTime());
        assertThat(schedule.getRepeatCount()).isEqualTo(expectedSchedule.getRepeatCount());
        assertThat(schedule.getDelay()).isEqualTo(expectedSchedule.getDelay());
        assertThat(schedule.getDelayUnit()).isEqualTo(expectedSchedule.getDelayUnit());
    }

    protected void assertEquals(DeleteJobEvent event, DeleteJobEvent expected) {
        assertThat(event).isNotNull();
        assertThat(expected).isNotNull();
        assertThat(event.getData()).isNotNull();
        assertThat(event.getSource()).isEqualTo(expected.getSource());
        assertThat(event.getData().getId()).isEqualTo(expected.getData().getId());
    }

    protected void verifyCreateJobEventWasCreated(int times, CreateJobEvent... expectedEvents) throws Exception {
        verify(objectMapper, times(times)).writeValueAsString(createEventCaptor.capture());
        List<CreateJobEvent> events = createEventCaptor.getAllValues();
        for (int i = 0; i < times; i++) {
            assertEquals(events.get(i), expectedEvents[i]);
        }
    }

    protected void verifyDeleteJobEventCreated(int times, DeleteJobEvent... expectedEvents) throws Exception {
        verify(objectMapper, times(times)).writeValueAsString(deleteEventCaptor.capture());
        List<DeleteJobEvent> events = deleteEventCaptor.getAllValues();
        for (int i = 0; i < times; i++) {
            assertEquals(events.get(i), expectedEvents[i]);
        }
    }

    protected void verifyEmitterWasInvoked(int times, String... expectedPayloads) {
        verify(eventsEmitter, times(times)).send(messageCaptor.capture());
        assertThat(messageCaptor.getAllValues()).hasSize(times);
        List<Message<String>> messages = messageCaptor.getAllValues();
        for (int i = 0; i < times; i++) {
            assertThat(messages.get(i).getPayload()).isEqualTo(expectedPayloads[i]);
        }
    }

    static class EmitterMock implements Emitter<String> {

        private Exception nackError;

        private RuntimeException fatalError;

        public void setNackError(Exception error) {
            this.nackError = error;
        }

        public void setFatalError(RuntimeException fatalError) {
            this.fatalError = fatalError;
        }

        public void clearErrors() {
            this.nackError = null;
            this.fatalError = null;
        }

        @Override
        public CompletionStage<Void> send(String s) {
            return raiseNotSupported();
        }

        @Override
        public <M extends Message<? extends String>> void send(M m) {
            if (fatalError != null) {
                throw fatalError;
            }
            if (nackError != null) {
                m.nack(nackError);
            } else {
                m.ack();
            }
        }

        @Override
        public void complete() {
            raiseNotSupported();
        }

        @Override
        public void error(Exception e) {
            raiseNotSupported();
        }

        @Override
        public boolean isCancelled() {
            return raiseNotSupported();
        }

        @Override
        public boolean hasRequests() {
            return raiseNotSupported();
        }

        private <T> T raiseNotSupported() {
            throw new UnsupportedOperationException("Current method is not supported, feel free to implement if needed for testing purposes.");
        }
    }
}

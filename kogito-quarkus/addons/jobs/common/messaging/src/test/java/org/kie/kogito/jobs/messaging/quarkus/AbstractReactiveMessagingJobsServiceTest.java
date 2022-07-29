/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import org.kie.kogito.jobs.JobsServiceException;
import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.ProcessJobDescription;
import org.kie.kogito.jobs.TimerJobId;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    protected static final TimerJobId JOB_ID = new TimerJobId(1L);
    protected static final String SERIALIZED_EVENT = "SERIALIZED_EVENT";
    protected static final String SERIALIZED_SECOND_EVENT = "SERIALIZED_SECOND_EVENT";

    protected static final String JOB_ID_STRING = "JOB_ID_STRING";

    private static final String CALLBACK_ENDPOINT = SERVICE_URI + "/management/jobs/" + PROCESS_ID
            + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + JOB_ID.encode();

    protected static final String ERROR = "ERROR";
    protected static final String FATAL_ERROR = "FATAL_ERROR";

    protected EmitterMock eventsEmitter;

    @Captor
    protected ArgumentCaptor<Message<String>> messageCaptor;

    @Mock
    protected ObjectMapper objectMapper;

    @Captor
    protected ArgumentCaptor<CreateProcessInstanceJobRequestEvent> createEventCaptor;

    @Captor
    protected ArgumentCaptor<CancelJobRequestEvent> cancelEventCaptor;

    protected T jobsService;

    @BeforeEach
    protected void setUp() {
        this.eventsEmitter = spy(new EmitterMock());
        this.jobsService = createJobsService(SERVICE_URI, objectMapper, eventsEmitter);
    }

    protected abstract T createJobsService(URI serviceUrl, ObjectMapper objectMapper, Emitter<String> eventsEmitter);

    @Test
    protected void scheduleProcessInstanceJobSuccessful() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateProcessInstanceJobRequestEvent expectedEvent = mockExpectedCreateProcessInstanceJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));

        jobsService.scheduleProcessInstanceJob(description);

        verifyCreateProcessInstanceJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFailure() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateProcessInstanceJobRequestEvent expectedEvent = mockExpectedCreateProcessInstanceJobRequestEvent();
        executeScheduleProcessInstanceJobWithFailure(description);
        verifyCreateProcessInstanceJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFailureAndContinue() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateProcessInstanceJobRequestEvent expectedEvent = mockExpectedCreateProcessInstanceJobRequestEvent();
        // First execution fails
        executeScheduleProcessInstanceJobWithFailure(description);

        // Clear the errors and produce a second execution that must work fine.
        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));
        jobsService.scheduleProcessInstanceJob(description);

        verifyCreateProcessInstanceJobRequestWasCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeScheduleProcessInstanceJobWithFailure(ProcessInstanceJobDescription description) throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));
        RuntimeException nackError = new RuntimeException(ERROR);
        eventsEmitter.setNackError(nackError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.scheduleProcessInstanceJob(description))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(nackError);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFatalFailure() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateProcessInstanceJobRequestEvent expectedEvent = mockExpectedCreateProcessInstanceJobRequestEvent();
        executeScheduleProcessInstanceJobWithFataFailure(description);
        verifyCreateProcessInstanceJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void scheduleProcessInstanceJobWithFatalFailureAndContinue() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        CreateProcessInstanceJobRequestEvent expectedEvent = mockExpectedCreateProcessInstanceJobRequestEvent();
        // First execution fails
        executeScheduleProcessInstanceJobWithFataFailure(description);

        // Clear the errors and produce a second execution that must work fine.
        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));
        jobsService.scheduleProcessInstanceJob(description);

        verifyCreateProcessInstanceJobRequestWasCreated(2, expectedEvent, expectedEvent);
        verifyEmitterWasInvoked(2, SERIALIZED_EVENT, SERIALIZED_SECOND_EVENT);
    }

    protected void executeScheduleProcessInstanceJobWithFataFailure(ProcessInstanceJobDescription description) throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));
        RuntimeException fatalError = new RuntimeException(FATAL_ERROR);
        eventsEmitter.setFatalError(fatalError);
        // ensure the execution failed as programmed
        assertThatThrownBy(() -> jobsService.scheduleProcessInstanceJob(description))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(fatalError);
    }

    @Test
    protected void cancelJobSuccessful() throws Exception {
        CancelJobRequestEvent expectedEvent = mockExpectedCancelJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        jobsService.cancelJob(JOB_ID_STRING);

        verifyCancelJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFailure() throws Exception {
        CancelJobRequestEvent expectedEvent = mockExpectedCancelJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        verifyCancelJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFailureAndContinue() throws Exception {
        CancelJobRequestEvent expectedEvent = mockExpectedCancelJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));
        jobsService.cancelJob(JOB_ID_STRING);

        verifyCancelJobRequestWasCreated(2, expectedEvent, expectedEvent);
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
        CancelJobRequestEvent expectedEvent = mockExpectedCancelJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        executeCancelJobWithFatalFailure(JOB_ID_STRING);

        verifyCancelJobRequestWasCreated(1, expectedEvent);
        verifyEmitterWasInvoked(1, SERIALIZED_EVENT);
    }

    @Test
    protected void cancelJobWithFatalFailureAndContinue() throws Exception {
        CancelJobRequestEvent expectedEvent = mockExpectedCancelJobRequestEvent();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        executeCancelJobWithFailure(JOB_ID_STRING);

        eventsEmitter.clearErrors();
        doReturn(SERIALIZED_SECOND_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));
        jobsService.cancelJob(JOB_ID_STRING);

        verifyCancelJobRequestWasCreated(2, expectedEvent, expectedEvent);
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

    @Test
    protected void scheduleProcessJob() {
        ProcessJobDescription description = ProcessJobDescription.of(EXPIRATION_TIME, PROCESS_ID);
        assertThatThrownBy(() -> jobsService.scheduleProcessJob(description))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    protected ProcessInstanceJobDescription mockProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.of(JOB_ID,
                EXPIRATION_TIME,
                PRIORITY,
                PROCESS_INSTANCE_ID,
                ROOT_PROCESS_INSTANCE_ID, PROCESS_ID,
                ROOT_PROCESS_ID,
                NODE_INSTANCE_ID);
    }

    protected CreateProcessInstanceJobRequestEvent mockExpectedCreateProcessInstanceJobRequestEvent() {
        return CreateProcessInstanceJobRequestEvent.builder()
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .source(SERVICE_URI)
                .job(new Job(JOB_ID.encode(),
                        EXPIRATION_TIME.get(),
                        PRIORITY,
                        CALLBACK_ENDPOINT,
                        PROCESS_INSTANCE_ID,
                        ROOT_PROCESS_INSTANCE_ID,
                        PROCESS_ID,
                        ROOT_PROCESS_ID,
                        null,
                        0,
                        NODE_INSTANCE_ID))
                .build();
    }

    protected CancelJobRequestEvent mockExpectedCancelJobRequestEvent() {
        return CancelJobRequestEvent.builder()
                .jobId(JOB_ID_STRING)
                .source(SERVICE_URI)
                .build();
    }

    protected void assertEquals(CreateProcessInstanceJobRequestEvent event, CreateProcessInstanceJobRequestEvent expected) {
        assertThat(event).isNotNull();
        assertThat(expected).isNotNull();
        assertThat(event.getProcessInstanceId()).isEqualTo(expected.getProcessInstanceId());
        assertThat(event.getProcessId()).isEqualTo(expected.getProcessId());
        assertThat(event.getRootProcessInstanceId()).isEqualTo(expected.getRootProcessInstanceId());
        assertThat(event.getRootProcessId()).isEqualTo(expected.getRootProcessId());
        assertThat(event.getSource()).isEqualTo(expected.getSource());
        Job job = event.getData();
        assertThat(job).isNotNull();
        Job expectedJob = expected.getData();
        assertThat(expectedJob).isNotNull();
        assertThat(job).isEqualTo(expectedJob);
    }

    protected void assertEquals(CancelJobRequestEvent event, CancelJobRequestEvent expected) {
        assertThat(event).isNotNull();
        assertThat(expected).isNotNull();
        assertThat(event.getProcessInstanceId()).isEqualTo(expected.getProcessInstanceId());
        assertThat(event.getProcessId()).isEqualTo(expected.getProcessId());
        assertThat(event.getRootProcessInstanceId()).isEqualTo(expected.getRootProcessInstanceId());
        assertThat(event.getRootProcessId()).isEqualTo(expected.getRootProcessId());
        assertThat(event.getSource()).isEqualTo(expected.getSource());
    }

    protected void verifyCreateProcessInstanceJobRequestWasCreated(int times, CreateProcessInstanceJobRequestEvent... expectedEvents) throws Exception {
        verify(objectMapper, times(times)).writeValueAsString(createEventCaptor.capture());
        List<CreateProcessInstanceJobRequestEvent> events = createEventCaptor.getAllValues();
        for (int i = 0; i < times; i++) {
            assertEquals(events.get(i), expectedEvents[i]);
        }
    }

    protected void verifyCancelJobRequestWasCreated(int times, CancelJobRequestEvent... expectedEvents) throws Exception {
        verify(objectMapper, times(times)).writeValueAsString(cancelEventCaptor.capture());
        List<CancelJobRequestEvent> events = cancelEventCaptor.getAllValues();
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

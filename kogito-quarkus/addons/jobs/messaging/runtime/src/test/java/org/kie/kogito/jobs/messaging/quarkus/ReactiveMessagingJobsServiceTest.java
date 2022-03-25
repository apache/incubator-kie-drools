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

import io.smallrye.reactive.messaging.MutinyEmitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactiveMessagingJobsServiceTest {

    private static final URI SERVICE_URI = URI.create("http://myService.com:8080");
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final Integer PRIORITY = 0;
    private static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    private static final ExpirationTime EXPIRATION_TIME = ExactExpirationTime.of("2020-03-21T10:15:30+01:00");
    private static final TimerJobId JOB_ID = new TimerJobId(1L);
    private static final String SERIALIZED_EVENT = "SERIALIZED_EVENT";
    private static final String JOB_ID_STRING = "JOB_ID_STRING";

    private static final String CALLBACK_ENDPOINT = SERVICE_URI + "/management/jobs/" + PROCESS_ID
            + "/instances/" + PROCESS_INSTANCE_ID + "/timers/" + JOB_ID.encode();

    private static final String ERROR = "ERROR";

    @Mock
    private MutinyEmitter<String> eventsEmitter;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<CreateProcessInstanceJobRequestEvent> createEventCaptor;

    @Captor
    private ArgumentCaptor<CancelJobRequestEvent> cancelEventCaptor;

    private ReactiveMessagingJobsService jobsService;

    @BeforeEach
    void setUp() {
        this.jobsService = new ReactiveMessagingJobsService(SERVICE_URI, objectMapper, eventsEmitter);
    }

    @Test
    void scheduleProcessInstanceJobSuccessful() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));

        jobsService.scheduleProcessInstanceJob(description);

        verify(objectMapper).writeValueAsString(createEventCaptor.capture());
        assertExpectedCreateProcessInstanceJobRequestEvent(createEventCaptor.getValue());
        verify(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);
    }

    @Test
    void scheduleProcessInstanceJobSuccessWithFailure() throws Exception {
        ProcessInstanceJobDescription description = mockProcessInstanceJobDescription();
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CreateProcessInstanceJobRequestEvent.class));

        RuntimeException internalError = new RuntimeException(ERROR);
        doThrow(internalError).when(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);

        assertThatThrownBy(() -> jobsService.scheduleProcessInstanceJob(description))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(internalError);

        verify(objectMapper).writeValueAsString(createEventCaptor.capture());
        assertExpectedCreateProcessInstanceJobRequestEvent(createEventCaptor.getValue());
        verify(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);
    }

    @Test
    void cancelJobSuccessful() throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        jobsService.cancelJob(JOB_ID_STRING);

        verify(objectMapper).writeValueAsString(cancelEventCaptor.capture());
        assertExpectedCancelJobRequestEvent(cancelEventCaptor.getValue());
        verify(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);
    }

    @Test
    void cancelJobSuccessfulWithFailure() throws Exception {
        doReturn(SERIALIZED_EVENT).when(objectMapper).writeValueAsString(any(CancelJobRequestEvent.class));

        RuntimeException internalError = new RuntimeException(ERROR);
        doThrow(internalError).when(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);
        assertThatThrownBy(() -> jobsService.cancelJob(JOB_ID_STRING))
                .isInstanceOf(JobsServiceException.class)
                .hasMessageContaining("Error while emitting JobCloudEvent")
                .hasCause(internalError);

        verify(objectMapper).writeValueAsString(cancelEventCaptor.capture());
        assertExpectedCancelJobRequestEvent(cancelEventCaptor.getValue());
        verify(eventsEmitter).sendAndAwait(SERIALIZED_EVENT);
    }

    @Test
    void scheduleProcessJob() {
        ProcessJobDescription description = ProcessJobDescription.of(EXPIRATION_TIME, PROCESS_ID);
        assertThatThrownBy(() -> jobsService.scheduleProcessJob(description))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private void assertExpectedCreateProcessInstanceJobRequestEvent(CreateProcessInstanceJobRequestEvent event) {
        assertThat(event).isNotNull();
        assertThat(event.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(event.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(event.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(event.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(event.getSource()).isEqualTo(SERVICE_URI);
        Job job = event.getData();
        assertThat(job).isNotNull();
        assertThat(job.getId()).isEqualTo(JOB_ID.encode());
        assertThat(job.getExpirationTime()).isEqualTo(EXPIRATION_TIME.get());
        assertThat(job.getRepeatLimit()).isZero();
        assertThat(job.getRepeatInterval()).isNull();
        assertThat(job.getPriority()).isEqualTo(PRIORITY);
        assertThat(job.getCallbackEndpoint()).isEqualTo(CALLBACK_ENDPOINT);
        assertThat(job.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(job.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(job.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(job.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
    }

    private void assertExpectedCancelJobRequestEvent(CancelJobRequestEvent event) {
        assertThat(event).isNotNull();
        assertThat(event.getData()).isNotNull();
        assertThat(event.getData().getId()).isEqualTo(JOB_ID_STRING);
        assertThat(event.getSource()).isEqualTo(SERVICE_URI);
    }

    private ProcessInstanceJobDescription mockProcessInstanceJobDescription() {
        return ProcessInstanceJobDescription.of(JOB_ID,
                EXPIRATION_TIME,
                PRIORITY,
                PROCESS_INSTANCE_ID,
                ROOT_PROCESS_INSTANCE_ID, PROCESS_ID,
                ROOT_PROCESS_ID,
                NODE_INSTANCE_ID);
    }
}

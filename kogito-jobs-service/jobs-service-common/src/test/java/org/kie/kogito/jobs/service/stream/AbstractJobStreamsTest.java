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

package org.kie.kogito.jobs.service.stream;

import java.time.ZonedDateTime;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
abstract class AbstractJobStreamsTest<T extends AbstractJobStreams> {

    protected static final String URL = "http://localhost:8180";
    private static final String SERIALIZED_MESSAGE = "SERIALIZED_MESSAGE";

    private static final String JOB_ID = "JOB_ID";
    private static final String CORRELATION_ID = "CORRELATION_ID";
    private static final JobStatus STATUS = JobStatus.SCHEDULED;
    private static final ZonedDateTime LAST_UPDATE = ZonedDateTime.parse("2022-08-03T18:00:15.001+01:00");
    private static final Integer RETRIES = 1;
    private static final Integer PRIORITY = 1;
    private static final Integer EXECUTION_COUNTER = 1;
    private static final String SCHEDULE_ID = "SCHEDULE_ID";

    private static final Recipient RECIPIENT = new RecipientInstance(HttpRecipient.builder().forStringPayload().url("http://recipient").build());
    private static final Trigger TRIGGER = new PointInTimeTrigger();
    @Captor
    ArgumentCaptor<Message<String>> messageCaptor;

    @Mock
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Mock
    Emitter<String> emitter;

    T jobStreams;

    @BeforeEach
    void setUp() {
        jobStreams = spy(createJobStreams());
    }

    protected abstract T createJobStreams();

    @Test
    void jobStatusChangeWithAck() throws Exception {
        JobDetails job = mockJobDetails();
        doReturn(SERIALIZED_MESSAGE).when(objectMapper).writeValueAsString(any());
        Message<String> message = executeStatusChange(job);
        message.ack();
        verify(jobStreams).onAck(job);
    }

    @Test
    void jobStatusChangeWithNack() throws Exception {
        JobDetails job = mockJobDetails();
        doReturn(SERIALIZED_MESSAGE).when(objectMapper).writeValueAsString(any());
        Message<String> message = executeStatusChange(job);
        Exception error = new Exception("Nack error");
        message.nack(error);
        verify(jobStreams).onNack(error, job);
    }

    private Message<String> executeStatusChange(JobDetails job) throws Exception {
        jobStreams.jobStatusChange(job);
        verify(objectMapper).writeValueAsString(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(JobDataEvent.class);
        assertExpectedEvent((JobDataEvent) eventCaptor.getValue());

        verify(emitter).send(messageCaptor.capture());
        Message<String> message = messageCaptor.getValue();
        assertThat(message).isNotNull();
        assertThat(message.getPayload()).isEqualTo(SERIALIZED_MESSAGE);
        assertExpectedMetadata(message);
        return message;
    }

    @Test
    void jobStatusChangeWithUnexpectedError() throws Exception {
        JobDetails job = mockJobDetails();
        executeStatusChangeWithUnexpectedError(job);
    }

    @Test
    void jobStatusChangeWithUnexpectedErrorAndContinue() throws Exception {
        JobDetails job = mockJobDetails();
        executeStatusChangeWithUnexpectedError(job);

        doReturn(SERIALIZED_MESSAGE).when(objectMapper).writeValueAsString(any());
        jobStreams.jobStatusChange(job);

        verify(emitter).send(messageCaptor.capture());
        Message<String> message = messageCaptor.getValue();
        assertThat(message).isNotNull();
        assertThat(message.getPayload()).isEqualTo(SERIALIZED_MESSAGE);
        assertExpectedMetadata(message);
        message.ack();
        verify(jobStreams).onAck(job);
    }

    private void executeStatusChangeWithUnexpectedError(JobDetails job) throws Exception {
        doThrow(new RuntimeException("Unexpected error")).when(objectMapper).writeValueAsString(any());
        jobStreams.jobStatusChange(job);

        verify(jobStreams, never()).onAck(any());
        verify(jobStreams, never()).onNack(any(), any());
    }

    private JobDetails mockJobDetails() {
        return JobDetails.builder()
                .id(JOB_ID)
                .correlationId(CORRELATION_ID)
                .status(STATUS)
                .lastUpdate(LAST_UPDATE)
                .retries(RETRIES)
                .priority(PRIORITY)
                .executionCounter(EXECUTION_COUNTER)
                .scheduledId(SCHEDULE_ID)
                .recipient(RECIPIENT)
                .trigger(TRIGGER)
                .build();

    }

    private void assertExpectedEvent(JobDataEvent event) {
        assertThat(event.getId()).isNotNull();
        assertThat(event.getType()).isEqualTo(JobDataEvent.JOB_EVENT_TYPE);
        assertThat(event.getSource()).hasToString(URL + "/jobs");
        ScheduledJob data = event.getData();
        assertThat(data).isNotNull();
        assertThat(data.getId()).isEqualTo(JOB_ID);
        assertThat(data.getScheduledId()).isEqualTo(SCHEDULE_ID);
        assertThat(data.getStatus()).isEqualTo(STATUS);
        assertThat(data.getRetries()).isEqualTo(RETRIES);
        assertThat(data.getExecutionCounter()).isEqualTo(EXECUTION_COUNTER);
        assertThat(data.getLastUpdate()).isEqualTo(LAST_UPDATE);
    }

    protected void assertExpectedMetadata(Message<String> message) {
        assertThat(message.getMetadata()).isEqualTo(Metadata.empty());
    }
}

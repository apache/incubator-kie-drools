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
package org.kie.kogito.addons.quarkus.jobs.service.embedded.stream;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.job.JobInstanceDataEvent;
import org.kie.kogito.index.addon.DataIndexEventPublisherMock;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.Recipient;
import org.kie.kogito.jobs.service.model.RecipientInstance;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.enterprise.inject.Instance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class EventPublisherJobStreamsTest {

    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    public static final String NODE_INSTANCE_ID = "NODE_INSTANCE_ID";
    private static final String URL = "http://my_service";
    private static final String JOB_ID = "JOB_ID";
    private static final String CORRELATION_ID = "CORRELATION_ID";
    private static final JobStatus STATUS = JobStatus.SCHEDULED;
    private static final ZonedDateTime LAST_UPDATE = ZonedDateTime.parse("2023-04-12T15:00:00.001Z");
    private static final Integer RETRIES = 1;
    private static final Integer PRIORITY = 3;
    private static final Integer EXECUTION_COUNTER = 1;
    private static final String SCHEDULE_ID = "SCHEDULE_ID";
    private static final ZonedDateTime EXPIRATION_TIME = ZonedDateTime.parse("2023-04-13T00:00:00.001Z");
    private static final long PERIOD = 3000;
    private static final ChronoUnit PERIOD_UNIT = ChronoUnit.MILLIS;
    private static final int REPEAT_COUNT = 4;

    private static final Trigger TRIGGER = new SimpleTimerTrigger(Date.from(EXPIRATION_TIME.toInstant()), PERIOD,
            PERIOD_UNIT, REPEAT_COUNT, EXPIRATION_TIME.getZone().getId());
    private static final String RECIPIENT_URL = "http://recipient";

    private static final Recipient RECIPIENT = new RecipientInstance(HttpRecipient.builder()
            .forStringPayload()
            .url(RECIPIENT_URL)
            .header("processInstanceId", PROCESS_INSTANCE_ID)
            .header("processId", PROCESS_ID)
            .header("rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID)
            .header("rootProcessId", ROOT_PROCESS_ID)
            .header("nodeInstanceId", NODE_INSTANCE_ID)
            .build());

    @Test
    void onJobStatusChange() throws Exception {
        ArgumentCaptor<JobInstanceDataEvent> eventCaptor = ArgumentCaptor.forClass(JobInstanceDataEvent.class);
        DataIndexEventPublisherMock eventPublisher = spy(new DataIndexEventPublisherMock());
        Instance<EventPublisher> eventPublisherInstance = mock(Instance.class);
        Stream<EventPublisher> eventPublishers = Arrays.stream(new EventPublisher[] { eventPublisher });
        doReturn(eventPublishers).when(eventPublisherInstance).stream();
        ManagedExecutor managedExecutor = mock(ManagedExecutor.class);
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);
        doReturn(completableFuture).when(managedExecutor).runAsync(any());
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        EventPublisherJobStreams eventPublisherJobStreams = new EventPublisherJobStreams(URL, eventPublisherInstance, objectMapper, managedExecutor);

        JobDetails jobDetails = buildJobDetails();
        eventPublisherJobStreams.publishJobStatusChange(jobDetails);
        verify(managedExecutor).runAsync(runnableArgumentCaptor.capture());
        runnableArgumentCaptor.getValue().run();

        verify(eventPublisher).publish(eventCaptor.capture());
        verify(eventPublisher, never()).publish(anyCollection());

        JobInstanceDataEvent event = eventCaptor.getValue();
        assertThat(event).isNotNull();

        assertThat(event.getSpecVersion()).hasToString("1.0");
        assertThat(event.getId()).isNotNull();
        assertThat(event.getSource()).hasToString(URL + "/jobs");
        assertThat(event.getType()).isEqualTo("JobEvent");
        assertThat(event.getTime()).isNotNull();
        assertThat(event.getSubject()).isNull();
        assertThat(event.getKogitoProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(event.getKogitoProcessId()).isEqualTo(PROCESS_ID);
        assertThat(event.getKogitoRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(event.getKogitoRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(event.getData()).isNotEmpty();

        assertData(objectMapper.readTree(event.getData()));
    }

    private void assertData(JsonNode jsonNode) {
        assertThat(jsonNode).hasSize(19);
        assertHasField(jsonNode, "id", JOB_ID);
        assertHasField(jsonNode, "expirationTime", EXPIRATION_TIME.toString());
        assertHasField(jsonNode, "priority", Integer.toString(PRIORITY));
        assertHasField(jsonNode, "callbackEndpoint", RECIPIENT_URL);
        assertHasField(jsonNode, "processInstanceId", PROCESS_INSTANCE_ID);
        assertHasField(jsonNode, "processId", PROCESS_ID);
        assertHasField(jsonNode, "rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID);
        assertHasField(jsonNode, "rootProcessId", ROOT_PROCESS_ID);
        assertHasField(jsonNode, "nodeInstanceId", NODE_INSTANCE_ID);
        assertHasField(jsonNode, "repeatInterval", Long.toString(PERIOD));
        assertHasField(jsonNode, "repeatLimit", Integer.toString(REPEAT_COUNT));
        assertHasField(jsonNode, "scheduledId", SCHEDULE_ID);
        assertHasField(jsonNode, "retries", Integer.toString(RETRIES));
        assertHasField(jsonNode, "status", STATUS.name());
        assertHasField(jsonNode, "lastUpdate", LAST_UPDATE.toString());
        assertHasField(jsonNode, "executionCounter", Integer.toString(EXECUTION_COUNTER));
        assertHasField(jsonNode, "executionResponse", null);
        assertHasField(jsonNode, "exceptionMessage", null);
        assertHasField(jsonNode, "exceptionDetails", null);
    }

    private JobDetails buildJobDetails() {
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

    private static void assertHasField(JsonNode node, String fieldName, String expectedValue) {
        JsonNode valueNode = node.get(fieldName);
        assertThat(node.get(fieldName)).isNotNull();
        if (expectedValue != null) {
            assertThat(valueNode.asText()).isEqualTo(expectedValue);
        } else {
            assertThat(valueNode.isNull()).isTrue();
        }
    }
}

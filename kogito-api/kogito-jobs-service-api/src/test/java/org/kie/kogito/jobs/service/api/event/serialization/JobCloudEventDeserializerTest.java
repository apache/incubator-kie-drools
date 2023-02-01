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

package org.kie.kogito.jobs.service.api.event.serialization;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.Retry;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientBinaryPayloadData;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.jobs.service.api.event.TestConstants.*;

class JobCloudEventDeserializerTest {

    private static final String CREATE_JOB_EVENT_RESOURCE = "org/kie/kogito/jobs/service/api/event/serialization/CreateJobEvent.json";
    private static final String CREATE_JOB_EVENT_DATA_CONTENT_RESOURCE = "org/kie/kogito/jobs/service/api/event/serialization/CreateJobEventDataContent.json";
    private static final String DELETE_JOB_EVENT_RESOURCE = "org/kie/kogito/jobs/service/api/event/serialization/DeleteJobEvent.json";
    private static final String DELETE_JOB_EVENT_DATA_CONTENT_RESOURCE = "org/kie/kogito/jobs/service/api/event/serialization/DeleteJobEventDataContent.json";
    private static final String UNEXPECTED_TYPE_EVENT_RESOURCE = "org/kie/kogito/jobs/service/api/event/serialization/UnexpectedTypeEvent.json";

    private JobCloudEventDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new JobCloudEventDeserializer();
    }

    @Test
    void deserializeCreateJobEvent() throws Exception {
        JobCloudEvent<?> result = deserializer.deserialize(readFileContent(CREATE_JOB_EVENT_RESOURCE));
        assertCreateJobEvent(result);
    }

    @Test
    void deserializeCreateJobEventFromCloudEvent() throws Exception {
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(ID)
                .withSource(SOURCE)
                .withType(CreateJobEvent.TYPE)
                .withTime(TIME)
                .withSubject(SUBJECT)
                .withData(readFileContent(CREATE_JOB_EVENT_DATA_CONTENT_RESOURCE))
                .build();

        JobCloudEvent<?> result = deserializer.deserialize(cloudEvent);
        assertCreateJobEvent(result);
    }

    @Test
    void deserializeCancelJobEvent() throws Exception {
        JobCloudEvent<?> result = deserializer.deserialize(readFileContent(DELETE_JOB_EVENT_RESOURCE));
        assertDeleteJobEvent(result);
    }

    @Test
    void deserializeDeleteJobEventFromCloudEvent() throws Exception {
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(ID)
                .withSource(SOURCE)
                .withType(DeleteJobEvent.TYPE)
                .withTime(TIME)
                .withSubject(SUBJECT)
                .withData(readFileContent(DELETE_JOB_EVENT_DATA_CONTENT_RESOURCE))
                .build();

        JobCloudEvent<?> result = deserializer.deserialize(cloudEvent);
        assertDeleteJobEvent(result);
    }

    @Test
    void deserializeUnexpectedType() throws Exception {
        byte[] fileContent = readFileContent(UNEXPECTED_TYPE_EVENT_RESOURCE);
        assertThatThrownBy(() -> deserializer.deserialize(fileContent))
                .isInstanceOf(DeserializationException.class)
                .hasMessage("Unknown JobCloudEvent event type: UnexpectedType");
    }

    private static void assertCreateJobEvent(JobCloudEvent<?> result) {
        assertThat(result).isInstanceOf(CreateJobEvent.class);
        CreateJobEvent event = (CreateJobEvent) result;
        assertBaseFields(event);

        Job job = event.getData();
        assertThat(job).isNotNull();

        assertThat(job.getId()).isEqualTo(JOB_ID);
        assertThat(job.getCorrelationId()).isEqualTo(CORRELATION_ID);
        assertThat(job.getState()).isEqualTo(Job.State.TBD1);

        assertThat(job.getSchedule()).isInstanceOf(TimerSchedule.class);
        TimerSchedule schedule = (TimerSchedule) job.getSchedule();
        assertThat(schedule.getStartTime()).isEqualTo(SCHEDULE_START_TIME);
        assertThat(schedule.getRepeatCount()).isEqualTo(SCHEDULE_REPEAT_COUNT);
        assertThat(schedule.getDelay()).isEqualTo(SCHEDULE_DELAY);
        assertThat(schedule.getDelayUnit()).isEqualTo(SCHEDULE_DELAY_UNIT);

        assertThat(job.getRetry()).isNotNull();
        Retry retry = job.getRetry();
        assertThat(retry.getMaxRetries()).isEqualTo(RETRY_MAX_RETRIES);
        assertThat(retry.getDelay()).isEqualTo(RETRY_DELAY);
        assertThat(retry.getDelayUnit()).isEqualTo(RETRY_DELAY_UNIT);
        assertThat(retry.getMaxDuration()).isEqualTo(RETRY_MAX_DURATION);
        assertThat(retry.getDurationUnit()).isEqualTo(RETRY_DURATION_UNIT);

        assertThat(job.getRecipient()).isInstanceOf(HttpRecipient.class);
        HttpRecipient<?> recipient = (HttpRecipient<?>) job.getRecipient();
        HttpRecipientPayloadData<?> payloadData = recipient.getPayload();
        assertThat(payloadData)
                .isNotNull()
                .isExactlyInstanceOf(HttpRecipientBinaryPayloadData.class);
        assertThat(recipient.getPayload().getData()).isEqualTo(RECIPIENT_PAYLOAD);
        assertThat(recipient.getUrl()).isEqualTo(RECIPIENT_URL);
        assertThat(recipient.getMethod()).isEqualTo(RECIPIENT_METHOD);
        assertThat(recipient.getHeaders())
                .hasSize(1)
                .containsEntry(RECIPIENT_HEADER_1, RECIPIENT_HEADER_1_VALUE);
        assertThat(recipient.getQueryParams())
                .hasSize(2)
                .containsEntry(RECIPIENT_QUERY_PARAM_1, RECIPIENT_QUERY_PARAM_1_VALUE)
                .containsEntry(RECIPIENT_QUERY_PARAM_2, RECIPIENT_QUERY_PARAM_2_VALUE);
    }

    private static void assertDeleteJobEvent(JobCloudEvent<?> result) {
        assertThat(result).isInstanceOf(DeleteJobEvent.class);
        DeleteJobEvent event = (DeleteJobEvent) result;
        assertBaseFields(event);
        assertThat(event.getData()).isInstanceOf(JobLookupId.class);
        JobLookupId lookupId = event.getData();
        assertThat(lookupId.getCorrelationId()).isEqualTo(CORRELATION_ID);
    }

    private static void assertBaseFields(JobCloudEvent<?> event) {
        assertThat(event.getId()).isEqualTo(ID);
        assertThat(event.getSpecVersion()).isEqualTo(SPEC_VERSION);
        assertThat(event.getSource()).isEqualTo(SOURCE);
        assertThat(event.getTime()).isEqualTo(TIME);
        assertThat(event.getSubject()).isEqualTo(SUBJECT);
    }

    private static byte[] readFileContent(String resource) throws Exception {
        URL url = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(resource), "Required test resource was not found in class path: " + resource);
        Path path = Paths.get(url.toURI());
        return Files.readAllBytes(path);
    }
}

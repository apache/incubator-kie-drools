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

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.api.event.TestConstants.CORRELATION_ID;
import static org.kie.kogito.jobs.service.api.event.TestConstants.ID;
import static org.kie.kogito.jobs.service.api.event.TestConstants.JOB_ID;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_HEADER_1;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_HEADER_1_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_METHOD;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_PAYLOAD;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_1;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_1_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_2;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_QUERY_PARAM_2_VALUE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.RECIPIENT_URL;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SCHEDULE_DELAY;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SCHEDULE_DELAY_UNIT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SCHEDULE_REPEAT_COUNT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SCHEDULE_START_TIME;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SOURCE;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SPEC_VERSION;
import static org.kie.kogito.jobs.service.api.event.TestConstants.SUBJECT;
import static org.kie.kogito.jobs.service.api.event.TestConstants.TIME;
import static org.kie.kogito.jobs.service.api.event.TestConstants.buildJob;

class JobCloudEventSerializerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JobCloudEventSerializer serializer;

    @BeforeEach
    void setUp() {
        this.serializer = new JobCloudEventSerializer();
    }

    @Test
    void serializeCreateJobEvent() throws Exception {
        CreateJobEvent event = CreateJobEvent.builder()
                .id(ID)
                .source(SOURCE)
                .time(TIME)
                .subject(SUBJECT)
                .job(buildJob())
                .build();

        String json = serializer.serialize(event);
        assertThat(json).isNotNull();

        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
        assertHasTotalFields(jsonNode, 7);
        assertHasFieldWithValue(jsonNode, "type", CreateJobEvent.TYPE);
        assertHasBaseFields(jsonNode);

        JsonNode dataJsonNode = jsonNode.get("data");
        assertThat(dataJsonNode).isNotNull();
        assertHasTotalFields(dataJsonNode, 6);
        assertHasFieldWithValue(dataJsonNode, "id", null);
        assertHasFieldWithValue(dataJsonNode, "correlationId", CORRELATION_ID);
        assertHasFieldWithValue(dataJsonNode, "state", null);

        JsonNode scheduleJsonNode = dataJsonNode.get("schedule");
        assertThat(scheduleJsonNode).isNotNull();
        assertHasTotalFields(scheduleJsonNode, 5);
        assertHasFieldWithValue(scheduleJsonNode, "type", "timer");
        assertHasFieldWithValue(scheduleJsonNode, "startTime", SCHEDULE_START_TIME.toString());
        assertHasFieldWithValue(scheduleJsonNode, "repeatCount", String.valueOf(SCHEDULE_REPEAT_COUNT));
        assertHasFieldWithValue(scheduleJsonNode, "delay", String.valueOf(SCHEDULE_DELAY));
        assertHasFieldWithValue(scheduleJsonNode, "delayUnit", String.valueOf(SCHEDULE_DELAY_UNIT));

        JsonNode recipientJsonNode = dataJsonNode.get("recipient");
        assertThat(recipientJsonNode).isNotNull();
        assertHasTotalFields(recipientJsonNode, 6);
        assertHasFieldWithValue(recipientJsonNode, "type", "http");
        assertHasFieldWithValue(recipientJsonNode, "url", RECIPIENT_URL);
        assertHasFieldWithValue(recipientJsonNode, "method", RECIPIENT_METHOD);

        JsonNode payloadJsonNode = recipientJsonNode.get("payload");
        assertHasFieldWithValue(payloadJsonNode, "type", "binary");
        assertHasFieldWithValue(payloadJsonNode, "data", Base64.getEncoder().encodeToString(RECIPIENT_PAYLOAD));

        JsonNode headersJsonNode = recipientJsonNode.get("headers");
        assertThat(headersJsonNode).isNotNull();
        assertHasTotalFields(headersJsonNode, 1);
        assertHasFieldWithValue(headersJsonNode, RECIPIENT_HEADER_1, RECIPIENT_HEADER_1_VALUE);

        JsonNode queryParamsJsonNode = recipientJsonNode.get("queryParams");
        assertThat(queryParamsJsonNode).isNotNull();
        assertHasTotalFields(queryParamsJsonNode, 2);
        assertHasFieldWithValue(queryParamsJsonNode, RECIPIENT_QUERY_PARAM_1, RECIPIENT_QUERY_PARAM_1_VALUE);
        assertHasFieldWithValue(queryParamsJsonNode, RECIPIENT_QUERY_PARAM_2, RECIPIENT_QUERY_PARAM_2_VALUE);
    }

    @Test
    void serializeDeleteJobEvent() throws Exception {
        DeleteJobEvent event = DeleteJobEvent.builder()
                .id(ID)
                .source(SOURCE)
                .time(TIME)
                .subject(SUBJECT)
                .lookupId(JobLookupId.fromId(JOB_ID))
                .build();
        String json = serializer.serialize(event);
        assertThat(json).isNotNull();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
        assertHasTotalFields(jsonNode, 7);
        assertHasFieldWithValue(jsonNode, "type", DeleteJobEvent.TYPE);
        assertHasBaseFields(jsonNode);
        JsonNode dataJsonNode = jsonNode.get("data");
        assertThat(dataJsonNode).isNotNull();
        assertHasTotalFields(dataJsonNode, 2);
        assertHasFieldWithValue(dataJsonNode, "id", JOB_ID);
        assertHasFieldWithValue(dataJsonNode, "correlationId", null);
    }

    private static void assertHasBaseFields(JsonNode jsonNode) {
        assertHasFieldWithValue(jsonNode, "id", ID);
        assertHasFieldWithValue(jsonNode, "source", SOURCE.toString());
        assertHasFieldWithValue(jsonNode, "subject", SUBJECT);
        assertHasFieldWithValue(jsonNode, "specversion", SPEC_VERSION.toString());
    }

    private static void assertHasFieldWithValue(JsonNode jsonNode, String fieldName, String value) {
        assertThat(jsonNode.has(fieldName)).isTrue();
        JsonNode field = jsonNode.get(fieldName);
        assertThat(field).isNotNull();
        if (value != null) {
            assertThat(field.asText()).isEqualTo(value);
        } else {
            assertThat(field.isNull()).isTrue();
        }
    }

    private static void assertHasTotalFields(JsonNode jsonNode, int totalFields) {
        assertThat(jsonNode.size()).isEqualTo(totalFields);
    }
}

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

package org.kie.kogito.jobs.api.event.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.ADDONS;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_ID;
import static org.kie.kogito.event.cloudevents.CloudEventExtensionConstants.PROCESS_ROOT_PROCESS_INSTANCE_ID;
import static org.kie.kogito.jobs.api.event.CancelJobRequestEvent.CANCEL_JOB_REQUEST;
import static org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.CALLBACK_ENDPOINT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.EXPIRATION_TIME;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.JOB_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.KOGITO_ADDONS_VALUE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.NODE_INSTANCE_ID;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PRIORITY;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PROCESS_ID_VALUE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.PROCESS_INSTANCE_ID_VALUE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.REPEAT_INTERVAL;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.REPEAT_LIMIT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ROOT_PROCESS_ID_VALUE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.ROOT_PROCESS_INSTANCE_ID_VALUE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SOURCE;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SPEC_VERSION;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.SUBJECT;
import static org.kie.kogito.jobs.api.event.serialization.SerializationTestConstants.TIME;

class JobCloudEventSerializerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JobCloudEventSerializer serializer;

    @BeforeEach
    void setUp() {
        this.serializer = new JobCloudEventSerializer();
    }

    @Test
    void serializeCancelJobRequestEvent() throws Exception {
        CancelJobRequestEvent event = CancelJobRequestEvent.builder()
                .id(ID)
                .specVersion(SPEC_VERSION)
                .source(SOURCE)
                .time(TIME)
                .subject(SUBJECT)
                .processInstanceId(PROCESS_INSTANCE_ID_VALUE)
                .processId(PROCESS_ID_VALUE)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID_VALUE)
                .rootProcessId(ROOT_PROCESS_ID_VALUE)
                .kogitoAddons(KOGITO_ADDONS_VALUE)
                .jobId(JOB_ID)
                .build();
        String json = serializer.serialize(event);
        assertThat(json).isNotNull();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
        assertHasTotalFields(jsonNode, 12);
        assertHasFieldWithValue(jsonNode, "type", CANCEL_JOB_REQUEST);
        assertHasBaseFields(jsonNode);
        assertHasProcessContextFields(jsonNode);
        JsonNode dataJsonNode = jsonNode.get("data");
        assertThat(dataJsonNode).isNotNull();
        assertHasTotalFields(dataJsonNode, 1);
        assertHasFieldWithValue(dataJsonNode, "id", JOB_ID);
    }

    @Test
    void serializeCreateProcessInstanceJobRequestEvent() throws Exception {
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .id(ID)
                .specVersion(SPEC_VERSION)
                .source(SOURCE)
                .time(TIME)
                .subject(SUBJECT)
                .processInstanceId(PROCESS_INSTANCE_ID_VALUE)
                .processId(PROCESS_ID_VALUE)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID_VALUE)
                .rootProcessId(ROOT_PROCESS_ID_VALUE)
                .kogitoAddons(KOGITO_ADDONS_VALUE)
                .job(new Job(JOB_ID,
                        EXPIRATION_TIME,
                        SerializationTestConstants.PRIORITY,
                        CALLBACK_ENDPOINT,
                        PROCESS_INSTANCE_ID_VALUE,
                        ROOT_PROCESS_INSTANCE_ID_VALUE,
                        PROCESS_ID_VALUE,
                        ROOT_PROCESS_ID_VALUE,
                        REPEAT_INTERVAL,
                        REPEAT_LIMIT,
                        NODE_INSTANCE_ID))
                .build();
        String json = serializer.serialize(event);
        assertThat(json).isNotNull();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
        assertHasTotalFields(jsonNode, 12);
        assertHasFieldWithValue(jsonNode, "type", CREATE_PROCESS_INSTANCE_JOB_REQUEST);
        assertHasBaseFields(jsonNode);
        assertHasProcessContextFields(jsonNode);
        JsonNode dataJsonNode = jsonNode.get("data");
        assertThat(dataJsonNode).isNotNull();
        assertHasTotalFields(dataJsonNode, 11);
        assertHasFieldWithValue(dataJsonNode, "id", JOB_ID);
        assertHasFieldWithValue(dataJsonNode, "expirationTime", EXPIRATION_TIME.toString());
        assertHasFieldWithValue(dataJsonNode, "priority", Integer.toString(PRIORITY));
        assertHasFieldWithValue(dataJsonNode, "callbackEndpoint", CALLBACK_ENDPOINT);
        assertHasFieldWithValue(dataJsonNode, "processInstanceId", PROCESS_INSTANCE_ID_VALUE);
        assertHasFieldWithValue(dataJsonNode, "rootProcessInstanceId", ROOT_PROCESS_INSTANCE_ID_VALUE);
        assertHasFieldWithValue(dataJsonNode, "processId", PROCESS_ID_VALUE);
        assertHasFieldWithValue(dataJsonNode, "rootProcessId", ROOT_PROCESS_ID_VALUE);
        assertHasFieldWithValue(dataJsonNode, "nodeInstanceId", NODE_INSTANCE_ID);
        assertHasFieldWithValue(dataJsonNode, "repeatInterval", Long.toString(REPEAT_INTERVAL));
        assertHasFieldWithValue(dataJsonNode, "repeatLimit", Integer.toString(REPEAT_LIMIT));
    }

    private static void assertHasBaseFields(JsonNode jsonNode) {
        assertHasFieldWithValue(jsonNode, "id", ID);
        assertHasFieldWithValue(jsonNode, "source", SOURCE.toString());
        assertHasFieldWithValue(jsonNode, "subject", SUBJECT);
        assertHasFieldWithValue(jsonNode, "specversion", SPEC_VERSION);
    }

    private static void assertHasProcessContextFields(JsonNode jsonNode) {
        assertHasFieldWithValue(jsonNode, PROCESS_INSTANCE_ID, PROCESS_INSTANCE_ID_VALUE);
        assertHasFieldWithValue(jsonNode, PROCESS_ID, PROCESS_ID_VALUE);
        assertHasFieldWithValue(jsonNode, PROCESS_ROOT_PROCESS_INSTANCE_ID, ROOT_PROCESS_INSTANCE_ID_VALUE);
        assertHasFieldWithValue(jsonNode, PROCESS_ROOT_PROCESS_ID, ROOT_PROCESS_ID_VALUE);
        assertHasFieldWithValue(jsonNode, ADDONS, KOGITO_ADDONS_VALUE);
    }

    private static void assertHasFieldWithValue(JsonNode jsonNode, String fieldName, String value) {
        JsonNode field = jsonNode.get(fieldName);
        assertThat(field).isNotNull();
        assertThat(field.asText()).isEqualTo(value);
    }

    private static void assertHasTotalFields(JsonNode jsonNode, int totalFields) {
        assertThat(jsonNode.size()).isEqualTo(totalFields);
    }
}

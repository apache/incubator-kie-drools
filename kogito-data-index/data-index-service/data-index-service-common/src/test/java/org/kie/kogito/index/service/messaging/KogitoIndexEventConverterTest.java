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
package org.kie.kogito.index.service.messaging;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.process.NodeDefinition;
import org.kie.kogito.event.process.ProcessDefinitionDataEvent;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceStateDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.json.JsonUtils;
import org.kie.kogito.index.json.ObjectMapperProducer;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.SpecVersion;
import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

import jakarta.ws.rs.core.HttpHeaders;

import static io.cloudevents.core.v1.CloudEventV1.DATACONTENTTYPE;
import static io.cloudevents.core.v1.CloudEventV1.DATASCHEMA;
import static io.cloudevents.core.v1.CloudEventV1.ID;
import static io.cloudevents.core.v1.CloudEventV1.SOURCE;
import static io.cloudevents.core.v1.CloudEventV1.SPECVERSION;
import static io.cloudevents.core.v1.CloudEventV1.SUBJECT;
import static io.cloudevents.core.v1.CloudEventV1.TIME;
import static io.cloudevents.core.v1.CloudEventV1.TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.index.test.TestUtils.readFileContent;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class KogitoIndexEventConverterTest {

    private static final String PROCESS_INSTANCE_STATE_EVENT_TYPE = "ProcessInstanceStateDataEvent";
    private static final String USER_TASK_INSTANCE_STATE_EVENT_TYPE = "UserTaskInstanceStateDataEvent";
    private static final String JOB_EVENT_TYPE = "JobEvent";
    private static final String EVENT_ID = "ID";
    private static final URI EVENT_SOURCE = URI.create("http://localhost:8080/travels");
    private static final OffsetDateTime EVENT_TIME = OffsetDateTime.parse("2022-03-18T15:33:05.608395+10:00");
    private static final URI EVENT_DATA_SCHEMA = URI.create("http://my_event_data_schema/my_schema.json");
    private static final String EVENT_DATA_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String EVENT_SUBJECT = "SUBJECT";
    private static final String STRUCTURED_PROCESS_INSTANCE_CLOUD_EVENT = "process_instance_event.json";
    private static final String BINARY_PROCESS_INSTANCE_CLOUD_EVENT_DATA = "binary_process_instance_event_data.json";
    private static final String BINARY_USER_TASK_INSTANCE_CLOUD_EVENT_DATA = "binary_user_task_instance_state_event_data.json";
    private static final String BINARY_KOGITO_JOB_CLOUD_EVENT_DATA = "binary_job_event_data.json";
    private static final String STRUCTURED_PROCESS_DEFINITION_CLOUD_EVENT = "process_definition_event.json";
    private static final String BINARY_PROCESS_DEFINITION_CLOUD_EVENT = "binary_process_definition_event.json";

    @Mock
    private IncomingHttpMetadata httpMetadata;
    private MultiMap headers;
    private KogitoIndexEventConverter converter;

    @BeforeEach
    void setUp() {
        headers = MultiMap.caseInsensitiveMultiMap();
        lenient().doReturn(headers).when(httpMetadata).getHeaders();
        converter = new KogitoIndexEventConverter();
        ObjectMapper objectMapper = JsonUtils.getObjectMapper();
        new ObjectMapperProducer().customize(objectMapper);
        converter.setObjectMapper(objectMapper);
        converter.init();
    }

    @Test
    void canConvertBufferPayload() {
        Buffer buffer = Buffer.buffer("{}");
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        assertThat(converter.canConvert(message, ProcessInstanceDataEvent.class)).isTrue();
        assertThat(converter.canConvert(message, UserTaskInstanceDataEvent.class)).isTrue();
        assertThat(converter.canConvert(message, KogitoJobCloudEvent.class)).isTrue();
    }

    @Test
    void canConvertNotBufferPayload() {
        assertThat(converter.canConvert(Message.of(new ProcessInstanceDataEvent<>(), Metadata.of(httpMetadata)),
                ProcessInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(new UserTaskInstanceDataEvent<>(), Metadata.of(httpMetadata)),
                UserTaskInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(KogitoJobCloudEvent.builder().build(), Metadata.of(httpMetadata)),
                KogitoJobCloudEvent.class)).isFalse();
    }

    @Test
    void convertBinaryProcessDefinitionDataEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_PROCESS_DEFINITION_CLOUD_EVENT));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        // set ce-xxx headers for the binary format.
        headers.add(ceHeader(SPECVERSION), SpecVersion.V1.toString());
        headers.add(ceHeader(ID), EVENT_ID);
        headers.add(ceHeader(SOURCE), EVENT_SOURCE.toString());
        headers.add(ceHeader(TYPE), ProcessDefinitionDataEvent.PROCESS_DEFINITION_EVENT);
        headers.add(ceHeader(TIME), EVENT_TIME.toString());
        headers.add(ceHeader(DATASCHEMA), EVENT_DATA_SCHEMA.toString());
        headers.add(ceHeader(DATACONTENTTYPE), EVENT_DATA_CONTENT_TYPE);
        headers.add(ceHeader(SUBJECT), EVENT_SUBJECT);

        Message<?> result = converter.convert(message, ProcessDefinitionDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessDefinitionDataEvent.class);
        ProcessDefinitionDataEvent cloudEvent = (ProcessDefinitionDataEvent) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(cloudEvent.getSpecVersion().toString()).isEqualTo(SpecVersion.V1.toString());
        assertThat(cloudEvent.getSource().toString()).isEqualTo(EVENT_SOURCE.toString());
        assertThat(cloudEvent.getType()).isEqualTo(ProcessDefinitionDataEvent.PROCESS_DEFINITION_EVENT);
        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME);
        assertThat(cloudEvent.getDataSchema()).isEqualTo(EVENT_DATA_SCHEMA);
        assertThat(cloudEvent.getDataContentType()).isEqualTo(EVENT_DATA_CONTENT_TYPE);
        assertThat(cloudEvent.getSubject()).isEqualTo(EVENT_SUBJECT);
    }

    private static Map<String, String> getMetadata() {
        return toStringMap(
                Map.of("Description", "JSON based greeting workflow",
                        "annotations", getAnnotations(),
                        "Tags", getAnnotations()));
    }

    private List<Node> getNodes() {
        try {
            List<NodeDefinition> nodes = ObjectMapperFactory.get()
                    .readerForListOf(NodeDefinition.class)
                    .readValue(readFileContent("nodes_definitions.json"));
            return nodes.stream().map(definition -> {
                Node node = new Node();
                node.setId(definition.getId());
                node.setName(definition.getName());
                node.setUniqueId(definition.getUniqueId());
                node.setType(definition.getType());
                node.setMetadata(toStringMap(definition.getMetadata()));
                return node;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getAnnotations() {
        return List.of("test1", "test2", "test3");
    }

    private static Map<String, String> toStringMap(Map<String, ?> input) {
        if (input == null) {
            return null;
        }
        return input.entrySet().stream()
                .map(entry -> {
                    if (String.class.isInstance(entry.getValue())) {
                        return entry;
                    }
                    String value = null;
                    try {
                        value = JsonUtils.getObjectMapper().writeValueAsString(entry.getValue());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), value);
                }).collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue()));
    }

    @Test
    void convertStructuredProcessDefinitionDataEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(STRUCTURED_PROCESS_DEFINITION_CLOUD_EVENT));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));

        // set ce header for the structured format.
        headers.add(HttpHeaders.CONTENT_TYPE, "application/cloudevents+json");

        Message<?> result = converter.convert(message, ProcessDefinitionDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessDefinitionDataEvent.class);
        ProcessDefinitionDataEvent cloudEvent = (ProcessDefinitionDataEvent) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo("717af02d-645a-4b27-8058-b67ff1fa8edb");
        assertThat(cloudEvent.getSpecVersion().toString()).isEqualTo(SpecVersion.V1.toString());
        assertThat(cloudEvent.getSource().toString()).isEqualTo("http://localhost:8080/jsongreet");
        assertThat(cloudEvent.getType()).isEqualTo(ProcessDefinitionDataEvent.PROCESS_DEFINITION_EVENT);
        assertThat(cloudEvent.getTime()).isEqualTo("2023-10-19T10:18:01.540311-03:00");
    }

    @Test
    void convertBinaryProcessInstanceDataEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_PROCESS_INSTANCE_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));

        // set ce-xxx headers for the binary format.
        headers.add(ceHeader(SPECVERSION), SpecVersion.V1.toString());
        headers.add(ceHeader(ID), EVENT_ID);
        headers.add(ceHeader(SOURCE), EVENT_SOURCE.toString());
        headers.add(ceHeader(TYPE), PROCESS_INSTANCE_STATE_EVENT_TYPE);
        headers.add(ceHeader(TIME), EVENT_TIME.toString());
        headers.add(ceHeader(DATASCHEMA), EVENT_DATA_SCHEMA.toString());
        headers.add(ceHeader(DATACONTENTTYPE), EVENT_DATA_CONTENT_TYPE);
        headers.add(ceHeader(SUBJECT), EVENT_SUBJECT);

        Message<?> result = converter.convert(message, ProcessInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceStateDataEvent cloudEvent = (ProcessInstanceStateDataEvent) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(cloudEvent.getSpecVersion().toString()).isEqualTo(SpecVersion.V1.toString());
        assertThat(cloudEvent.getSource().toString()).isEqualTo(EVENT_SOURCE.toString());
        assertThat(cloudEvent.getType()).isEqualTo(PROCESS_INSTANCE_STATE_EVENT_TYPE);
        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME);
        assertThat(cloudEvent.getDataSchema()).isEqualTo(EVENT_DATA_SCHEMA);
        assertThat(cloudEvent.getDataContentType()).isEqualTo(EVENT_DATA_CONTENT_TYPE);
        assertThat(cloudEvent.getSubject()).isEqualTo(EVENT_SUBJECT);
    }

    @Test
    void convertStructuredProcessInstanceDataEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(STRUCTURED_PROCESS_INSTANCE_CLOUD_EVENT));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));

        // set ce header for the structured format.
        headers.add(HttpHeaders.CONTENT_TYPE, "application/cloudevents+json");

        Message<?> result = converter.convert(message, ProcessInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent<?> cloudEvent = (ProcessInstanceDataEvent<?>) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo("867ff7b4-2e49-49b3-882a-76f65a2c4124");
        assertThat(cloudEvent.getSpecVersion().toString()).isEqualTo(SpecVersion.V1.toString());
        assertThat(cloudEvent.getSource().toString()).isEqualTo(EVENT_SOURCE.toString());
        assertThat(cloudEvent.getType()).isEqualTo(PROCESS_INSTANCE_STATE_EVENT_TYPE);
        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME);
    }

    @Test
    void convertBinaryKogitoJobCloudEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_KOGITO_JOB_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));

        // set ce-xxx headers for the binary format.
        headers.add(ceHeader(SPECVERSION), SpecVersion.V1.toString());
        headers.add(ceHeader(ID), EVENT_ID);
        headers.add(ceHeader(SOURCE), EVENT_SOURCE.toString());
        headers.add(ceHeader(TYPE), JOB_EVENT_TYPE);
        headers.add(ceHeader(TIME), EVENT_TIME.toString());
        headers.add(ceHeader(DATASCHEMA), EVENT_DATA_SCHEMA.toString());
        headers.add(ceHeader(DATACONTENTTYPE), EVENT_DATA_CONTENT_TYPE);
        headers.add(ceHeader(SUBJECT), EVENT_SUBJECT);

        Message<?> result = converter.convert(message, KogitoJobCloudEvent.class);
        assertThat(result.getPayload()).isInstanceOf(KogitoJobCloudEvent.class);
        KogitoJobCloudEvent cloudEvent = (KogitoJobCloudEvent) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(cloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V1.toString());
        assertThat(cloudEvent.getSource().toString()).isEqualTo(EVENT_SOURCE.toString());
        assertThat(cloudEvent.getType()).isEqualTo(JOB_EVENT_TYPE);
        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME.toZonedDateTime());
        assertThat(cloudEvent.getSchemaURL()).isEqualTo(EVENT_DATA_SCHEMA);
        assertThat(cloudEvent.getContentType()).isEqualTo(EVENT_DATA_CONTENT_TYPE);
        assertThat(cloudEvent.getSubject()).isEqualTo(EVENT_SUBJECT);

        Job job = cloudEvent.getData();
        assertThat(job.getId()).isEqualTo("8350b8b6-c5d9-432d-a339-a9fc85f642d4_0");
        assertThat(job.getProcessId()).isEqualTo("timerscycle");
        assertThat(job.getProcessInstanceId()).isEqualTo("7c1d9b38-b462-47c5-8bf2-d9154f54957b");
        assertThat(job.getRootProcessId()).isEqualTo("root_process_id");
        assertThat(job.getRootProcessInstanceId()).isEqualTo("root_process_instance_id");
        assertThat(job.getNodeInstanceId()).isEqualTo("node_instance_id");
        assertThat(job.getRepeatInterval()).isEqualTo(1000);
        assertThat(job.getCallbackEndpoint())
                .isEqualTo("http://localhost:8080/management/jobs/timerscycle/instances/7c1d9b38-b462-47c5-8bf2-d9154f54957b/timers/8350b8b6-c5d9-432d-a339-a9fc85f642d4_0");
        assertThat(job.getScheduledId()).isEqualTo("1234");
        assertThat(job.getStatus()).isEqualTo("SCHEDULED");
        assertThat(job.getRepeatInterval()).isEqualTo(1000);
        assertThat(job.getRepeatLimit()).isEqualTo(2147483647);
        assertThat(job.getRetries()).isEqualTo(0);
        assertThat(job.getExecutionCounter()).isEqualTo(0);
    }

    @Test
    void convertBinaryUserTaskInstanceDataEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_USER_TASK_INSTANCE_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));

        // set ce-xxx headers for the binary format.
        headers.add(ceHeader(SPECVERSION), SpecVersion.V1.toString());
        headers.add(ceHeader(ID), EVENT_ID);
        headers.add(ceHeader(SOURCE), EVENT_SOURCE.toString());
        headers.add(ceHeader(TYPE), USER_TASK_INSTANCE_STATE_EVENT_TYPE);
        headers.add(ceHeader(TIME), EVENT_TIME.toString());
        headers.add(ceHeader(DATASCHEMA), EVENT_DATA_SCHEMA.toString());
        headers.add(ceHeader(DATACONTENTTYPE), EVENT_DATA_CONTENT_TYPE);
        headers.add(ceHeader(SUBJECT), EVENT_SUBJECT);

        Message<?> result = converter.convert(message, UserTaskInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(UserTaskInstanceStateDataEvent.class);
        UserTaskInstanceStateDataEvent cloudEvent = (UserTaskInstanceStateDataEvent) result.getPayload();

        assertThat(cloudEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(cloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(cloudEvent.getSource().toString()).isEqualTo(EVENT_SOURCE.toString());
        assertThat(cloudEvent.getType()).isEqualTo(USER_TASK_INSTANCE_STATE_EVENT_TYPE);
        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME);
        assertThat(cloudEvent.getDataSchema()).isEqualTo(EVENT_DATA_SCHEMA);
        assertThat(cloudEvent.getDataContentType()).isEqualTo(EVENT_DATA_CONTENT_TYPE);
        assertThat(cloudEvent.getSubject()).isEqualTo(EVENT_SUBJECT);
    }

    @Test
    void convertFailureBinaryUnexpectedBufferContent() {
        Buffer buffer = Buffer.buffer("unexpected Content");
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> converter.convert(message, ProcessInstanceDataEvent.class));
    }

    private static String ceHeader(String name) {
        return "ce-" + name;
    }
}

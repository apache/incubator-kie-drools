/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.service.messaging;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.ProcessInstanceEventMapper;
import org.kie.kogito.index.event.UserTaskInstanceEventMapper;
import org.kie.kogito.index.json.ObjectMapperProducer;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.index.test.TestUtils.readFileContent;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class KogitoIndexEventConverterTest {

    private static final String BINARY_PROCESS_INSTANCE_CLOUD_EVENT_DATA = "process_instance_event.json";
    private static final String BINARY_PROCESS_INSTANCE_CLOUD_EVENT_BODY_DATA = "binary_process_instance_event_data.json";
    private static final String BINARY_USER_TASK_INSTANCE_CLOUD_EVENT_DATA = "binary_user_task_instance_event_data.json";
    private static final String BINARY_KOGITO_JOB_CLOUD_EVENT_DATA = "binary_job_event_data.json";
    @Mock
    IncomingHttpMetadata httpMetadata;

    private MultiMap headers;

    private KogitoIndexEventConverter converter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        headers = MultiMap.caseInsensitiveMultiMap();
        lenient().doReturn(headers).when(httpMetadata).getHeaders();
        converter = new KogitoIndexEventConverter();
        objectMapper = new ObjectMapper();
        new ObjectMapperProducer().customize(objectMapper);
        converter.setObjectMapper(objectMapper);
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
        assertThat(converter.canConvert(Message.of(new ProcessInstanceDataEvent(), Metadata.of(httpMetadata)),
                ProcessInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(new UserTaskInstanceDataEvent(), Metadata.of(httpMetadata)),
                UserTaskInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(KogitoJobCloudEvent.builder().build(), Metadata.of(httpMetadata)),
                KogitoJobCloudEvent.class)).isFalse();
    }

    @Test
    void convertBinaryCloudProcessInstanceEventBody() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_PROCESS_INSTANCE_CLOUD_EVENT_BODY_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, ProcessInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent cloudEvent = (ProcessInstanceDataEvent) result.getPayload();

        ProcessInstance pi = new ProcessInstanceEventMapper().apply(cloudEvent);
        assertThat(pi.getId()).isEqualTo("5f8b1a48-4d37-4bd2-a1a6-9b8f6097cfdd");
        assertThat(pi.getProcessId()).isEqualTo("subscription_flow");
        assertThat(pi.getProcessName()).isEqualTo("workflow");
        assertThat(pi.getVariables()).hasSize(1);
        assertThat(pi.getNodes()).hasSize(14);
        assertThat(pi.getState()).isEqualTo(1);
        assertThat(pi.getStart()).isEqualTo("2023-05-24T10:41:14.911Z");
        assertThat(pi.getEnd()).isNull();
        assertThat(pi.getMilestones()).isEmpty();
    }

    @Test
    void convertBinaryCloudProcessInstanceEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_PROCESS_INSTANCE_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, ProcessInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent cloudEvent = (ProcessInstanceDataEvent) result.getPayload();

        ProcessInstance pi = new ProcessInstanceEventMapper().apply(cloudEvent);
        assertThat(pi.getId()).isEqualTo("2308e23d-9998-47e9-a772-a078cf5b891b");
        assertThat(pi.getProcessId()).isEqualTo("travels");
        assertThat(pi.getProcessName()).isEqualTo("travels");
        assertThat(pi.getVariables()).hasSize(3);
        assertThat(pi.getNodes()).hasSize(5);
        assertThat(pi.getState()).isEqualTo(1);
        assertThat(pi.getStart()).isEqualTo("2022-03-18T05:32:21.887Z");
        assertThat(pi.getEnd()).isNull();
        assertThat(pi.getMilestones()).isEmpty();
    }

    @Test
    void convertBinaryCloudKogitoJobEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_KOGITO_JOB_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, KogitoJobCloudEvent.class);
        assertThat(result.getPayload()).isInstanceOf(KogitoJobCloudEvent.class);
        KogitoJobCloudEvent cloudEvent = (KogitoJobCloudEvent) result.getPayload();

        Job job = cloudEvent.getData();
        assertThat(job.getId()).isEqualTo("8350b8b6-c5d9-432d-a339-a9fc85f642d4_0");
        assertThat(job.getProcessId()).isEqualTo("timerscycle");
        assertThat(job.getProcessInstanceId()).isEqualTo("7c1d9b38-b462-47c5-8bf2-d9154f54957b");
        assertThat(job.getRepeatInterval()).isEqualTo(1000l);
        assertThat(job.getCallbackEndpoint())
                .isEqualTo("http://localhost:8080/management/jobs/timerscycle/instances/7c1d9b38-b462-47c5-8bf2-d9154f54957b/timers/8350b8b6-c5d9-432d-a339-a9fc85f642d4_0");
        assertThat(job.getScheduledId()).isEqualTo("0");
        assertThat(job.getStatus()).isEqualTo("SCHEDULED");

    }

    @Test
    void convertBinaryCloudUserTaskInstanceEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_USER_TASK_INSTANCE_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, UserTaskInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(UserTaskInstanceDataEvent.class);
        UserTaskInstanceDataEvent cloudEvent = (UserTaskInstanceDataEvent) result.getPayload();

        UserTaskInstance userTaskInstance = new UserTaskInstanceEventMapper().apply(cloudEvent);
        assertThat(userTaskInstance.getId()).isEqualTo("45fae435-b098-4f27-97cf-a0c107072e8b");

        assertThat(userTaskInstance.getInputs().size()).isEqualTo(6);
        assertThat(userTaskInstance.getName()).isEqualTo("VisaApplication");
        assertThat(userTaskInstance.getState()).isEqualTo("Completed");
    }

    @Test
    void convertFailureBinaryUnexpectedBufferContent() throws Exception {
        Buffer buffer = Buffer.buffer("unexpected Content");
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> converter.convert(message, ProcessInstanceDataEvent.class));
    }
}

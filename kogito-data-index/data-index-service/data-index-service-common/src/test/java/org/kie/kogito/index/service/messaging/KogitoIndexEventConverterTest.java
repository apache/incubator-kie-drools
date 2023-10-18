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

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.mapper.ProcessInstanceStateDataEventMerger;
import org.kie.kogito.index.json.ObjectMapperProducer;
import org.kie.kogito.index.model.ProcessInstance;
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
        assertThat(converter.canConvert(Message.of(new ProcessInstanceDataEvent<>(), Metadata.of(httpMetadata)),
                ProcessInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(new UserTaskInstanceDataEvent<>(), Metadata.of(httpMetadata)),
                UserTaskInstanceDataEvent.class)).isFalse();
        assertThat(converter.canConvert(Message.of(KogitoJobCloudEvent.builder().build(), Metadata.of(httpMetadata)),
                KogitoJobCloudEvent.class)).isFalse();
    }

    @Test
    void convertBinaryCloudProcessInstanceEvent() throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(BINARY_PROCESS_INSTANCE_CLOUD_EVENT_DATA));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, ProcessInstanceDataEvent.class);
        assertThat(result.getPayload()).isInstanceOf(ProcessInstanceStateDataEvent.class);
        ProcessInstanceStateDataEvent cloudEvent = (ProcessInstanceStateDataEvent) result.getPayload();

        ProcessInstance pi = new ProcessInstance();
        new ProcessInstanceStateDataEventMerger().merge(pi, cloudEvent);
        assertThat(pi.getId()).isEqualTo("2308e23d-9998-47e9-a772-a078cf5b891b");
        assertThat(pi.getProcessId()).isEqualTo("travels");
        assertThat(pi.getProcessName()).isEqualTo("travels");
        assertThat(pi.getState()).isEqualTo(1);
        assertThat(pi.getStart()).isEqualTo("2022-03-18T05:32:21.887Z");
        assertThat(pi.getEnd()).isNull();
    }

    @Test
    void convertFailureBinaryUnexpectedBufferContent() throws Exception {
        Buffer buffer = Buffer.buffer("unexpected Content");
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> converter.convert(message, ProcessInstanceDataEvent.class));
    }
}

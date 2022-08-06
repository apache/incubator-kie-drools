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

package org.kie.kogito.jobs.service.messaging;

import java.net.URI;
import java.time.OffsetDateTime;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.quarkus.reactivemessaging.http.runtime.IncomingHttpMetadata;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import static io.cloudevents.core.v1.CloudEventV1.DATACONTENTTYPE;
import static io.cloudevents.core.v1.CloudEventV1.DATASCHEMA;
import static io.cloudevents.core.v1.CloudEventV1.ID;
import static io.cloudevents.core.v1.CloudEventV1.SOURCE;
import static io.cloudevents.core.v1.CloudEventV1.SPECVERSION;
import static io.cloudevents.core.v1.CloudEventV1.SUBJECT;
import static io.cloudevents.core.v1.CloudEventV1.TIME;
import static io.cloudevents.core.v1.CloudEventV1.TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.jobs.service.TestUtils.readFileContent;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CloudEventConverterTest {

    private static final String STRUCTURED_CLOUD_EVENT = "org/kie/kogito/jobs/service/messaging/StructuredCloudEvent.json";
    private static final String BINARY_CLOUD_EVENT_DATA = "org/kie/kogito/jobs/service/messaging/BinaryCloudEventData.json";

    private static final String EVENT_ID = "ID";
    private static final URI EVENT_SOURCE = URI.create("http://my_event_source");
    private static final String EVENT_TYPE = "TYPE";
    private static final OffsetDateTime EVENT_TIME = OffsetDateTime.parse("2022-07-27T15:01:20.001+01:00");
    private static final URI EVENT_DATA_SCHEMA = URI.create("http://my_event_data_schema/my_schema.json");
    private static final String EVENT_DATA_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String EVENT_SUBJECT = "SUBJECT";

    private static final String EXTENSION1_NAME = "extension1";
    private static final String EXTENSION1_VALUE = "EXTENSION_1";
    private static final String EXTENSION2_NAME = "extension2";
    private static final String EXTENSION2_VALUE = "EXTENSION_2";

    private static final String DATA_FIELD1 = "dataField1";
    private static final String DATA_FIELD1_VALUE = "DATA_FIELD_1";
    private static final String DATA_FIELD2 = "dataField2";
    private static final String DATA_FIELD2_VALUE = "DATA_FIELD_2";

    @Mock
    IncomingHttpMetadata httpMetadata;

    private MultiMap headers;

    private CloudEventConverter converter;

    @BeforeEach
    void setUp() {
        headers = MultiMap.caseInsensitiveMultiMap();
        lenient().doReturn(headers).when(httpMetadata).getHeaders();
        converter = new CloudEventConverter();
    }

    @Test
    void canConvertHttpMetadataIsPresent() {
        Buffer buffer = Buffer.buffer("{}");
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        assertThat(converter.canConvert(message, CloudEvent.class)).isTrue();
    }

    @Test
    void canConvertHttpMetadataIsNotPresent() {
        Buffer buffer = Buffer.buffer("{}");
        Message<?> message = Message.of(buffer);
        assertThat(converter.canConvert(message, CloudEvent.class)).isFalse();
    }

    @Test
    void convertStructuredCloudEvent() throws Exception {
        headers.add(HttpHeaders.CONTENT_TYPE, "application/cloudevents+json");
        convert(STRUCTURED_CLOUD_EVENT);
    }

    @Test
    void convertBinaryCloudEvent() throws Exception {
        headers.add(ceHeader(SPECVERSION), SpecVersion.V1.toString());
        headers.add(ceHeader(ID), EVENT_ID);
        headers.add(ceHeader(SOURCE), EVENT_SOURCE.toString());
        headers.add(ceHeader(TYPE), EVENT_TYPE);

        headers.add(ceHeader(TIME), EVENT_TIME.toString());
        headers.add(ceHeader(DATASCHEMA), EVENT_DATA_SCHEMA.toString());
        headers.add(ceHeader(DATACONTENTTYPE), EVENT_DATA_CONTENT_TYPE);
        headers.add(ceHeader(SUBJECT), EVENT_SUBJECT);

        headers.add(ceHeader(EXTENSION1_NAME), EXTENSION1_VALUE);
        headers.add(ceHeader(EXTENSION2_NAME), EXTENSION2_VALUE);

        convert(BINARY_CLOUD_EVENT_DATA);
    }

    private void convert(String bufferContentResource) throws Exception {
        Buffer buffer = Buffer.buffer(readFileContent(bufferContentResource));
        Message<?> message = Message.of(buffer, Metadata.of(httpMetadata));
        Message<?> result = converter.convert(message, CloudEvent.class);
        assertThat(result.getPayload()).isInstanceOf(CloudEvent.class);
        CloudEvent cloudEvent = (CloudEvent) result.getPayload();

        assertSpecFields(cloudEvent);
        assertExtensionFields(cloudEvent);
        assertData(cloudEvent);
    }

    private void assertSpecFields(CloudEvent cloudEvent) {
        assertThat(cloudEvent.getSpecVersion()).isEqualTo(SpecVersion.V1);
        assertThat(cloudEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(cloudEvent.getSource()).isEqualTo(EVENT_SOURCE);
        assertThat(cloudEvent.getType()).isEqualTo(EVENT_TYPE);

        assertThat(cloudEvent.getTime()).isEqualTo(EVENT_TIME);
        assertThat(cloudEvent.getDataSchema()).isEqualTo(EVENT_DATA_SCHEMA);
        assertThat(cloudEvent.getDataContentType()).isEqualTo(EVENT_DATA_CONTENT_TYPE);
        assertThat(cloudEvent.getSubject()).isEqualTo(EVENT_SUBJECT);
    }

    private void assertExtensionFields(CloudEvent cloudEvent) {
        assertThat(cloudEvent.getExtensionNames()).containsExactlyInAnyOrder(EXTENSION1_NAME, EXTENSION2_NAME);
        assertThat(cloudEvent.getExtension(EXTENSION1_NAME)).isEqualTo(EXTENSION1_VALUE);
        assertThat(cloudEvent.getExtension(EXTENSION2_NAME)).isEqualTo(EXTENSION2_VALUE);
    }

    private void assertData(CloudEvent cloudEvent) {
        assertThat(cloudEvent.getData()).isNotNull();
        JsonObject dataObject = new JsonObject(Buffer.buffer(cloudEvent.getData().toBytes()));
        assertThat(dataObject).isNotNull();
        assertThat(dataObject.fieldNames()).containsExactlyInAnyOrder(DATA_FIELD1, DATA_FIELD2);
        assertThat(dataObject.getValue(DATA_FIELD1)).isEqualTo(DATA_FIELD1_VALUE);
        assertThat(dataObject.getValue(DATA_FIELD2)).isEqualTo(DATA_FIELD2_VALUE);
    }

    private static String ceHeader(String name) {
        return "ce-" + name;
    }
}

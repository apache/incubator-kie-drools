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
package org.kie.kogito.addon.quarkus.messaging.common;

import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.impl.ByteArrayCloudEventUnmarshallerFactory;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.SpecVersion;
import io.cloudevents.jackson.JsonFormat;
import io.smallrye.reactive.messaging.ce.CloudEventMetadata;
import io.smallrye.reactive.messaging.ce.DefaultCloudEventMetadataBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuarkusDefaultUnmarshallerTest {

    private ObjectMapper objectMapper;
    private ByteArrayCloudEventUnmarshallerFactory unmarshaller;
    private QuarkusCloudEventConverter<byte[], JsonNode> converter;

    @BeforeEach
    void setup() {
        objectMapper = ObjectMapperFactory.get().registerModule(JsonFormat.getCloudEventJacksonModule());
        unmarshaller = new ByteArrayCloudEventUnmarshallerFactory(objectMapper);
        converter = new QuarkusCloudEventConverter<>(unmarshaller.unmarshaller(JsonNode.class));
    }

    private byte[] getStructureCE(String specVersion, String type, String source, JsonNode data) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(objectMapper.createObjectNode().put("specversion", specVersion).put("id", "1").put("type", type).put("source", source)
                .put("timestamp", ZonedDateTime.now().toString()).set("data", data));
    }

    private CloudEventMetadata<?> getMetadata(String specVersion, String type, String source) {
        return new DefaultCloudEventMetadataBuilder<>().withId("1").withSpecVersion(specVersion).withType(type).withSource(URI.create(source)).build();
    }

    private JsonNode getPayload(String name) {
        return objectMapper.createObjectNode().put("name", name);
    }

    private <T> Message<T> getMessage(T payload, CloudEventMetadata<?> metadata) {
        Message<T> message = mock(Message.class);
        when(message.getPayload()).thenReturn(payload);
        when(message.getMetadata(CloudEventMetadata.class)).thenReturn(Optional.ofNullable(metadata));
        return message;
    }

    @Test
    void testStructureCloudEvent() throws IOException {
        Message<byte[]> message = getMessage(getStructureCE("1.0", "type", "/path", getPayload("Javierito")), null);
        DataEvent<JsonNode> ce = converter.convert(message);
        assertEquals("type", ce.getType());
        assertEquals("/path", ce.getSource().toString());
        assertEquals(SpecVersion.V1, ce.getSpecVersion());
        assertEquals("Javierito", ce.getData().get("name").asText());
    }

    @Test
    void testBynaryCloudEvent() throws IOException {
        Message<byte[]> message = getMessage(objectMapper.writeValueAsBytes(getPayload("Javierito")), getMetadata("0.3", "type", "/path"));
        DataEvent<JsonNode> ce = converter.convert(message);
        assertEquals("type", ce.getType());
        assertEquals("/path", ce.getSource().toString());
        assertEquals(SpecVersion.V03, ce.getSpecVersion());
        assertEquals("Javierito", ce.getData().get("name").asText());
    }

    @Test
    void testBynaryCEWithoutPayload() throws IOException {
        Message<byte[]> message = getMessage(null, getMetadata("0.3", "type", "/path"));
        DataEvent<JsonNode> ce = converter.convert(message);
        assertEquals("type", ce.getType());
        assertEquals("/path", ce.getSource().toString());
        assertEquals(SpecVersion.V03, ce.getSpecVersion());
    }
}

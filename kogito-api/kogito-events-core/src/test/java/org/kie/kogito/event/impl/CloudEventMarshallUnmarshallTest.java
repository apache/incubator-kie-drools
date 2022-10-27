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
package org.kie.kogito.event.impl;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.CloudEventUnmarshaller;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.DummyCloudEvent;
import org.kie.kogito.event.DummyEvent;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.jackson.JsonFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CloudEventMarshallUnmarshallTest {

    private static ObjectMapper mapper;

    @BeforeAll
    static void init() {
        mapper = ObjectMapperFactory.get().registerModule(JsonFormat.getCloudEventJacksonModule());
    }

    @Test
    void testStringMarshaller() throws IOException {
        testIt(new DummyCloudEvent(new DummyEvent("pepe"), "pepa"), new StringCloudEventMarshaller(mapper), new StringCloudEventUnmarshallerFactory(mapper));
    }

    @Test
    void testObjectMarshaller() throws IOException {
        testIt(new DummyCloudEvent(new DummyEvent("pepe"), "pepa"), new NoOpCloudEventMarshaller(mapper), new ObjectCloudEventUnmarshallerFactory(mapper));
    }

    @Test
    void testByteArrayMarshaller() throws IOException {
        testIt(new DummyCloudEvent(new DummyEvent("pepe"), "pepa"), new ByteArrayCloudEventMarshaller(mapper), new ByteArrayCloudEventUnmarshallerFactory(mapper));
    }

    private <T> void testIt(DataEvent<DummyEvent> event, CloudEventMarshaller<T> marshaller, CloudEventUnmarshallerFactory<T> unmarshallerFactory) throws IOException {
        CloudEventUnmarshaller<T, DummyEvent> unmarshaller = unmarshallerFactory.unmarshaller(DummyEvent.class);
        DataEvent<DummyEvent> targetEvent =
                DataEventFactory.from(unmarshaller.cloudEvent().convert(marshaller.marshall(event.asCloudEvent(marshaller.cloudEventDataFactory()))), unmarshaller.data());
        assertEquals(event.getId(), targetEvent.getId());
        assertEquals(event.getType(), targetEvent.getType());
        assertEquals(event.getSource(), targetEvent.getSource());
        assertEquals(event.getSpecVersion(), targetEvent.getSpecVersion());
        assertEquals(event.getSubject(), targetEvent.getSubject());
        assertEquals(event.getDataContentType(), targetEvent.getDataContentType());
        assertEquals(event.getDataSchema(), targetEvent.getDataSchema());
        assertEquals(event.getExtensionNames(), targetEvent.getExtensionNames());
        assertEquals(event.getTime(), targetEvent.getTime());
        assertEquals(event.getData(), targetEvent.getData());
    }
}

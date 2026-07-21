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
package org.kie.kogito.event.avro;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.impl.TestEvent;

import com.fasterxml.jackson.databind.JsonNode;

import static org.kie.kogito.event.impl.DataEventTestUtils.getJsonNode;
import static org.kie.kogito.event.impl.DataEventTestUtils.getJsonNodeCloudEvent;
import static org.kie.kogito.event.impl.DataEventTestUtils.getPojoCloudEvent;
import static org.kie.kogito.event.impl.DataEventTestUtils.getRawEvent;
import static org.kie.kogito.event.impl.DataEventTestUtils.testCloudEventMarshalling;
import static org.kie.kogito.event.impl.DataEventTestUtils.testEventMarshalling;

class AvroMarshallUnmarshallTest {

    private static AvroIO avroUtils;

    @BeforeAll
    static void init() throws IOException {
        avroUtils = new AvroIO();
    }

    @Test
    void testCloudEventMarshaller() throws IOException {
        testCloudEventMarshalling(getPojoCloudEvent(), TestEvent.class, new AvroCloudEventMarshaller(avroUtils), new AvroCloudEventUnmarshallerFactory(avroUtils));
    }

    @Test
    void testEventMarshaller() throws IOException {
        testEventMarshalling(getRawEvent(), new AvroEventMarshaller(avroUtils), new AvroEventUnmarshaller(avroUtils));
    }

    @Test
    void testJsonNodeMarshaller() throws IOException {
        testEventMarshalling(getJsonNode(), new AvroEventMarshaller(avroUtils), new AvroEventUnmarshaller(avroUtils));
    }

    @Test
    void testJsonNodeCloudEventMarshaller() throws IOException {
        testCloudEventMarshalling(getJsonNodeCloudEvent(), JsonNode.class, new AvroCloudEventMarshaller(avroUtils), new AvroCloudEventUnmarshallerFactory(avroUtils));
    }

    @Test
    void testGeneratedPojoMarshaller() throws IOException {
        testEventMarshalling(Person.newBuilder().setAge(0).setName("Pepe").build(), new AvroEventMarshaller(avroUtils), new AvroEventUnmarshaller(avroUtils));
    }
}

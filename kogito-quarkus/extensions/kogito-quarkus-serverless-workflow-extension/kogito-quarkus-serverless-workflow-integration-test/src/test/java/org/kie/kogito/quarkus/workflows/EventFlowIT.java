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
package org.kie.kogito.quarkus.workflows;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.impl.ByteArrayCloudEventMarshaller;
import org.kie.kogito.workflows.services.JavaSerializationMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.kie.kogito.quarkus.workflows.AssuredTestUtils.buildCloudEvent;
import static org.kie.kogito.quarkus.workflows.AssuredTestUtils.startProcess;
import static org.kie.kogito.quarkus.workflows.AssuredTestUtils.waitForFinish;

@QuarkusIntegrationTest
class EventFlowIT {

    private static Map<String, CloudEventMarshaller<byte[]>> marshallers;

    private static CloudEventMarshaller<byte[]> defaultMarshaller;

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        ObjectMapper mapper = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
        marshallers = Map.of("quiet", new JavaSerializationMarshaller());
        defaultMarshaller = new ByteArrayCloudEventMarshaller(mapper);
    }

    @Test
    void testStartingEventWithToStateFilter() throws IOException {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(defaultMarshaller.marshall(CloudEventBuilder.v1()
                        .withId(UUID.randomUUID().toString())
                        .withSource(URI.create("customer-arrival-event-source"))
                        .withType("customer-arrival-type")
                        .withTime(OffsetDateTime.now())
                        .withData(defaultMarshaller.cloudEventDataFactory().apply(Collections.singletonMap("customer", Map.of("name", "pepe")))).build()))
                .post("/eventWithToStateFilter")
                .then()
                .statusCode(202);

    }

    @Test
    void testNotStartingEvent() throws IOException {
        doIt("nonStartEvent", Optional.of("manolo"), "move");
    }

    @Test
    void testNotStartingMultipleEvent() throws IOException {
        doIt("nonStartMultipleEvent", "quiet", "never");
    }

    @Test
    void testNotStartingMultipleEventTimeout() throws IOException {
        doIt("nonStartMultipleEventTimeout", "eventTimeout1", "eventTimeout2");
    }

    @Test
    void testNotStartingMultipleEventTimeoutExclusive() throws IOException {
        doIt("nonStartMultipleEventTimeoutExclusive");
    }

    @Test
    void testNotStartingMultipleEventExclusive1() throws IOException {
        doIt("nonStartMultipleEventExclusive", "event1Exclusive");
    }

    @Test
    void testNotStartingEventWorkflowTimeout() {
        final String flowName = "nonStartMultipleEventWorkflowTimeout";
        String id = startProcess(flowName);
        waitForFinish(flowName, id, Duration.ofSeconds(5));
    }

    @Test
    void testNotStartingMultipleEventExclusive2() throws IOException {
        doIt("nonStartMultipleEventExclusive", "event2Exclusive");
    }

    @Test
    void testNotStartingMultipleEventExclusive3() throws IOException {
        doIt("nonStartMultipleEventExclusive", "event3Exclusive");
    }

    @Test
    void testNotStartingMultipleEventRainy() throws IOException {
        final String flowName = "nonStartMultipleEvent";
        final String id = startProcess(flowName);
        sendEvents(id, "quiet");
        assertThrows(ConditionTimeoutException.class, () -> waitForFinish(flowName, id, Duration.ofSeconds(5)));
        sendEvents(id, "never");
        waitForFinish(flowName, id, Duration.ofSeconds(5));
    }

    @Test
    void testWrongProcessIdNotProcessedRainy() throws IOException {
        final String flowName = "nonStartEvent";
        final String id = startProcess(flowName);
        sendEvents(UUID.randomUUID().toString(), "move");
        assertThrows(ConditionTimeoutException.class, () -> waitForFinish(flowName, id, Duration.ofSeconds(5)));
    }

    private void sendEvents(String id, String... eventTypes) throws IOException {
        sendEvents(id, Optional.empty(), eventTypes);
    }

    private void sendEvents(String id, Optional<String> businessKey, String... eventTypes) throws IOException {
        for (String eventType : eventTypes) {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(generateCloudEvent(id, businessKey, eventType))
                    .post("/" + eventType)
                    .then()
                    .statusCode(202);
        }
    }

    private void doIt(String flowName, Optional<String> businessKey, String... eventTypes) throws IOException {
        String id = startProcess(flowName, businessKey);
        sendEvents(id, businessKey, eventTypes);
        waitForFinish(flowName, id, Duration.ofSeconds(15));
    }

    private void doIt(String flowName, String... eventTypes) throws IOException {
        String id = startProcess(flowName);
        sendEvents(id, eventTypes);
        waitForFinish(flowName, id, Duration.ofSeconds(15));
    }

    private byte[] generateCloudEvent(String id, Optional<String> businessKey, String type) throws IOException {
        CloudEventMarshaller<byte[]> marshaller = marshallers.getOrDefault(type, defaultMarshaller);
        return marshaller.marshall(buildCloudEvent(id, businessKey, type, marshaller));
    }
}

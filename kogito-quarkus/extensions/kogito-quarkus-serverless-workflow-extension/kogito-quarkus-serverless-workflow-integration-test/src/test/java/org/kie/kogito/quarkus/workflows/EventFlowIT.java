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
package org.kie.kogito.quarkus.workflows;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.impl.ByteArrayCloudEventMarshaller;
import org.kie.kogito.workflows.services.JavaSerializationMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testNotStartingEvent() throws IOException {
        doIt("nonStartEvent", "move");
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
    void testNotStartingMultipleEventExclusive() throws IOException {
        doIt("nonStartMultipleEventExclusive", "event1Exclusive");
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

    private String startProcess(String flowName) {
        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", Collections.emptyMap()))
                .post("/" + flowName)
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/" + flowName + "/{id}", id)
                .then()
                .statusCode(200);
        return id;

    }

    private void sendEvents(String id, String... eventTypes) throws IOException {

        for (String eventType : eventTypes) {
            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .body(generateCloudEvent(id, eventType))
                    .post("/" + eventType)
                    .then()
                    .statusCode(202);
        }
    }

    private void waitForFinish(String flowName, String id, Duration duration) {
        await("dead").atMost(duration)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get("/" + flowName + "/{id}", id)
                        .then()
                        .statusCode(404));
    }

    private void doIt(String flowName, String... eventTypes) throws IOException {
        String id = startProcess(flowName);
        sendEvents(id, eventTypes);
        waitForFinish(flowName, id, Duration.ofSeconds(15));
    }

    private byte[] generateCloudEvent(String id, String type) throws IOException {
        CloudEventMarshaller<byte[]> marshaller = marshallers.getOrDefault(type, defaultMarshaller);
        return marshaller.marshall(buildCloudEvent(id, type, marshaller));
    }

    private CloudEvent buildCloudEvent(String id, String type, CloudEventMarshaller<byte[]> marshaller) {
        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType(type)
                .withTime(OffsetDateTime.now())
                .withExtension("kogitoprocrefid", id)
                .withData(marshaller.cloudEventDataFactory().apply(Collections.singletonMap(type, "This has been injected by the event")))
                .build();
    }

}

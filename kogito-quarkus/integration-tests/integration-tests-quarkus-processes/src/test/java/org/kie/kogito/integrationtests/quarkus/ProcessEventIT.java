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
package org.kie.kogito.integrationtests.quarkus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.acme.travels.Traveller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessDataEvent;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusIntegrationTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
class ProcessEventIT {

    public static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";
    public static final String KOGITO_USERTASKINSTANCES_EVENTS = "kogito-usertaskinstances-events";
    public static final String KOGITO_VARIABLE_EVENTS = "kogito-variables-events";

    private static Logger LOGGER = LoggerFactory.getLogger(ProcessEventIT.class);

    public KafkaTestClient kafkaClient;
    private static ObjectMapper mapper;

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    private String kafkaBootstrapServers;

    @BeforeEach
    public void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @BeforeAll
    static void init() {
        mapper = new ObjectMapper()
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .registerModule(new JavaTimeModule());
    }

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testSaveTask() throws Exception {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish");
        final CountDownLatch countDownLatch = new CountDownLatch(6);
        final CompletableFuture<Void> future = new CompletableFuture<>();

        String username = "buddy";
        String password = "buddy";

        kafkaClient.consume(Set.of(KOGITO_PROCESSINSTANCES_EVENTS, KOGITO_USERTASKINSTANCES_EVENTS, KOGITO_VARIABLE_EVENTS), s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                ProcessDataEvent event = mapper.readValue(s, ProcessDataEvent.class);
                LinkedHashMap data = (LinkedHashMap) event.getData();
                if ("handleApprovals".equals(data.get("processId"))) {
                    switch (event.getType()) {
                        case "ProcessInstanceEvent":
                            assertEquals("ProcessInstanceEvent", event.getType());
                            assertEquals("/handleApprovals", event.getSource().toString());
                            assertEquals("handleApprovals", data.get("processId"));
                            assertEquals("1.0", event.getKogitoProcessInstanceVersion());
                            assertEquals("BPMN", data.get("processType"));
                            assertEquals("BPMN", event.getKogitoProcessType());
                            assertEquals(username, event.getKogitoIdentity());
                            break;
                        case "UserTaskInstanceEvent":
                            assertEquals("UserTaskInstanceEvent", event.getType());
                            assertEquals("/handleApprovals", event.getSource().toString());
                            assertEquals("handleApprovals", data.get("processId"));
                            assertEquals("1.0", event.getKogitoProcessInstanceVersion());
                            assertEquals(username, event.getKogitoIdentity());
                            break;
                        case "VariableInstanceEvent":
                            assertEquals("VariableInstanceEvent", event.getType());
                            assertEquals("/handleApprovals", event.getSource().toString());
                            assertEquals("handleApprovals", data.get("processId"));
                            assertEquals("1.0", event.getKogitoProcessInstanceVersion());
                            assertEquals(username, event.getKogitoIdentity());
                            break;
                    }
                }
                countDownLatch.countDown();
                if (countDownLatch.getCount() == 0) {
                    future.complete(null);
                }
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });

        String processId = given()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .when()
                .body(Collections.singletonMap("traveller", traveller))
                .post("/handleApprovals")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        String taskId = given()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .when()
                .get("/handleApprovals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        Map<String, Object> model = Collections.singletonMap("approved", true);

        assertEquals(model, given()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(model)
                .put("/handleApprovals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class));

        assertEquals(true, given()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .get("/handleApprovals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .path("results.approved"));

        given()
                .contentType(ContentType.JSON)
                .auth().basic(username, password)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .when()
                .get("/handleApprovals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[1].id");

        future.get(10, TimeUnit.SECONDS);
    }
}

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.acme.travels.Traveller;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.fail;

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
    void testSaveTask() throws InterruptedException {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish");
        final int count = 7;
        final CountDownLatch countDownLatch = new CountDownLatch(count);

        kafkaClient.consume(Set.of(KOGITO_PROCESSINSTANCES_EVENTS, KOGITO_USERTASKINSTANCES_EVENTS, KOGITO_VARIABLE_EVENTS), s -> {
            LOGGER.info("Received from kafka: {}", s);
            try {
                ProcessDataEvent event = mapper.readValue(s, ProcessDataEvent.class);
                LinkedHashMap data = (LinkedHashMap) event.getData();
                if (data.get("processId") == "handleApprovals") {
                    switch (event.getType()) {
                        case "ProcessInstanceEvent":
                            Assertions.assertEquals("ProcessInstanceEvent", event.getType());
                            Assertions.assertEquals("/handleApprovals", event.getSource().toString());
                            Assertions.assertEquals("handleApprovals", data.get("processId"));
                            break;
                        case "UserTaskInstanceEvent":
                            Assertions.assertEquals("UserTaskInstanceEvent", event.getType());
                            Assertions.assertEquals("/handleApprovals", event.getSource().toString());
                            Assertions.assertEquals("handleApprovals", data.get("processId"));
                            break;
                        case "VariableInstanceEvent":
                            Assertions.assertEquals("VariableInstanceEvent", event.getType());
                            Assertions.assertEquals("/handleApprovals", event.getSource().toString());
                            Assertions.assertEquals("handleApprovals", data.get("processId"));
                            break;
                    }
                }
                countDownLatch.countDown();

            } catch (Exception e) {
                LOGGER.error("Error parsing {}", s, e);
                fail(e);
            }
        });

        String processId = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("traveller", traveller))
                .post("/handleApprovals")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        String taskId = given()
                .contentType(ContentType.JSON)
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

        Assertions.assertEquals(model, given().contentType(ContentType.JSON)
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

        Assertions.assertEquals(true, given().contentType(ContentType.JSON)
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

        String humanTaskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .when()
                .get("/handleApprovals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[1].id");

        countDownLatch.await(10, TimeUnit.SECONDS);
        Assertions.assertEquals(0, countDownLatch.getCount());
    }
}

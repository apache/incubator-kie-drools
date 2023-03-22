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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;
import org.kie.kogito.testcontainers.quarkus.KafkaQuarkusTestResource;
import org.kie.kogito.workflows.services.BasicDataPerson;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.kie.kogito.quarkus.workflows.WorkflowTestUtils.waitForKogitoProcessInstanceEvent;

@QuarkusIntegrationTest
class PojoServiceIT {

    @QuarkusTestProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
    String kafkaBootstrapServers;

    KafkaTestClient kafkaClient;

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setup() {
        kafkaClient = new KafkaTestClient(kafkaBootstrapServers);
    }

    @AfterEach
    public void cleanup() {
        if (kafkaClient != null) {
            kafkaClient.shutdown();
        }
    }

    @Test
    void testPojo() throws Exception {
        doIt("pojoService");
    }

    @Test
    void testFilterPojo() throws Exception {
        doIt("pojoServiceFilter");
    }

    @Test
    void testSquareService() {
        Map<String, Object> body = new HashMap<>();
        body.put("first", 3);
        body.put("second", 6);
        body.put("third", 9);
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(body)
                .post("/squareService")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("workflowdata.response", is(Arrays.asList(9, 36, 81)));
    }

    @Test
    void testTypesPojo() throws Exception {
        Map<String, Object> body = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date creationDate = formatter.parse("07/07/2022");
        String cardId = "cardID";
        int count = 5;
        boolean enabled = true;
        Date birthDate = formatter.parse("08/12/1999");
        BasicDataPerson basicDataPerson = new BasicDataPerson(cardId, 100.01, count, enabled, birthDate);

        body.put("name", "javierito");
        body.put("age", 666);
        body.put("income", 666.3);
        body.put("creationDate", creationDate);
        body.put("basicDataPerson", basicDataPerson);
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", body))
                .post("/pojoServiceTypes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("workflowdata.name", is("javieritoPerson"))
                .body("workflowdata.age", is(1))
                .body("workflowdata.income", is(20000.5f))
                .body("workflowdata.creationDate", is(creationDate.getTime()))
                .body("workflowdata.cardId", is(cardId))
                .body("workflowdata.discount", is(100.01f))
                .body("workflowdata.count", is(count))
                .body("workflowdata.enabled", is(enabled))
                .body("workflowdata.birthDate", is(birthDate.getTime()));

        JsonPath processInstanceEventContent = waitForKogitoProcessInstanceEvent(kafkaClient, true);
        Map workflowDataMap = processInstanceEventContent.getMap("data.variables.workflowdata");
        assertThat(workflowDataMap)
                .hasSize(9)
                .containsEntry("name", "javieritoPerson")
                .containsEntry("creationDate", creationDate.getTime())
                .containsEntry("age", 1)
                .containsEntry("income", 20000.5f)
                .containsEntry("cardId", "cardID")
                .containsEntry("discount", 100.01f)
                .containsEntry("count", count)
                .containsEntry("enabled", enabled)
                .containsEntry("birthDate", birthDate.getTime());
    }

    private void doIt(String flowName) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "javierito");
        body.put("age", 666);
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", body))
                .post("/" + flowName)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("workflowdata.name", is("javieritoPerson"))
                .body("workflowdata.age", nullValue());
        JsonPath processInstanceEventContent = waitForKogitoProcessInstanceEvent(kafkaClient, true);
        Map workflowDataMap = processInstanceEventContent.getMap("data.variables.workflowdata");
        assertThat(workflowDataMap).hasSize(1);
        assertThat(workflowDataMap).containsEntry("name", "javieritoPerson");
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasEntry;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.Conditional.class)
class ManagementAddOnTest {

    private static final String HELLO1_NODE = "_3CDC6E61-DCC5-4831-8BBB-417CFF517CB0";
    private static final String GREETINGS = "greetings";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testGetProcessNodesWithInvalidProcessId() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/management/processes/{processId}/nodes", "aprocess")
                .then()
                .statusCode(404)
                .body(equalTo("Process with id aprocess not found"));
    }

    @Test
    void testAbortProcessInstance() {
        String pid = givenGreetingsProcess();

        given().contentType(ContentType.JSON)
               .when()
               .delete("/management/processes/{processId}/instances/{processInstanceId}", GREETINGS, pid)
               .then()
               .statusCode(200);
    }

    @Test
    void testGetNodeInstances() {
        String pid = givenGreetingsProcess();

        given().contentType(ContentType.JSON)
                .when()
                .get("/management/processes/{processId}/instances/{processInstanceId}/nodeInstances", GREETINGS, pid)
                .then()
                .statusCode(200)
                .body("$.size", is(2))
                .body("$", hasItems(hasEntry("name", "Hello1"), hasEntry("name", "Hello2")))
                .body("[0].state", is(0))
                .body("[1].state", is(0));
    }

    @Test
    void testGetProcessNodes() {
        given().contentType(ContentType.JSON)
            .when()
                .get("/management/processes/{processId}/nodes", GREETINGS)
            .then()
                .statusCode(200)
                .body("$.size", is(10))
                .body("[0].id", is(1))
                .body("[0].name", is("End"))
                .body("[0].type", is("EndNode"))
                .body("[0].uniqueId", is("1"))
                .body("[0].nodeDefinitionId", not(emptyOrNullString()))
                .body("[9].id", is(10))
                .body("[9].name", is("BoundaryEvent"))
                .body("[9].type", is("BoundaryEventNode"))
                .body("[9].uniqueId", is("10"))
                .body("[9].nodeDefinitionId", not(emptyOrNullString()));
    }

    @Test
    void testReTriggerNode() {
        String pid = givenGreetingsProcess();

        List<String> nodeInstanceIds = whenGetNodeInstances(pid);
        assertThat(nodeInstanceIds).isNotEmpty();

        // cancel node instance
        nodeInstanceIds.forEach(nodeInstanceId -> whenCancelNodeInstance(pid, nodeInstanceId));

        // then trigger new node instance via management interface
        given().contentType(ContentType.JSON)
               .when()
               .post("/management/processes/{processId}/instances/{processInstanceId}/nodes/{node}", GREETINGS, pid, HELLO1_NODE)
               .then()
               .statusCode(200);

        // since node instance was retriggered it must have different ids
        List<String> newNodeInstanceIds = whenGetNodeInstances(pid);
        assertThat(newNodeInstanceIds).isNotEmpty();
        assertThat(newNodeInstanceIds).doesNotContainAnyElementsOf(nodeInstanceIds);
    }

    private String givenGreetingsProcess() {
        return given().contentType(ContentType.JSON)
                      .when()
                      .post("/greetings")
                      .then()
                      .statusCode(201)
                      .body("id", not(emptyOrNullString()))
                      .body("test", emptyOrNullString())
                      .extract().path("id");
    }

    private void whenCancelNodeInstance(String pid, String nodeInstanceId) {
        given().contentType(ContentType.JSON)
               .when()
               .delete("/management/processes/{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}", GREETINGS, pid, nodeInstanceId)
               .then()
               .statusCode(200);
    }

    private List<String> whenGetNodeInstances(String pid) {
        return given().contentType(ContentType.JSON)
                      .when()
                      .get("/management/processes/{processId}/instances/{processInstanceId}/nodeInstances", GREETINGS, pid)
                      .then()
                      .statusCode(200)
                      .extract().response().jsonPath().getList("nodeInstanceId");
    }
}

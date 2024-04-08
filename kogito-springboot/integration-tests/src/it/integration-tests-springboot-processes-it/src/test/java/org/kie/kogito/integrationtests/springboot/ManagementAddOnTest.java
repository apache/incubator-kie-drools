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
package org.kie.kogito.integrationtests.springboot;

import java.util.List;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasEntry;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
class ManagementAddOnTest extends BaseRestTest {

    private static final String HELLO1_NODE = "_3CDC6E61-DCC5-4831-8BBB-417CFF517CB0";
    private static final String GREETINGS = "greetings";
    
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @Test
    void testGetProcessNodesWithInvalidProcessId() {
        given().contentType(ContentType.JSON)
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
                .body("$.size()", is(2))
                .body("$", hasItems(hasEntry("name", "Hello1"),hasEntry("name", "Hello2")))
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
                .body("$.size()", is(10))
                .body("[0].id", is("_F8881669-9AE6-46D0-9633-02A42D3D06BB"))
                .body("[0].name", is("End"))
                .body("[0].type", is("EndNode"))
                .body("[0].uniqueId", is("_F8881669-9AE6-46D0-9633-02A42D3D06BB"))
                .body("[0].nodeDefinitionId", not(emptyOrNullString()))
                .body("[9].id", is("_BC5E8132-1AD8-4F2A-94DA-299C99D79CD9"))
                .body("[9].name", is("BoundaryEvent"))
                .body("[9].type", is("BoundaryEventNode"))
                .body("[9].uniqueId", is("_BC5E8132-1AD8-4F2A-94DA-299C99D79CD9"))
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
                    .header("Location", not(emptyOrNullString()))
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

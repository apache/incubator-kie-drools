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
package org.kie.kogito.index;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static java.util.Collections.singletonMap;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

public abstract class AbstractProcessDataIndexIT {

    private static Duration TIMEOUT = Duration.ofSeconds(30);

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    RequestSpecification spec;

    public abstract String getDataIndexURL();

    public boolean validateDomainData() {
        return true;
    }

    public boolean checkRuntimeConnectionsResponses() {
        return true;
    }

    public RequestSpecification dataIndexSpec() {
        if (spec == null) {
            spec = new RequestSpecBuilder().setBaseUri(getDataIndexURL()).build();
        }
        return spec;
    }

    @Test
    public void testProcessInstanceEvents() {
        String pId = given()
                .contentType(ContentType.JSON)
                .body("{\"traveller\" : {\"firstName\" : \"Darth\",\"lastName\" : \"Vader\",\"email\" : \"darth.vader@deathstar.com\",\"nationality\" : \"Tatooine\"}}")
                .when()
                .post("/approvals")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");

        String flTaskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", pId)
                .when()
                .get("/approvals/{processId}/tasks")
                .then()
                .statusCode(200)
                .body("$.size", is(1))
                .body("[0].name", is("firstLineApproval"))
                .body("[0].id", notNullValue())
                .extract()
                .path("[0].id");

        if (validateDomainData()) {
            await()
                    .atMost(TIMEOUT)
                    .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                            .body("{ \"query\" : \"{Approvals{ id, traveller { firstName, lastName }, metadata { processInstances { id, state }, userTasks { id, name, state } } } }\" }")
                            .when().post("/graphql")
                            .then().statusCode(200)
                            .body("data.Approvals.size()", is(1))
                            .body("data.Approvals[0].id", is(pId))
                            .body("data.Approvals[0].traveller.firstName", is("Darth"))
                            .body("data.Approvals[0].traveller.lastName", is("Vader"))
                            .body("data.Approvals[0].metadata.processInstances", is(notNullValue()))
                            .body("data.Approvals[0].metadata.processInstances.size()", is(1))
                            .body("data.Approvals[0].metadata.processInstances[0].id", is(pId))
                            .body("data.Approvals[0].metadata.processInstances[0].state", is("ACTIVE"))
                            .body("data.Approvals[0].metadata.userTasks", is(notNullValue()))
                            .body("data.Approvals[0].metadata.userTasks.size()", is(1))
                            .body("data.Approvals[0].metadata.userTasks[0].id", is(flTaskId))
                            .body("data.Approvals[0].metadata.userTasks[0].name", is("firstLineApproval"))
                            .body("data.Approvals[0].metadata.userTasks[0].state", is("Ready")));
        }

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ProcessInstances{ id, processId, state } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.ProcessInstances.size()", is(1))
                        .body("data.ProcessInstances[0].id", is(pId))
                        .body("data.ProcessInstances[0].processId", is("approvals"))
                        .body("data.ProcessInstances[0].state", is("ACTIVE")));

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{UserTaskInstances{ id, name, state } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(1))
                        .body("data.UserTaskInstances[0].id", is(flTaskId))
                        .body("data.UserTaskInstances[0].name", is("firstLineApproval"))
                        .body("data.UserTaskInstances[0].state", is("Ready")));

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .when()
                        .queryParam("user", "admin")
                        .queryParam("group", "managers")
                        .pathParam("processId", pId)
                        .pathParam("taskId", flTaskId)
                        .body(singletonMap("approved", true))
                        .post("/approvals/{processId}/firstLineApproval/{taskId}")
                        .then()
                        .statusCode(200)
                        .body("firstLineApproval", is(true)));

        String slTaskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "manager")
                .queryParam("group", "managers")
                .pathParam("processId", pId)
                .when()
                .get("/approvals/{processId}/tasks")
                .then()
                .statusCode(200)
                .body("$.size", is(1))
                .body("[0].name", is("secondLineApproval"))
                .body("[0].id", notNullValue())
                .extract()
                .path("[0].id");

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .when()
                        .queryParam("user", "manager")
                        .queryParam("group", "managers")
                        .pathParam("processId", pId)
                        .pathParam("taskId", slTaskId)
                        .body(singletonMap("approved", true))
                        .post("/approvals/{processId}/secondLineApproval/{taskId}")
                        .then()
                        .statusCode(200)
                        .body("secondLineApproval", is(true)));

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .pathParam("processId", pId)
                        .get("/approvals/{processId}")
                        .then()
                        .statusCode(404));

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ProcessInstances{ id, processId, state } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.ProcessInstances.size()", is(1))
                        .body("data.ProcessInstances[0].id", is(pId))
                        .body("data.ProcessInstances[0].processId", is("approvals"))
                        .body("data.ProcessInstances[0].state", is("COMPLETED")));

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{UserTaskInstances{ id, name, state } }\" }")
                        .when().post("/graphql")
                        .then().statusCode(200)
                        .body("data.UserTaskInstances.size()", is(2)));

        if (validateDomainData()) {
            await()
                    .atMost(TIMEOUT)
                    .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                            .body("{ \"query\" : \"{Approvals{ id, firstLineApproval, secondLineApproval, metadata { processInstances { id, state }, userTasks { id, name, state } } } }\" }")
                            .when().post("/graphql")
                            .then().statusCode(200)
                            .body("data.Approvals.size()", is(1))
                            .body("data.Approvals[0].id", is(pId))
                            .body("data.Approvals[0].firstLineApproval", is(true))
                            .body("data.Approvals[0].secondLineApproval", is(true))
                            .body("data.Approvals[0].metadata.processInstances", is(notNullValue()))
                            .body("data.Approvals[0].metadata.processInstances.size()", is(1))
                            .body("data.Approvals[0].metadata.processInstances[0].id", is(pId))
                            .body("data.Approvals[0].metadata.processInstances[0].state", is("COMPLETED"))
                            .body("data.Approvals[0].metadata.userTasks", is(notNullValue()))
                            .body("data.Approvals[0].metadata.userTasks.size()", is(2)));
        }

        String pId2 = createTestProcessInstance();
        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> getProcessInstanceById(pId2, "ACTIVE"));
        if (checkRuntimeConnectionsResponses()) {
            await()
                    .atMost(TIMEOUT)
                    .untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                            .body("{ \"query\" : \"{ ProcessInstances (where: { id: {equal: \\\"" + pId2 + "\\\"}}) { nodeDefinitions {id} nodes {id}} }\"}")
                            .when().post("/graphql")
                            .then()
                            .statusCode(200)
                            .body("data.ProcessInstances[0].nodeDefinitions", notNullValue())
                            .body("data.ProcessInstances[0].nodeDefinitions.size()", is(4))
                            .body("data.ProcessInstances[0].nodes.size()", is(2)));

            await()
                    .atMost(TIMEOUT).untilAsserted(() -> given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                            .body("{ \"query\" : \"mutation {ProcessInstanceAbort( id: \\\"" + pId2 + "\\\")}\"}")
                            .when().post("/graphql")
                            .then()
                            .statusCode(200)
                            .body("errors", nullValue()));
            await()
                    .atMost(TIMEOUT)
                    .untilAsserted(() -> getProcessInstanceById(pId2, "ABORTED"));

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ ProcessInstanceRetry( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200)
                    .body("data.ProcessInstanceRetry", nullValue());

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ ProcessInstanceSkip( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200)
                    .body("errors[0].message", containsString("FAILED: SKIP"));

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ UndefinedMutation( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200)
                    .body("errors[0].message", containsString("Field 'UndefinedMutation' in type 'Mutation' is undefined"));
        } else {

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation {ProcessInstanceAbort( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200);

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ ProcessInstanceRetry( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200);

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ ProcessInstanceSkip( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200);

            given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                    .body("{ \"query\" : \"mutation{ UndefinedMutation( id: \\\"" + pId2 + "\\\")}\"}")
                    .when().post("/graphql")
                    .then()
                    .statusCode(200)
                    .body("errors[0].message", containsString("Field 'UndefinedMutation' in type 'Mutation' is undefined"));
        }
    }

    protected String createTestProcessInstance() {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"traveller\" : {\"firstName\" : \"Darth\",\"lastName\" : \"Vader\",\"email\" : \"darth.vader@deathstar.com\",\"nationality\" : \"Tatooine\"}}")
                .when()
                .post("/approvals")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    protected ValidatableResponse getProcessInstanceById(String processInstanceId, String state) {
        return given().spec(dataIndexSpec()).contentType(ContentType.JSON)
                .body("{ \"query\" : \"{ProcessInstances(where: {  id: {  equal : \\\"" + processInstanceId + "\\\"}}){ id, processId, state } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(1))
                .body("data.ProcessInstances[0].id", is(processInstanceId))
                .body("data.ProcessInstances[0].processId", is("approvals"))
                .body("data.ProcessInstances[0].state", is(state));
    }
}

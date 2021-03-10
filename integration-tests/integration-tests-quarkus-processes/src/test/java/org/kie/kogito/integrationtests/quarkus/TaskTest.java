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
package org.kie.kogito.integrationtests.quarkus;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.task.management.service.TaskInfo;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.Conditional.class)
class TaskTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testJsonSchema() {
        // Quarkus returns URI with "quarkus://" scheme when running via CLI and this is not compatible with
        // matchesJsonSchemaInClasspath, while matchesJsonSchema directly accepts InputStream
        InputStream jsonSchema = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "META-INF/jsonSchema/approvals_firstLineApproval.json");
        assertThat(jsonSchema).isNotNull();

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/approvals/firstLineApproval/schema")
                .then()
                .statusCode(200)
                .body(matchesJsonSchema(jsonSchema));
    }

    @Test
    void testUpdateTask() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish");

        String processId = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("traveller", traveller))
                .post("/approvals")
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
                .get("/approvals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(Collections.singletonMap("approved", true))
                .patch("/approvals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .body("approved", is(true));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(Collections.singletonMap("approved", false))
                .patch("/approvals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .body("approved", is(false));
    }

    @Test
    void testUpdateExcludedUsers() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish");

        String processId = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("traveller", traveller))
                .post("/approvals")
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
                .get("/approvals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        Collection<String> excludedUsers = Arrays.asList("Javierito", "Manuel");
        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(Collections.singletonMap("excludedUsers", excludedUsers))
                .patch("/management/processes/approvals/instances/{processId}/tasks/{taskId}")
                .then()
                .statusCode(200)
                .body("excludedUsers", is(excludedUsers));

        assertEquals(excludedUsers, given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .get("/management/processes/approvals/instances/{processId}/tasks/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .path("excludedUsers"));
    }

    private static class ClientTaskInfo {

        public String description;
        public String priority;
        public Set<String> potentialUsers;
        public Set<String> potentialGroups;
        public Set<String> excludedUsers;
        public Set<String> adminUsers;
        public Set<String> adminGroups;
        public TravellerInputModel inputParams;
    }

    private static class TravellerInputModel {

        public Traveller traveller;
    }

    @Test
    void testUpdateTaskInfo() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish");

        String processId = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("traveller", traveller))
                .post("/approvals")
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
                .get("/approvals/{processId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        traveller.setEmail("javierito@gmail.com");
        TaskInfo upTaskInfo = new TaskInfo("firstAproval", "high", Collections.singleton("admin"),
                Collections.singleton("managers"), Collections.singleton("Javierito"), Collections.emptySet(),
                Collections.emptySet(), Collections.singletonMap("traveller", traveller));
        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(upTaskInfo)
                .put("/management/processes/approvals/instances/{processId}/tasks/{taskId}")
                .then()
                .statusCode(200);

        ClientTaskInfo downTaskInfo = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .get("/management/processes/approvals/instances/{processId}/tasks/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .as(ClientTaskInfo.class);
        assertEquals(upTaskInfo.getAdminGroups(), downTaskInfo.adminGroups);
        assertEquals(upTaskInfo.getAdminUsers(), downTaskInfo.adminUsers);
        assertEquals(upTaskInfo.getPotentialGroups(), downTaskInfo.potentialGroups);
        assertEquals(upTaskInfo.getPotentialUsers(), downTaskInfo.potentialUsers);
        assertEquals(upTaskInfo.getExcludedUsers(), downTaskInfo.excludedUsers);
        assertEquals(upTaskInfo.getDescription(), downTaskInfo.description);
        assertEquals(upTaskInfo.getPriority(), downTaskInfo.priority);
        assertEquals(traveller, downTaskInfo.inputParams.traveller);
    }

}

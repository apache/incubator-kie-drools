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
package org.kie.kogito.integrationtests.quarkus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.workitem.AttachmentInfo;
import org.kie.kogito.task.management.service.TaskInfo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import jakarta.ws.rs.core.UriBuilder;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusIntegrationTest
class TaskIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testJsonSchema() throws IOException {
        // Quarkus returns URI with "quarkus://" scheme when running via CLI and this is not compatible with
        // matchesJsonSchemaInClasspath, while matchesJsonSchema directly accepts InputStream
        try (InputStream jsonSchema = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "testJsonSchema/test_approvals_firstLineApproval.json")) {
            assertThat(jsonSchema).isNotNull();

            given()
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/approvals/firstLineApproval/schema")
                    .then()
                    .statusCode(200)
                    .body(matchesJsonSchema(jsonSchema));
        }

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

        try (InputStream jsonSchema = Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "testJsonSchema/test_approvals_firstLineApproval_instance.json")) {
            assertThat(jsonSchema).isNotNull();

            given()
                    .contentType(ContentType.JSON)
                    .queryParam("user", "admin")
                    .queryParam("group", "managers")
                    .when()
                    .get("/approvals/{processId}/firstLineApproval/{taskId}/schema", processId, taskId)
                    .then()
                    .statusCode(200)
                    .body(matchesJsonSchema(jsonSchema));
        }
    }

    @Test
    void testSaveTask() {
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

        Map<String, Object> model = Collections.singletonMap("approved", true);
        assertEquals(model, given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(model)
                .put("/approvals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class));

        assertEquals(true, given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .get("/approvals/{processId}/firstLineApproval/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .path("results.approved"));
    }

    @Test
    void testCommentAndAttachment() {
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

        final String commentId = given().contentType(ContentType.TEXT)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body("We need to act")
                .post("/approvals/{processId}/firstLineApproval/{taskId}/comments")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        final String commentText = "We have done everything we can";
        given().contentType(ContentType.TEXT)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("commentId", commentId)
                .body(commentText)
                .put("/approvals/{processId}/firstLineApproval/{taskId}/comments/{commentId}")
                .then()
                .statusCode(200);

        assertEquals(commentText, given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("commentId", commentId)
                .get("/approvals/{processId}/firstLineApproval/{taskId}/comments/{commentId}")
                .then()
                .statusCode(200).extract().path("content"));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("commentId", commentId)
                .delete("/approvals/{processId}/firstLineApproval/{taskId}/comments/{commentId}")
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("commentId", commentId)
                .get("/approvals/{processId}/firstLineApproval/{taskId}/comments/{commentId}")
                .then()
                .statusCode(404);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("commentId", commentId)
                .delete("/approvals/{processId}/firstLineApproval/{taskId}/comments/{commentId}")
                .then()
                .statusCode(404);

        final String attachmentId = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(new AttachmentInfo(UriBuilder.fromPath("pepito.txt").scheme("file").build(), "pepito.txt"))
                .post("/approvals/{processId}/firstLineApproval/{taskId}/attachments")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("attachmentId", attachmentId)
                .body(new AttachmentInfo(UriBuilder.fromPath("/home/fulanito.txt").scheme("file").build()))
                .put("/approvals/{processId}/firstLineApproval/{taskId}/attachments/{attachmentId}")
                .then()
                .statusCode(200);

        given().contentType(
                ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("attachmentId", attachmentId)
                .get("/approvals/{processId}/firstLineApproval/{taskId}/attachments/{attachmentId}")
                .then()
                .statusCode(200).body("name", equalTo("fulanito.txt")).body("content", equalTo(
                        "file:/home/fulanito.txt"));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("attachmentId", attachmentId)
                .delete("/approvals/{processId}/firstLineApproval/{taskId}/attachments/{attachmentId}")
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("attachmentId", attachmentId)
                .get("/approvals/{processId}/firstLineApproval/{taskId}/attachments/{attachmentId}")
                .then()
                .statusCode(404);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .pathParam("attachmentId", attachmentId)
                .delete("/approvals/{processId}/firstLineApproval/{taskId}/attachments/{attachmentId}")
                .then()
                .statusCode(404);
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
        public String TaskName;
        public String NodeName;
        public Boolean Skippable;
        public String ActorId;
        public String GroupId;
        public Traveller traveller;
    }
}

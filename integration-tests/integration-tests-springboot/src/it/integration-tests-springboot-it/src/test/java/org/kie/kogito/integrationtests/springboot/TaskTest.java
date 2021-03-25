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
package org.kie.kogito.integrationtests.springboot;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.acme.travels.Traveller;
import org.acme.travels.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.process.workitem.AttachmentInfo;
import org.kie.kogito.task.management.service.TaskInfo;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.Conditional.class)
public class TaskTest extends BaseRestTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testJsonSchema() {
        given()
                .contentType(ContentType.JSON)
            .when()
                .get("/approvals/firstLineApproval/schema")
            .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("approvals_firstLineApproval.json"));
    }

    @Test
    void testCommentAndAttachment() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish", null);

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

        final String attachmentId = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .body(new AttachmentInfo(URI.create("pepito.txt"), "pepito.txt"))
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
                .body(new AttachmentInfo(URI.create("file:/home/fulanito.txt")))
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
    void testSaveTask() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish", new Address("Alfredo Di Stefano", "Madrid", "28033", "Spain"));

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
    }

    @Test
    void testUpdateExcludedUsers() {
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish", new Address("Alfredo Di Stefano", "Madrid", "28033", "Spain"));

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
        Traveller traveller = new Traveller("pepe", "rubiales", "pepe.rubiales@gmail.com", "Spanish", new Address("Alfredo Di Stefano", "Madrid", "28033", "Spain"));

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

        TaskInfo upTaskInfo = new TaskInfo("firstAproval", "high", Collections.singleton("admin"),
                Collections.singleton("managers"), Collections.singleton("Javierito"), Collections.emptySet(),
                Collections.emptySet(), Collections.emptyMap());
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

        TaskInfo downTaskInfo = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .pathParam("taskId", taskId)
                .get("/management/processes/approvals/instances/{processId}/tasks/{taskId}")
                .then()
                .statusCode(200)
                .extract()
                .as(TaskInfo.class);
        assertEquals(upTaskInfo.getAdminGroups(), downTaskInfo.getAdminGroups());
        assertEquals(upTaskInfo.getAdminUsers(), downTaskInfo.getAdminUsers());
        assertEquals(upTaskInfo.getPotentialGroups(), downTaskInfo.getPotentialGroups());
        assertEquals(upTaskInfo.getPotentialUsers(), downTaskInfo.getPotentialUsers());
        assertEquals(upTaskInfo.getExcludedUsers(), downTaskInfo.getExcludedUsers());
        assertEquals(upTaskInfo.getDescription(), downTaskInfo.getDescription());
        assertEquals(upTaskInfo.getPriority(), downTaskInfo.getPriority());
    }
}

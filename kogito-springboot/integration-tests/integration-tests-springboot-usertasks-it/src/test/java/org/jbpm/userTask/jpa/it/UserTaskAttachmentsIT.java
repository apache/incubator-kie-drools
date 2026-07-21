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

package org.jbpm.userTask.jpa.it;

import java.net.URI;

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.it.KogitoSpringbootApplication;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.kie.kogito.usertask.model.AttachmentInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
public class UserTaskAttachmentsIT extends BaseUserTaskIT {
    public static final String USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/attachments";
    public static final String USER_TASKS_INSTANCE_ATTACHMENT = USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT + "/{attachmentId}";

    @Test
    public void testUserTaskAttachments() {
        Traveller traveller = new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US"));

        final String pid = startProcessInstance(traveller);

        String taskId = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

        AttachmentInfo attachment1 = new AttachmentInfo(URI.create("http://localhost:8080/attachment_1.txt"), "Attachment 1");

        String attachment1Id = addAndVerifyAttachment(taskId, attachment1);

        AttachmentInfo attachment2 = new AttachmentInfo(URI.create("http://localhost:8080/attachment_2.txt"), "Attachment 2");

        String attachment2Id = addAndVerifyAttachment(taskId, attachment2);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(2));

        attachment1 = new AttachmentInfo(URI.create("http://localhost:8080/new_attachment_1.txt"), "NEW Attachment 1");

        updateAndVerifyAttachment(taskId, attachment1Id, attachment1);

        attachment2 = new AttachmentInfo(URI.create("http://localhost:8080/new_attachment_2.txt"), "NEW Attachment 2");

        updateAndVerifyAttachment(taskId, attachment2Id, attachment2);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].id", not(emptyOrNullString()))
                .body("[0].content", equalTo(attachment1.getUri().toString()))
                .body("[0].name", equalTo(attachment1.getName()))
                .body("[0].updatedBy", not(emptyOrNullString()))
                .body("[0].updatedAt", not(emptyOrNullString()))
                .body("[1].id", not(emptyOrNullString()))
                .body("[1].content", equalTo(attachment2.getUri().toString()))
                .body("[1].name", equalTo(attachment2.getName()))
                .body("[1].updatedBy", not(emptyOrNullString()))
                .body("[1].updatedAt", not(emptyOrNullString()));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .delete(USER_TASKS_INSTANCE_ATTACHMENT, taskId, attachment1Id)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(1));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .delete(USER_TASKS_INSTANCE_ATTACHMENT, taskId, attachment2Id)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

        abortProcessInstance(pid);
    }

    private String addAndVerifyAttachment(String taskId, AttachmentInfo attachment) {
        String id = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .body(attachment)
                .post(USER_TASKS_INSTANCE_ATTACHMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(attachment.getUri().toString()))
                .body("name", equalTo(attachment.getName()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()))
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENT, taskId, id)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(attachment.getUri().toString()))
                .body("name", equalTo(attachment.getName()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()));

        return id;
    }

    private void updateAndVerifyAttachment(String taskId, String attachmentId, AttachmentInfo attachment) {
        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .body(attachment)
                .put(USER_TASKS_INSTANCE_ATTACHMENT, taskId, attachmentId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(attachment.getUri().toString()))
                .body("name", equalTo(attachment.getName()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()))
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ATTACHMENT, taskId, attachmentId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(attachment.getUri().toString()))
                .body("name", equalTo(attachment.getName()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()));
    }
}

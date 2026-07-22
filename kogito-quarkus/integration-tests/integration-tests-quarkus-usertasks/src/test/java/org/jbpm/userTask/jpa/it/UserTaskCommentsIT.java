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

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;
import org.kie.kogito.usertask.model.CommentInfo;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.emptyOrNullString;

@QuarkusIntegrationTest
@QuarkusTestResource(value = PostgreSqlQuarkusTestResource.class, restrictToAnnotatedClass = true)
public class UserTaskCommentsIT extends BaseUserTaskIT {
    public static final String USER_TASKS_INSTANCE_COMMENTS_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/comments";
    public static final String USER_TASKS_INSTANCE_COMMENT = USER_TASKS_INSTANCE_COMMENTS_ENDPOINT + "/{commentId}";

    @Test
    public void testUserTaskComments() {
        Traveller traveller = new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US"));

        startProcessInstance(traveller);

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
                .get(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

        CommentInfo comment1 = new CommentInfo("This is my second comment.");

        String comment1Id = addAndVerifyComment(taskId, comment1);

        CommentInfo comment2 = new CommentInfo("This is my second comment.");

        String comment2Id = addAndVerifyComment(taskId, comment2);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .get(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(2));

        comment1 = new CommentInfo("This is the first comment modified");

        updateAndVerifyComment(taskId, comment1Id, comment1);

        comment2 = new CommentInfo("This is the second comment modified");

        updateAndVerifyComment(taskId, comment2Id, comment2);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(2))
                .body("[0].id", not(emptyOrNullString()))
                .body("[0].content", equalTo(comment1.getComment()))
                .body("[0].updatedBy", not(emptyOrNullString()))
                .body("[0].updatedAt", not(emptyOrNullString()))
                .body("[1].id", not(emptyOrNullString()))
                .body("[1].content", equalTo(comment2.getComment()))
                .body("[1].updatedBy", not(emptyOrNullString()))
                .body("[1].updatedAt", not(emptyOrNullString()));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .delete(USER_TASKS_INSTANCE_COMMENT, taskId, comment1Id)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(1));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .delete(USER_TASKS_INSTANCE_COMMENT, taskId, comment2Id)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }

    private String addAndVerifyComment(String taskId, CommentInfo comment) {
        String id = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .body(comment)
                .post(USER_TASKS_INSTANCE_COMMENTS_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(comment.getComment()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()))
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_COMMENT, taskId, id)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(comment.getComment()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()));

        return id;
    }

    private void updateAndVerifyComment(String taskId, String commentId, CommentInfo comment) {
        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .body(comment)
                .put(USER_TASKS_INSTANCE_COMMENT, taskId, commentId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(comment.getComment()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()))
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_COMMENT, taskId, commentId)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("content", equalTo(comment.getComment()))
                .body("updatedBy", not(emptyOrNullString()))
                .body("updatedAt", not(emptyOrNullString()));
    }
}

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

package org.kie.kogito.it;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.usertask.model.TransitionInfo;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;

public abstract class TransactionalErrorHandlingTest {
    private static final String USER_TASK_BASE_PATH = "/usertasks/instance";
    private static final String MANAGEMENT_INSTANCE_PATH = "/management/processes/{processId}/instances/{processInstanceId}";

    private static final String ERRORS_PROCESS = "transactional_errors";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @LocalServerPort
    private int httpPort;

    @BeforeEach
    void setPort() {
        RestAssured.port = httpPort;
    }

    @Test
    void testTransactionalErrorsProcess() {
        String pId = given().contentType(ContentType.JSON)
                .when()
                .body(Map.of("fail", true))
                .post("/{processId}", ERRORS_PROCESS)
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        String userTaskId = given()
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "jdoe")
                .queryParam("group", "user")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .path("[0].id");

        given()
                .contentType(ContentType.JSON)
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "jdoe")
                .queryParam("group", "user")
                .body(new TransitionInfo("complete", Map.of("fail", true)))
                .when()
                .post("/{userTaskId}/transition", userTaskId)
                .then()
                .statusCode(500);

        given()
                .contentType(ContentType.JSON)
                .basePath(MANAGEMENT_INSTANCE_PATH)
                .pathParam("processId", ERRORS_PROCESS)
                .pathParam("processInstanceId", pId)
                .when()
                .get("error")
                .then()
                .statusCode(200)
                .body("failedNodeId", equalTo("_E691E0BE-B728-4369-867D-AACC71812F87"))
                .body("id", equalTo(pId))
                .body("message", containsString("java.lang.RuntimeException - This is a controlled error... fail -> true"));

        given()
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "jdoe")
                .queryParam("group", "user")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(userTaskId));

        given()
                .contentType(ContentType.JSON)
                .basePath(MANAGEMENT_INSTANCE_PATH)
                .pathParam("processId", ERRORS_PROCESS)
                .pathParam("processInstanceId", pId)
                .when()
                .post("retrigger")
                .then()
                .statusCode(200)
                .body("id", equalTo(pId))
                .body("fail", equalTo(true));

        given()
                .contentType(ContentType.JSON)
                .basePath(MANAGEMENT_INSTANCE_PATH)
                .pathParam("processId", ERRORS_PROCESS)
                .pathParam("processInstanceId", pId)
                .when()
                .get("error")
                .then()
                .statusCode(400)
                .body(equalTo("Process instance with id " + pId + " is not in error state"));

        userTaskId = given()
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "jdoe")
                .queryParam("group", "user")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("[0].id", not(equalTo(userTaskId)))
                .extract()
                .body()
                .path("[0].id");

        given()
                .contentType(ContentType.JSON)
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "jdoe")
                .queryParam("group", "user")
                .body(new TransitionInfo("complete", Map.of("fail", false)))
                .when()
                .post("/{userTaskId}/transition", userTaskId)
                .then()
                .statusCode(200);

        given()
                .accept(ContentType.JSON)
                .when()
                .pathParam("processId", ERRORS_PROCESS)
                .get("/{processId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }
}

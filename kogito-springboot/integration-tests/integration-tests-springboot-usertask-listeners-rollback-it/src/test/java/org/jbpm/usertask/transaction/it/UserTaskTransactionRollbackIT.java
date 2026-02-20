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
package org.jbpm.usertask.transaction.it;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.it.KogitoSpringbootApplication;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

/**
 * Integration test to verify that UserTaskEventListener exceptions cause transaction rollback.
 * 
 * This test verifies that when a UserTaskEventListener throws an exception during task completion,
 * the transaction is rolled back and the task remains in its previous state (Reserved instead of Completed).
 * 
 * The FailingUserTaskEventListener is configured via application.properties with:
 * app.listener.fail-on-complete=true
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
public class UserTaskTransactionRollbackIT {

    private static final String PROCESS_ID = "hiring";
    private static final String HIRING_ENDPOINT = "/" + PROCESS_ID;
    private static final String USER_TASKS_ENDPOINT = "/usertasks/instance";
    private static final String USER_TASKS_INSTANCE_ENDPOINT = USER_TASKS_ENDPOINT + "/{taskId}";
    private static final String USER_TASKS_TRANSITION_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/transition";

    @LocalServerPort
    int httpPort;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = httpPort;
    }

    @Test
    public void testListenerExceptionCausesTransactionRollback() {
        // 1. Start a hiring process
        Map<String, Object> candidateData = Map.of(
                "name", "Jon",
                "lastName", "Snow",
                "email", "jon@snow.org",
                "experience", 5,
                "skills", List.of("Java", "Kogito"));

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .body(Map.of("candidateData", candidateData))
                .when()
                .post(HIRING_ENDPOINT)
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        System.out.println("Started process instance with ID: " + processInstanceId);

        // 2. Get the HR Interview task
        String taskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "jdoe")
                .when()
                .get(USER_TASKS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract()
                .path("[0].id");

        // 3. Verify task is in Reserved state
        given()
                .contentType(ContentType.JSON)
                .queryParam("user", "jdoe")
                .when()
                .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("status.name", equalTo("Reserved"))
                .body("taskName", equalTo("HRInterview"));

        // 4. Try to complete the task - FailingUserTaskEventListener will throw exception
        // The transaction should rollback, returning 500 error
        given()
                .contentType(ContentType.JSON)
                .queryParam("user", "jdoe")
                .body(Map.of("transitionId", "complete", "data", Map.of("approve", true)))
                .when()
                .post(USER_TASKS_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(500); // Expecting error due to listener exception

        // 5. Verify the task is Still in Reserved state (transaction rolled back)
        given()
                .contentType(ContentType.JSON)
                .queryParam("user", "jdoe")
                .when()
                .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("status.name", equalTo("Reserved")) // Should still be Reserved, not Completed
                .body("taskName", equalTo("HRInterview"));
    }
}

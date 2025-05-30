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
package org.kie.kogito.quarkus.workflows;

import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.process.management.SlaPayload;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;

@QuarkusIntegrationTest
public class ProcessManagementIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testManagementTimersEndpoint() {
        String processInstanceId = given()
                .body(Map.of())
                .contentType(ContentType.JSON)
                .when()
                .post("/timers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().path("id");

        String nodeInstanceId = given()
                .when()
                .get("/management/processes/timers/instances/{processInstanceId}/nodeInstances", processInstanceId)
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .extract().path("[0].nodeInstanceId");

        given()
                .when()
                .get("/management/processes/timers/instances/{processInstanceId}/timers", processInstanceId)
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(4))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "[SLA-Process] timers"))))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "[CANCEL-Process] timers"))))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasEntry("nodeInstanceId", nodeInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "[SLA] Task"))))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasEntry("nodeInstanceId", nodeInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "Task-Boundary Timer"))));

        given()
                .when()
                .get("/management/processes/timers/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}/timers", processInstanceId, nodeInstanceId)
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasEntry("nodeInstanceId", nodeInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "[SLA] Task"))))
                .body("", hasItem(allOf(
                        hasEntry("processId", "timers"),
                        hasEntry("processInstanceId", processInstanceId),
                        hasEntry("nodeInstanceId", nodeInstanceId),
                        hasKey("timerId"),
                        hasEntry("description", "Task-Boundary Timer"))));
    }

    @Test
    public void testRescheduleSLATimersEndpoints() {
        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/timers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().path("id");

        String nodeInstanceId = given()
                .when()
                .get("/management/processes/timers/instances/{processInstanceId}/nodeInstances", processInstanceId)
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .extract().path("[0].nodeInstanceId");

        given()
                .body(new SlaPayload(ZonedDateTime.now()))
                .contentType(ContentType.JSON)
                .patch("/management/processes/timers/instances/{processInstanceId}/sla", processInstanceId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Process Instance '" + processInstanceId + "' SLA due date successfully updated"));

        given()
                .body(new SlaPayload(ZonedDateTime.now()))
                .contentType(ContentType.JSON)
                .when()
                .patch("/management/processes/timers/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}/sla", processInstanceId, nodeInstanceId)
                .then()
                .statusCode(200)
                .body("message", equalTo("Node Instance '" + nodeInstanceId + "' SLA due date successfully updated"));
    }
}

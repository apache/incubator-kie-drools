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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

@QuarkusIntegrationTest
class MultiInstanceTaskIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testStartProcess() {
        String processId = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("inputList", Arrays.asList("user1", "user2")))
                .post("/multiinstancetaskprocess")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        List<String> tasks = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("processId", processId)
                .when()
                .get("/multiinstancetaskprocess/{processId}/tasks")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2))
                .extract()
                .jsonPath().getList("id");

        assertThat(tasks).hasSize(2);

        given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .when()
                .body(Collections.singletonMap("output", "user3"))
                .post("/multiinstancetaskprocess/{id}/MultiInstanceTask/{tId}", processId, tasks.get(0))
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .when()
                .body(Collections.singletonMap("output", "user4"))
                .post("/multiinstancetaskprocess/{id}/MultiInstanceTask/{tId}", processId, tasks.get(1))
                .then()
                .statusCode(200)
                .body("inputList", hasItems("user1", "user2"))
                .body("outputList", hasItems("user3", "user4"));

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/multiinstancetaskprocess/{id}", processId)
                .then()
                .statusCode(404);
    }
}

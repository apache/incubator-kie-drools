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

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
class SubFlowsIT {

    public static final Duration TIMEOUT = Duration.ofSeconds(5);

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testSubFlows() {
        String mainId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("/MainFlow")
                .then()
                .statusCode(201)
                .extract().path("id");

        String[] eventFlow1Id = new String[1];
        eventFlow1Id[0] = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/EventFlow1")
                .then()
                .statusCode(200)
                .extract().path("[0].id");

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-source", "org.persistence")
                .header("ce-type", "event1")
                .header("ce-kogitoprocrefid", eventFlow1Id[0])
                .contentType(MediaType.APPLICATION_JSON)
                .accept(ContentType.JSON)
                .post("/")
                .then()
                .statusCode(202);

        String[] eventFlow2Id = new String[1];
        await().timeout(TIMEOUT).untilAsserted(() -> eventFlow2Id[0] = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/EventFlow2")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract().path("[0].id"));

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-source", "org.persistence")
                .header("ce-type", "event2")
                .header("ce-kogitoprocrefid", eventFlow2Id[0])
                .contentType(MediaType.APPLICATION_JSON)
                .accept(ContentType.JSON)
                .post("/")
                .then()
                .statusCode(202);

        await().timeout(TIMEOUT).untilAsserted(() -> eventFlow1Id[0] = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/EventFlow1")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract().path("[0].id"));

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-source", "org.persistence")
                .header("ce-type", "event1")
                .header("ce-kogitoprocrefid", eventFlow1Id[0])
                .contentType(MediaType.APPLICATION_JSON)
                .accept(ContentType.JSON)
                .post("/")
                .then()
                .statusCode(202);

        await().timeout(TIMEOUT).untilAsserted(() -> eventFlow2Id[0] = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/EventFlow2")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract().path("[0].id"));

        given()
                .header("ce-specversion", "1.0")
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-source", "org.persistence")
                .header("ce-type", "event2")
                .header("ce-kogitoprocrefid", eventFlow2Id[0])
                .contentType(MediaType.APPLICATION_JSON)
                .accept(ContentType.JSON)
                .post("/")
                .then()
                .statusCode(202);

        await().timeout(TIMEOUT).untilAsserted(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/MainFlow/{id}", mainId)
                .then()
                .statusCode(404));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/MainFlow")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));

    }

}

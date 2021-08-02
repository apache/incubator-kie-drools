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
package org.kie.kogito.it;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.Assert.assertEquals;

public abstract class PersistenceTest {

    public static final String PROCESS_ID = "hello";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testPersistence() {
        final String pid = given().contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("var1", "Tiago"))
                .post("/{processId}", PROCESS_ID)
                .then()
                .statusCode(201)
                .header("Location", not(emptyOrNullString()))
                .body("id", not(emptyOrNullString()))
                .body("var1", equalTo("Tiago"))
                .body("var2", equalTo("Hello Tiago! Script"))
                .extract()
                .path("id");

        final String createdPid = given().contentType(ContentType.JSON)
                .when()
                .get("/{processId}/{id}", PROCESS_ID, pid)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("var1", equalTo("Tiago"))
                .body("var2", equalTo("Hello Tiago! Script"))
                .extract()
                .path("id");

        assertEquals(createdPid, pid);

        given().contentType(ContentType.JSON)
                .when()
                .delete("/management/processes/{processId}/instances/{processInstanceId}", PROCESS_ID, pid)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .get("/greetings/{id}", pid)
                .then()
                .statusCode(404);
    }

    @Test
    void testHealthCheck() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }
}

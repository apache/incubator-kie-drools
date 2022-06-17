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
package org.kie.kogito.quarkus.workflows;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
class KeepActiveIT {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testKeepActive() {
        Map<String, Object> body = Collections.singletonMap("keepActive", true);
        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", body))
                .post("/keepActive")
                .then()
                .statusCode(201)
                .extract().path("id");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/keepActive/{id}", id)
                .then().statusCode(200).body("workflowdata.message", is("this will never end"));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .delete("/keepActive/{id}", id);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/keepActive/{id}", id)
                .then().statusCode(404);

    }

    @Test
    void testEndIt() {
        Map<String, Object> body = Collections.singletonMap("keepActive", false);
        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", body))
                .post("/keepActive")
                .then()
                .statusCode(201)
                .extract().path("id");
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/keepActive/{id}", id)
                .then().statusCode(404);
    }

}

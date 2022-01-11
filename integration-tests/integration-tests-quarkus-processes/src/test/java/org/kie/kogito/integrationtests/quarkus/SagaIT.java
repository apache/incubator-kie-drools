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
package org.kie.kogito.integrationtests.quarkus;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusIntegrationTest
public class SagaIT {

    public static final String PATH = "/saga_error";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testSuccessProcess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Arthur");
        payload.put("error", "HasNoError");
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .post(PATH)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Arthur"))
                .body("response", equalTo("Hello Arthur!"))
                .body("responseError", equalTo("Hello HasNoError!"));
    }

    @Test
    void testFailedProcess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Arthur");
        payload.put("error", "error");
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(payload)
                .post(PATH)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Arthur"))
                .body("response", equalTo("Failed"))
                .body("responseError", nullValue());
    }
}

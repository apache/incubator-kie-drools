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

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTestResource(OpenAPIArrayMockService.class)
@QuarkusIntegrationTest
class OpenAPIArrayFlowIT {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testArray() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("inputArray", Arrays.asList(1, 2, 3, 4)))
                .post("/openapiarray")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("workflowdata.response", is(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testInt() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.emptyMap())
                .post("/long-call")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }
}

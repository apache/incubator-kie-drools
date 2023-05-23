/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.legacy.rules;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TmsEndpointTest {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .queryParam("string", "test")
                .when().post("/test-tms")
                .then()
                .statusCode(200)
                .body(is("1")); // 1 rule fired

        given()
                .when().get("/test-tms")
                .then()
                .statusCode(200)
                .body(is("4")); // the session contains an int equal to the length of the inserted string

        given()
                .queryParam("string", "test")
                .when().delete("/test-tms")
                .then()
                .statusCode(200)
                .body(is("0")); // no rule fired

        given()
                .when().get("/test-tms")
                .then()
                .statusCode(200)
                .body(is("-1")); // the deletion of the string also causes the retraction of the logical asserted int
    }
}

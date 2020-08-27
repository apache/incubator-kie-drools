/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.Conditional.class)
class FlexibleProcessTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testInstantiateProcess() {
        Map<String, String> params = new HashMap<>();
        params.put("var1", "first");
        params.put("var2", "second");

        String pid = given()
                .contentType(ContentType.JSON)
            .when()
                .body(params)
                .post("/AdHocProcess")
            .then()
                .statusCode(201)
                .header("Location", not(emptyOrNullString()))
                .body("id", not(emptyOrNullString()))
                .body("var1", equalTo("Hello first! Script"))
                .body("var2", equalTo("second Script 2"))
            .extract()
                .path("id");

        given()
                .contentType(ContentType.JSON)
            .when()
                .get("/AdHocProcess/{pid}", pid)
            .then()
                .statusCode(200);
    }

    @Test
    void testProcessException() {
        Map<String, String> params = new HashMap<>();
        params.put("var1", "exception");
        params.put("var2", "second");

        given()
            .contentType(ContentType.JSON)
            .when()
            .body(params)
            .post("/AdHocProcess")
            .then()
            .statusCode(500);
    }
}

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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusIntegrationTest
class ExpressionRestIT {

    @Test
    void testExpressionRest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("pepe", "pepa")
                .body("{\"workflowdata\":{\"numbers\":[{\"x\":2, \"y\": 1},{\"x\":4, \"y\": 3}]}, \"randomAdditionalProperty\":\"Im ignored in runtimes but will be visible on data index\"}").when()
                .post("/expression")
                .then()
                .statusCode(201)
                .body("workflowdata.result", is(4))
                .body("workflowdata.originalFirstX", is(2))
                .body("workflowdata.number", nullValue())
                .body("workflowdata.message", is("my name is javierito and in my native language dog is translated to perro and the header pepe is pepa"))
                .body("workflowdata.user", is("anonymous"))
                .body("workflowdata.discardedResult", nullValue());
    }

    @Test
    void testExpressionInputValidation() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\":{\"numbers\":[{\"x\":\"abcdedf\", \"y\": 1},{\"x\":4, \"y\": 3}]}}").when()
                .post("/expression")
                .then()
                .statusCode(is(400))
                .body("message", notNullValue())
                .body("id", nullValue());
    }

    @Test
    void testExpressionOutputValidation() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\":{\"numbers\":[{\"x\":2, \"y\": 1},{\"x\":4, \"y\": 3}]}}").when()
                .post("/invalidOutputExpression")
                .then()
                .statusCode(is(400))
                .body("message", containsString("message"))
                .body("id", notNullValue());
    }
}

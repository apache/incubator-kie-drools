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
package org.kie.kogito.process.dynamic;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;

@QuarkusTest
public class DynamicCallResourceTest {

    @Test
    void testDynamicCall() {

        String id = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/dynamicWait")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/dynamicWait/" + id)
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "port", 8081, "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id + "/rest")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get("/dynamicWait/" + id)
                .then()
                .statusCode(404);
    }

}

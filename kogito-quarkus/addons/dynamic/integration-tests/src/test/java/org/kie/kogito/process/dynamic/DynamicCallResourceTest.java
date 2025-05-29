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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class DynamicCallResourceTest {

    @Test
    void testDynamicCall() {

        String id = createDynamicWaitProcessInstance();

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

    @Test
    void testDynamicCallWithInvalidProcessId() {
        String invalidId = "invalid-process-id";

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "port", 8081, "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + invalidId + "/rest")
                .then()
                .statusCode(400)
                .body("message", containsString("Cannot find process instance"));
    }

    @Test
    void testDynamicCallWithMissingUrlParameter() {
        String id = createDynamicWaitProcessInstance();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("port", 8081))
                .when()
                .post("/_dynamic/dynamicWait/" + id + "/rest")
                .then()
                .statusCode(400)
                .body("message", containsString("Missing required parameter Url"));
    }

    @Test
    void testDynamicCallWithMissingMethodParameter() {
        String id = createDynamicWaitProcessInstance();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "port", 8081, "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id + "/rest")
                .then()
                .statusCode(500)
                .body("message", containsString("Method Not Allowed"));
    }

    @Test
    void testDynamicCallWithMissingPortParameter() {
        String id = createDynamicWaitProcessInstance();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id + "/rest")
                .then()
                .statusCode(500);
    }

    @Test
    void testDynamicCallWithInvalidEndpointFormat() {
        String id = createDynamicWaitProcessInstance();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "invalid-endpoint-format", "port", 8081, "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id + "/rest")
                .then()
                .statusCode(404)
                .body("message", containsString("endpoint invalid-endpoint-format failed"));
    }

    @Test
    void testConcurrentDynamicCalls() throws Exception {
        String id1 = createDynamicWaitProcessInstance();

        String id2 = createDynamicWaitProcessInstance();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "port", 8081, "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id1 + "/rest")
                .then()
                .log().body()
                .statusCode(200));

        Future<?> future2 = executor.submit(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of("endpoint", "/example/{processInstanceId}", "port", 8081, "method", "post", "outputExpression", "{message}"))
                .when()
                .post("/_dynamic/dynamicWait/" + id2 + "/rest")
                .then()
                .log().body()
                .statusCode(200));

        future1.get();
        future2.get();

        executor.shutdown();
    }

    private String createDynamicWaitProcessInstance() {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/dynamicWait")
                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");
    }

}

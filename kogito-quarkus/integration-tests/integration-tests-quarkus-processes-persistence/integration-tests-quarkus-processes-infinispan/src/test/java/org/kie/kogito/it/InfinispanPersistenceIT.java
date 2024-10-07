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
package org.kie.kogito.it;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusIntegrationTest
class InfinispanPersistenceIT extends PersistenceTest {
    private static final String USER_TASK_BASE_PATH = "/usertasks/instance";

    @Test
    public void testStartApprovalAuthorized() {
        // start new approval
        String id = given()
                .body("{}")
                .contentType(ContentType.JSON)
                .when()
                .post("/AddedTask")
                .then()
                .statusCode(201)
                .body("id", notNullValue()).extract().path("id");
        // get all active approvals
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/AddedTask")
                .then()
                .statusCode(200)
                .body("size()", is(1), "[0].id", is(id));

        // get just started approval
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/AddedTask/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id));

        // tasks assigned in just started approval

        String userTaskId = given()
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("user", "mary")
                .queryParam("group", "managers")
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .path("[0].id");

        given()
                .contentType(ContentType.JSON)
                .basePath(USER_TASK_BASE_PATH)
                .queryParam("transitionId", "complete")
                .queryParam("user", "mary")
                .queryParam("group", "managers")
                .body(Collections.emptyMap())
                .when()
                .post("/{userTaskId}/transition", userTaskId)
                .then()
                .statusCode(200);

        // get all active approvals
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/AddedTask")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }
}

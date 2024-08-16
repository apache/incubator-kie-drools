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
package org.kie.kogito.integrationtests.springboot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
class GatewayFEELTest extends BaseRestTest {

    @Test
    void testGatewayFEEL() {
        given().body("{ \"va\": true, \"vb\": false }")
                .contentType(ContentType.JSON)
                .when()
                .post("/BPMN2GatewayFEEL")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("task1", is("ok"))
                .body("task2", is("ok"))
                .body("task3", nullValue())
                .extract()
                .path("id");
        given().body("{ \"va\": false, \"vb\": true }")
                .contentType(ContentType.JSON)
                .when()
                .post("/BPMN2GatewayFEEL")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("task1", is("ok"))
                .body("task2", nullValue())
                .body("task3", is("ok"))
                .extract()
                .path("id");
    }
}

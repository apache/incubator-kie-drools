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
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
class BasicAddTest extends BaseRestTest {

    @Test
    void testWholeModel() {
        given().body("{ \"a\": \"v1\", \"b\": \"v2\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/basicAdd")
                .then()
                .statusCode(200)
                .body("decision", is("v1v2"));
    }

    @Test
    void testWholeModel_dmnresult() {
        given().body("{ \"a\": \"v1\", \"b\": \"v2\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/basicAdd/dmnresult")
                .then()
                .statusCode(200)
                .body("dmnContext.decision", is("v1v2"));
    }

    @Test
    void testDs1() {
        given().body("{ \"a\": \"v1\", \"b\": \"v2\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/basicAdd/ds1")
                .then()
                .statusCode(200)
                .body(is("\"v1v2\"")); // a JSON string literal: "v1v2"
    }

    @Test
    void testDs1_dmnresult() {
        given().body("{ \"a\": \"v1\", \"b\": \"v2\" }")
                .contentType(ContentType.JSON)
                .when()
                .post("/basicAdd/ds1/dmnresult")
                .then()
                .statusCode(200)
                .body("dmnContext.decision", is("v1v2"));
    }

}

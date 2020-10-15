/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.integrationtests.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class BasicAddTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

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

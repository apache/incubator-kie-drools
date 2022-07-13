/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.workflows;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTestResource(GrpcServerPortResource.class)
@QuarkusIntegrationTest
class RPCGreetIT {
    @Test
    void testEnglish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"John\", \"language\":\"English\"}}").when()
                .post("/rpcgreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"))
                .body("workflowdata.state", is("SUCCESS"))
                .body("workflowdata.innerMessage.number", is(23));

    }

    @Test
    void testSpanish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"Javierito\", \"language\":\"Spanish\", \"unknown\": true}}").when()
                .post("/rpcgreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Saludos"))
                .body("workflowdata.state", nullValue());
    }

    @Test
    void testDefaultLanguage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"John\"}}").when()
                .post("/rpcgreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"));
    }

    @Test
    void testUnsupportedLanguage() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"Jan\", \"language\":\"Czech\"}}").when()
                .post("/rpcgreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"));
    }
}

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

import com.fasterxml.jackson.databind.JsonNode;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
class PythonFlowIT {

    @Test
    void testPythonScript() {
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"x\" : 4}").post("/Duplicate")
                .then().statusCode(201).body("workflowdata.result", is(8));
    }

    @Test
    void testPythonService() {
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"x\" : 5, \"y\":3}").post("/Factorial")
                .then().statusCode(201).body("workflowdata.factorial", is(120)).body("workflowdata.module", is(2.0f)).body("workflowdata.isClose", is(true));
    }

    @Test
    public void testPythonDateTime() {
        JsonNode node = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{}").when()
                .post("/DateTime")
                .then()
                .statusCode(201)
                .extract().as(JsonNode.class);
        assertThat(node.get("workflowdata").get("year").intValue()).isGreaterThanOrEqualTo(2025);
    }

    @Test
    public void testPythonDateTimeString() {
        JsonNode node = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{}").when()
                .post("/DateTimeString")
                .then()
                .statusCode(201)
                .extract().as(JsonNode.class);
        assertThat(node.get("workflowdata").get("date").textValue()).isNotNull();
    }
}

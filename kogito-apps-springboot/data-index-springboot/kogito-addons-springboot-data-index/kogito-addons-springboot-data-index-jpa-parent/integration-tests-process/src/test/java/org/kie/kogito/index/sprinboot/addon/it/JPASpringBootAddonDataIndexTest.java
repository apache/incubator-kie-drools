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
package org.kie.kogito.index.sprinboot.addon.it;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.KogitoSpringBootApplication;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringBootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JPASpringBootAddonDataIndexTest {

    @LocalServerPort
    int randomServerPort;

    static {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = randomServerPort;
    }

    @Test
    void testDataIndexAddon() {
        String processDefId = "hello";
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessDefinitions(where: { id: {equal: \\\"" + processDefId +
                "\\\"}}){ id, version, name, addons, nodes { name } } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessDefinitions.size()", is(1))
                .body("data.ProcessDefinitions[0].id", is("hello"))
                .body("data.ProcessDefinitions[0].version", is("1.0"))
                .body("data.ProcessDefinitions[0].name", is("hello"))
                .body("data.ProcessDefinitions[0].addons", allOf(
                        hasItem("jdbc-persistence"),
                        hasItem("process-svg")))
                .body("data.ProcessDefinitions[0].nodes.size()", is(2));

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .post("/hello")
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().path("id");

        assertThat(processInstanceId)
                .isNotBlank();

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{ProcessInstances(where: { id: {equal: \\\"" + processInstanceId + "\\\"}}){ id, state,  nodeDefinitions { name } } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances.size()", is(1))
                .body("data.ProcessInstances[0].id", is(processInstanceId))
                .body("data.ProcessInstances[0].state", is("COMPLETED"))
                .body("data.ProcessInstances[0].nodeDefinitions.size()", is(2));
    }

    @Test
    void testGraphQLUI() {
        given().contentType(ContentType.HTML)
                .when().get("/graphiql")
                .then().statusCode(200)
                .body("html.head.title", containsString("GraphiQL"));
    }

}

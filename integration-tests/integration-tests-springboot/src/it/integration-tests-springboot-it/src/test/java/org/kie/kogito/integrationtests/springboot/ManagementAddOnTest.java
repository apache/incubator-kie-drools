/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.integrationtests.springboot;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.Conditional.class)
class ManagementAddOnTest extends BaseRestTest {

    @Test
    void testGetProcessNodesWithInvalidProcessId() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/management/processes/{processId}/nodes", "aprocess")
                .then()
                .statusCode(404)
                .body(equalTo("Process with id aprocess not found"));
    }

    @Test
    void testAbortProcessInstance() {
        String pid = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/greetings")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("test", emptyOrNullString())
                .header("Location", not(emptyOrNullString()))
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/management/processes/{processId}/instances/{processInstanceId}", "greetings", pid)
                .then()
                .statusCode(200);
    }

    @Test
    void testGetNodeInstances() {
        String pid = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/greetings")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("test", emptyOrNullString())
                .header("Location", not(emptyOrNullString()))
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/management/processes/{processId}/instances/{processInstanceId}/nodeInstances", "greetings", pid)
                .then()
                .statusCode(200)
                .body("$.size", is(2))
                .body("[0].name", is("Task"))
                .body("[0].state", is(0))
                .body("[1].name", is("Task"))
                .body("[1].state", is(0));
    }

    @Test
    void testGetProcessNodes() {
        given()
                .contentType(ContentType.JSON)
            .when()
                .get("/management/processes/{processId}/nodes", "greetings")
            .then()
                .statusCode(200)
                .body("$.size", is(10))
                .body("[0].id", is(1))
                .body("[0].name", is("End"))
                .body("[0].type", is("EndNode"))
                .body("[0].uniqueId", is("1"))
                .body("[0].nodeDefinitionId", not(emptyOrNullString()))
                .body("[9].id", is(10))
                .body("[9].name", is("BoundaryEvent"))
                .body("[9].type", is("BoundaryEventNode"))
                .body("[9].uniqueId", is("10"))
                .body("[9].nodeDefinitionId", not(emptyOrNullString()));
    }
}

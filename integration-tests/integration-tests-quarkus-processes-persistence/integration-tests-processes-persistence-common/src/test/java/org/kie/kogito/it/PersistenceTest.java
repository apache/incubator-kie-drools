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
package org.kie.kogito.it;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Person;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class PersistenceTest {

    public static final String PROCESS_ID = "hello";
    public static String PROCESS_EMBEDDED_ID = "embedded";
    public static String PROCESS_MULTIPLE_INSTANCES_EMBEDDED_ID = "MultipleInstanceEmbeddedSubProcess";
    public static String PROCESS_MULTIPLE_INSTANCES_ID = "MultipleInstanceSubProcess";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testPersistence() {
        Person person = new Person("Name", 10);
        final String pid = given().contentType(ContentType.JSON)
                .when()
                .body(Map.of("var1", "Tiago", "person", person))
                .post("/{processId}", PROCESS_ID)
                .then()
                .statusCode(201)
                .header("Location", not(emptyOrNullString()))
                .body("id", not(emptyOrNullString()))
                .body("var1", equalTo("Tiago"))
                .body("var2", equalTo("Hello Tiago! Script"))
                .body("person.name", equalTo(person.getName()))
                .body("person.age", equalTo(person.getAge()))
                .extract()
                .path("id");

        final String createdPid = given().contentType(ContentType.JSON)
                .when()
                .get("/{processId}/{id}", PROCESS_ID, pid)
                .then()
                .statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("var1", equalTo("Tiago"))
                .body("var2", equalTo("Hello Tiago! Script"))
                .body("person.name", equalTo(person.getName()))
                .body("person.age", equalTo(person.getAge()))
                .extract()
                .path("id");

        assertEquals(createdPid, pid);

        given().contentType(ContentType.JSON)
                .when()
                .get("/greetings/{id}", pid)
                .then()
                .statusCode(404);
    }

    @Test
    void testHealthCheck() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void testEmbeddedProcess() {
        final String pId = given().contentType(ContentType.JSON)
                .pathParam("processId", PROCESS_EMBEDDED_ID)
                .when()
                .post("/{processId}")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        String taskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .queryParam("group", "managers")
                .pathParam("pId", pId)
                .pathParam("processId", PROCESS_EMBEDDED_ID)
                .when()
                .get("/{processId}/{pId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .pathParam("pId", pId)
                .pathParam("taskId", taskId)
                .pathParam("processId", PROCESS_EMBEDDED_ID)
                .body("{}")
                .when()
                .post("/{processId}/{pId}/Task/{taskId}/phases/complete")
                .then()
                .statusCode(200);

    }

    @Test
    void testMultipleEmbeddedInstance() {
        String pId = given().contentType(ContentType.JSON)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_EMBEDDED_ID)
                .when()
                .post("/{processId}")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        String taskId = given()
                .contentType(ContentType.JSON)
                .queryParam("user", "admin")
                .pathParam("pId", pId)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_EMBEDDED_ID)
                .when()
                .get("/{processId}/{pId}/tasks")
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .pathParam("pId", pId)
                .pathParam("taskId", taskId)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_EMBEDDED_ID)
                .queryParam("user", "admin")
                .body("{}")
                .when()
                .post("/{processId}/{pId}/Task/{taskId}/phases/complete")
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_EMBEDDED_ID)
                .pathParam("pId", pId)
                .when()
                .get("/{processId}/{pId}")
                .then()
                .statusCode(404);
    }

    @Test
    void testMultipleInstance() {
        String pId = given().contentType(ContentType.JSON)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_ID)
                .when()
                .post("/{processId}")
                .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        given().contentType(ContentType.JSON)
                .pathParam("processId", PROCESS_MULTIPLE_INSTANCES_ID)
                .pathParam("pId", pId)
                .when()
                .get("/{processId}/{pId}")
                .then()
                .statusCode(404);
    }
}

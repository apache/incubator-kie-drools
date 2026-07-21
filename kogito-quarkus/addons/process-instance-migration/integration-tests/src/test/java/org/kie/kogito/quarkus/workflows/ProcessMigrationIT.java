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

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.process.migration.ProcessMigrationSpec;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusIntegrationTest
@QuarkusTestResource(value = PostgreSqlQuarkusTestResource.class, restrictToAnnotatedClass = true)
public class ProcessMigrationIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testMigrationEndpointMigrateAllInstances() {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .when()
                .post("/signal")
                .then()
                .statusCode(201);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .when()
                .post("/signal")
                .then()
                .statusCode(201);

        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId("signal2");
        processMigrationSpec.setTargetProcessVersion("1.0");

        given()
                .contentType("application/json")
                .body(processMigrationSpec)
                .when()
                .post("/management/processes/{processId}/migrate", "signal")
                .then()
                .body(notNullValue());

        given()
                .when()
                .get("/signal2")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(2));
    }

    @Test
    void testMigrationEndpointMigrateOneInstance() {

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .when()
                .post("/signal")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .when()
                .post("/signal")
                .then()
                .statusCode(201);

        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId("signal2");
        processMigrationSpec.setTargetProcessVersion("1.0");

        given()
                .contentType("application/json")
                .body(processMigrationSpec)
                .when()
                .post("/management/processes/{processId}/instances/{processInstanceId}/migrate", "signal", processInstanceId)
                .then()
                .body(notNullValue());

        given()
                .when()
                .get("/signal2")
                .then()
                .statusCode(200)
                .body("id", hasItem(processInstanceId));
        ;
    }
}

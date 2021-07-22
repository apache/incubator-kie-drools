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

package org.kie.kogito.persistence.inmemory.postgresql.it;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class InmemoryPostgreSQLResourceTest {

    @Test
    public void testListAll() {
        // List all, should have all the database has initially
        given()
                .when().get("/inmemory-postgresql")
                .then()
                .statusCode(200)
                .body(
                        containsString("test1"),
                        containsString("test2"),
                        containsString("test3"),
                        containsString("test4"));

        // Delete the test1
        given()
                .when().delete("/inmemory-postgresql/1")
                .then()
                .statusCode(204);

        // List all, test1 should be missing now
        given()
                .when().get("/inmemory-postgresql")
                .then()
                .statusCode(200)
                .body(
                        not(containsString("test1")),
                        containsString("test2"),
                        containsString("test3"),
                        containsString("test4"));
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.quarkus.jsonb.it;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Test various OptaPlanner operations running in Quarkus
 */
@QuarkusTest
public class OptaPlannerTestResourceTest {

    @Test
    @Timeout(600)
    public void solveWithSolverFactory() throws Exception {
        RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .body("{\"valueList\":[\"v1\",\"v2\"],\"entityList\":[{},{}]}")
                .post("/optaplanner/test/solver-factory")
                .then()
                .body(is(
                        "{\"entityList\":[{\"value\":\"v1\"},{\"value\":\"v2\"}],\"score\":\"0\",\"valueList\":[\"v1\",\"v2\"]}"));
    }

}

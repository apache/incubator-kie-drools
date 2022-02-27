/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.drl.it;

import java.io.StringReader;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Test various OptaPlanner operations running in Quarkus
 */

@QuarkusTest
class OptaPlannerTestResourceTest {

    @Test
    @Timeout(600)
    void solveWithSolverFactory() throws Exception {
        Properties result = new Properties();
        result.load(new StringReader(RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .post("/optaplanner/test/solver-factory")
                .then()
                .extract().body().asString()));
        Assertions.assertEquals("0", result.get("score"));
        Assertions.assertNotNull(result.get("entity.0.fullValue"));
        Assertions.assertNotNull(result.get("entity.1.fullValue"));
        Assertions.assertNotEquals(result.get("entity.0.fullValue"), result.get("entity.1.fullValue"),
                "Both entities have the same value. Maybe property reactive is set to ALWAYS?");
    }

}

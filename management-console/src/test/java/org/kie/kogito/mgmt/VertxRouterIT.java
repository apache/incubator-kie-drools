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
package org.kie.kogito.mgmt;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class VertxRouterIT {

    @Test
    public void testHandlePath() {
        given().when().get("/ProcessInstances")
                .then()
                .statusCode(200);

        given().when().get("/Process/a1e139d5-4e77-48c9-84ae-34578e904e5a")
                .then()
                .statusCode(200);

        given().when().get("/DomainExplorer")
                .then()
                .statusCode(200);

        given().when().get("/DomainExplorer/travels")
                .then()
                .statusCode(200);

        given().when().get("/JobsManagement")
                .then()
                .statusCode(200);

        given().when().get("/Another")
                .then()
                .statusCode(404);
    }
}

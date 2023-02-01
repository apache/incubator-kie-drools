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
package org.kie.kogito.jobs.service.resource;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@QuarkusTest
class JobServiceManagementResourceIT {

    private static final String HEALTH_ENDPOINT = "/q/health/ready";
    public static final String MANAGEMENT_SHUTDOWN_ENDPOINT = "/management/shutdown";

    @Test
    public void testShutdown() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(HEALTH_ENDPOINT)
                .then()
                .statusCode(200);

        given()
                .when()
                .post(MANAGEMENT_SHUTDOWN_ENDPOINT)
                .then()
                .statusCode(200);

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(HEALTH_ENDPOINT)
                .then()
                .statusCode(503));
    }
}

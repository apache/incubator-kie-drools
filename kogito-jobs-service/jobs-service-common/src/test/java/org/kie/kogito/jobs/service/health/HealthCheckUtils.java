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
package org.kie.kogito.jobs.service.health;

import java.util.concurrent.TimeUnit;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class HealthCheckUtils {

    public static final String HEALTH_ENDPOINT = "/q/health";
    private static final int OK = 200;

    private HealthCheckUtils() {
    }

    /**
     * Helper method that can be used along the tests to ensure jobs service ready health check passes before executing
     * other tests or invocations.
     */
    public static void awaitReadyHealthCheck(int timeout, TimeUnit timeUnit) {
        //health check - wait to be ready
        await()
                .atMost(1, MINUTES)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(HEALTH_ENDPOINT)
                        .then()
                        .statusCode(OK));
    }
}

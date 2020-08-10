/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.it.jobs;

import java.util.function.IntSupplier;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.with;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.kie.kogito.testcontainers.JobServiceContainer.KOGITO_SERVICE_PORT;

public abstract class BaseProcessTimerIT {

    public static final String TIMERS = "timers";
    public static final String TIMERS_CYCLE = "timerscycle";
    public static final String TIMERS_ON_TASK = "timersOnTask";
    public static final String KOGITO_SERVICE_URL = "kogito.service.url";
    static Integer httpPort;

    public static void beforeAll(IntSupplier httpPortSupplier) {
        httpPort = httpPortSupplier.getAsInt();
        //Container should have access the host port where the test is running
        //It should be called be fore the container is instantiated
        Testcontainers.exposeHostPorts(httpPort);
        //the hostname for the container to access the host is "host.testcontainers.internal"
        //https://www.testcontainers.org/features/networking/#exposing-host-ports-to-the-container
        System.setProperty(KOGITO_SERVICE_URL, "http://host.testcontainers.internal:" + httpPort);
        System.setProperty(KOGITO_SERVICE_PORT, String.valueOf(httpPort));
        RestAssured.port = httpPort;
    }

    //Timers Tests
    @Test
    public void testTimers() {
        String id = createTimer(new RequestPayload("PT02S"), TIMERS);
        Object id2 = getTimerById(id, TIMERS);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS));
    }

    @Test
    public void testCancelTimer() {
        String id = createTimer(new RequestPayload("PT030S"), TIMERS);
        Object id2 = deleteTimer(id, TIMERS);
        assertThat(id).isEqualTo(id2);
        getTimerWithStatusCode(id, 204, TIMERS);
    }

    //Cycle Timers Tests
    @Test
    public void testTimerCycle() {
        String id = createTimer(new RequestPayload("R2/PT1S"), TIMERS_CYCLE);
        String id2 = getTimerById(id, TIMERS_CYCLE);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS));
    }

    @Test
    public void testDeleteTimerCycle() {
        String id = createTimer(new RequestPayload("R20/PT1S"), TIMERS_CYCLE);
        String id2 = getTimerById(id, TIMERS_CYCLE);
        assertThat(id).isEqualTo(id2);
        deleteTimer(id, TIMERS_CYCLE);
    }

    //Boundary Timers Tests
    @Test
    public void testBoundaryTimersOnTask() {
        String id = createTimer(new RequestPayload("PT02S"), TIMERS_ON_TASK);
        String id2 = getTimerById(id, TIMERS_ON_TASK);
        assertThat(id).isEqualTo(id2);
        with().pollDelay(2, SECONDS)
                .atMost(3, SECONDS)
                .untilAsserted(() -> getTimerWithStatusCode(id, 204, TIMERS_ON_TASK));
    }

    @Test
    public void testDeleteBoundaryTimersOnTask() {
        String id = createTimer(new RequestPayload("PT030S"), TIMERS_ON_TASK);
        String id2 = getTimerById(id, TIMERS_ON_TASK);
        assertThat(id).isEqualTo(id2);
        deleteTimer(id, TIMERS_ON_TASK);
    }

    private ValidatableResponse getTimerWithStatusCode(String id, int code, String path) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .get("/" + path + "/{id}", id)
                .then()
                .statusCode(code);
    }

    private String createTimer(RequestPayload delay, String path) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(delay)
                .when()
                .post("/" + path)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private String getTimerById(String id, String path) {
        return getTimerWithStatusCode(id, 200, path)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    private Object deleteTimer(String id, String path) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .delete("/" + path + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    /**
     * Simple bean class to send as body on the requests
     */
    private class RequestPayload {

        private String delay;

        public RequestPayload(String delay) {
            this.delay = delay;
        }

        public String getDelay() {
            return delay;
        }
    }
}
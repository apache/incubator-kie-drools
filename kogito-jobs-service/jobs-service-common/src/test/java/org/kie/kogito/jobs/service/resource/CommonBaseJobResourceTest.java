/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.kie.kogito.jobs.service.scheduler.impl.VertxTimerServiceScheduler;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.jobs.service.health.HealthCheckUtils.awaitReadyHealthCheck;

public abstract class CommonBaseJobResourceTest {
    protected static final String CALLBACK_ENDPOINT = "http://localhost:%d/callback";
    protected static final String PROCESS_ID = "processId";
    protected static final String PROCESS_INSTANCE_ID = "processInstanceId";
    protected static final String ROOT_PROCESS_ID = "rootProcessId";
    protected static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";
    protected static final String NODE_INSTANCE_ID = "nodeInstanceId";
    protected static final int PRIORITY = 1;
    protected static final int BAD_REQUEST = 400;
    protected static final int OK = 200;

    @ConfigProperty(name = "quarkus.http.test-port")
    protected int port;

    @Inject
    protected ObjectMapper objectMapper;

    @Inject
    protected TimerDelegateJobScheduler scheduler;

    @Inject
    protected VertxTimerServiceScheduler timer;

    @BeforeEach
    void init() {
        //health check - wait to be ready
        awaitReadyHealthCheck(1, MINUTES);
    }

    @AfterEach
    void tearDown() {
        scheduler.setForceExecuteExpiredJobs(false);
    }

    protected abstract String getCreatePath();

    protected abstract String getGetJobQuery(String jobId);

    protected String getCallbackEndpoint() {
        return String.format(CALLBACK_ENDPOINT, port);
    }

    protected ValidatableResponse create(String body) {
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(getCreatePath())
                .then();
    }

    protected <T> T getJob(String jobId, Class<T> type) {
        return getJob(jobId, type, 200);
    }

    protected <T> T getJob(String jobId, Class<T> type, int code) {
        try {
            return objectMapper.readValue(given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .get(getGetJobQuery(jobId))
                    .then()
                    .statusCode(code)
                    .extract().body().asByteArray(), type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected ValidatableResponse deleteJob(String jobId) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .delete(getGetJobQuery(jobId))
                .then()
                .statusCode(200);
    }

    protected void assertJobHasFinished(String jobId, long atMostTimeoutInSeconds) {
        await()
                .atMost(atMostTimeoutInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(getGetJobQuery(jobId))
                        .then()
                        .statusCode(404));
    }
}

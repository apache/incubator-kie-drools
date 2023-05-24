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

package org.kie.kogito.jobs.service.messaging;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.api.URIBuilder;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.serialization.JobCloudEventSerializer;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.jobs.service.health.HealthCheckUtils.awaitReadyHealthCheck;

public abstract class BaseMessagingApiTest {

    private static final String KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_EMITTER = "kogito-job-service-job-request-events-emitter";

    private static final String TEST_SOURCE = "http://testSource";
    private static final String JOB_ID_1 = "JOB_ID_1";
    private static final String JOB_ID_2 = "JOB_ID_2";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final String KOGITO_ADDONS = "KOGITO_ADDONS";
    private static final String NODE_INSTANCE_ID_1 = "NODE_INSTANCE_ID_1";
    private static final String NODE_INSTANCE_ID_2 = "NODE_INSTANCE_ID_2";

    private static final long REPEAT_INTERVAL = 1000;
    private static final int REPEAT_LIMIT = 3;
    private static final int PRIORITY = 0;

    private static final AtomicInteger CHECK_CALLBACK_NODE_INSTANCE_ID = new AtomicInteger();
    private static final int CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS = 2 * 60;
    private static final int CALLBACK_EXECUTIONS_QUERY_POLL_INTERVAL_IN_MILLISECONDS = 3000;

    private static final String CALLBACK_RESOURCE_PATH = "/test/callback/management/jobs";

    @Inject
    @ConfigProperty(name = "quarkus.http.test-port")
    public int port;

    @Inject
    @Channel(KOGITO_JOB_SERVICE_JOB_REQUEST_EVENTS_EMITTER)
    public Emitter<String> jobEventsEmitter;

    private final JobCloudEventSerializer serializer = new JobCloudEventSerializer();

    @BeforeEach
    void init() throws Exception {
        //health check - wait to be ready
        awaitReadyHealthCheck(2, MINUTES);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    protected void createJob() {
        assertCallbackResource();
        // create a job service request event and send it to the jobs service.
        ZonedDateTime expiration = ZonedDateTime.now().plusSeconds(10);

        String callback = buildCallbackEndpoint(getCallbackResourceURL(), PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID_1);
        CreateProcessInstanceJobRequestEvent event = CreateProcessInstanceJobRequestEvent.builder()
                .source(URI.create(TEST_SOURCE))
                .job(new Job(JOB_ID_1,
                        expiration,
                        PRIORITY,
                        callback,
                        PROCESS_INSTANCE_ID,
                        ROOT_PROCESS_INSTANCE_ID,
                        PROCESS_ID,
                        ROOT_PROCESS_ID,
                        REPEAT_INTERVAL,
                        REPEAT_LIMIT,
                        NODE_INSTANCE_ID_1))
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .kogitoAddons(KOGITO_ADDONS)
                .build();
        String jsonEvent = serializer.serialize(event);
        jobEventsEmitter.send(jsonEvent);

        // wait until we can verify that the job was executed or fail if the CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS
        // elapsed.
        waitUntilResult(() -> getJobCallbackExecutions(NODE_INSTANCE_ID_1),
                executions -> Objects.equals(executions, "2"),
                CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS,
                CALLBACK_EXECUTIONS_QUERY_POLL_INTERVAL_IN_MILLISECONDS);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    protected void cancelJob() {
        assertCallbackResource();
        // create a job service request event and send it to the jobs service.
        ZonedDateTime expiration = ZonedDateTime.now().plusDays(1);
        String callback = buildCallbackEndpoint(getCallbackResourceURL(), PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID_2);
        CreateProcessInstanceJobRequestEvent createJobEvent = CreateProcessInstanceJobRequestEvent.builder()
                .source(URI.create(TEST_SOURCE))
                .job(new Job(JOB_ID_2,
                        expiration,
                        PRIORITY,
                        callback,
                        PROCESS_INSTANCE_ID,
                        ROOT_PROCESS_INSTANCE_ID,
                        PROCESS_ID,
                        ROOT_PROCESS_ID,
                        REPEAT_INTERVAL,
                        REPEAT_LIMIT,
                        NODE_INSTANCE_ID_2))
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .kogitoAddons(KOGITO_ADDONS)
                .build();
        String createJobEventJson = serializer.serialize(createJobEvent);
        jobEventsEmitter.send(createJobEventJson);

        // wait until the job is created or fail if the CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS elapsed.
        String jobUrl = "/jobs/" + JOB_ID_2;
        waitUntilResult(() -> getJob(jobUrl),
                response -> expectedJobExists(JOB_ID_2, response),
                CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS,
                CALLBACK_EXECUTIONS_QUERY_POLL_INTERVAL_IN_MILLISECONDS);

        // create a job service cancel event and send it to the jobs service.
        CancelJobRequestEvent cancelJobEvent = CancelJobRequestEvent.builder()
                .source(URI.create(TEST_SOURCE))
                .jobId(JOB_ID_2)
                .build();
        String cancelJobEventJson = serializer.serialize(cancelJobEvent);
        jobEventsEmitter.send(cancelJobEventJson);

        // wait until the job was canceled or fail if CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS elapsed.
        waitUntilResult(() -> getJob(jobUrl),
                response -> expectedJobDontExists(JOB_ID_2, response),
                CALLBACK_EXECUTIONS_QUERY_TIMOUT_IN_SECONDS,
                CALLBACK_EXECUTIONS_QUERY_POLL_INTERVAL_IN_MILLISECONDS);
    }

    /**
     * Get the url of the callback resource.
     * 
     * @see BaseCallbackResource
     */
    private String getCallbackResourceURL() {
        return "http://localhost:" + port;
    }

    /**
     * Helper method to verify that the CallbackResource used to fire the event callbacks is properly running. Useful
     * in cases were the test gets broken for any reason.
     */
    private void assertCallbackResource() {
        String checkCallbackNode = "chekCallbackNode_" + CHECK_CALLBACK_NODE_INSTANCE_ID.getAndIncrement();
        String callbackDirectCheckURL = buildCallbackEndpoint(getCallbackResourceURL(), PROCESS_ID, PROCESS_INSTANCE_ID, checkCallbackNode);
        // ensure that the callback resource used for the tests is working.
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{}")
                .when()
                .post(callbackDirectCheckURL)
                .then()
                .statusCode(200)
                .extract()
                .statusCode();
        assertThat(getJobCallbackExecutions(checkCallbackNode)).isEqualTo("1");
    }

    /**
     * Get the number of executions performed in the CallbackResource for a given timer.
     */
    private String getJobCallbackExecutions(String timerId) {
        String path = String.format(CALLBACK_RESOURCE_PATH + "/executions/%s", timerId);
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .body().asString();
    }

    /**
     * Invoke the jobs service get method, exception treatment is performed by the caller.
     */
    private static Response getJob(String jobUrl) {
        return given()
                .contentType(ContentType.JSON)
                .response().contentType(ContentType.JSON)
                .when()
                .get(jobUrl)
                .then()
                .extract().response();
    }

    /**
     * Create the callback endpoint to CallbackResource.
     */
    private static String buildCallbackEndpoint(String serviceURL, String processId, String processInstanceId, String nodeId) {
        return URIBuilder.toURI(serviceURL
                + CALLBACK_RESOURCE_PATH + "/"
                + processId
                + "/instances/"
                + processInstanceId
                + "/timers/"
                + nodeId)
                .toString();
    }

    private static <T> void waitUntilResult(Supplier<T> resultProducer, Predicate<T> condition, int pollIntervalInMillis, int timoutInSeconds) {
        await()
                .pollInterval(pollIntervalInMillis, MILLISECONDS)
                .atMost(timoutInSeconds, SECONDS)
                .until(() -> {
                    T result = resultProducer.get();
                    return condition.test(result);
                });
    }

    private static boolean expectedJobExists(String jobId, Response response) {
        if (response.statusCode() == 404) {
            return false;
        }
        if ("".equals(response.asString())) {
            return false;
        }
        return jobId.equals(response.jsonPath().getString("id"));
    }

    private static boolean expectedJobDontExists(String jobId, Response response) {
        String expectedMessage = "Job not found id " + jobId;
        return response.statusCode() == 404 && expectedMessage.equals(response.jsonPath().getString("message"));
    }
}

/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.workflows;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.kogito.test.quarkus.kafka.KafkaTestClient;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class WorkflowTestUtils {

    public static final int TIME_OUT_SECONDS = 50;
    public static final String KOGITO_PROCESSINSTANCES_EVENTS = "kogito-processinstances-events";

    private WorkflowTestUtils() {
    }

    /**
     * Start a new process instance by sending a post request to the processUrl and passing the processInput as
     * the post body. Assertions are made to ensure the process was properly created.
     *
     * @param processUrl the url to send the post request.
     * @param processInput a String containing a json value that will be the process parameter.
     * @return the id of the created process.
     */
    public static String newProcessInstanceAndGetId(String processUrl, String processInput) {
        return newProcessInstance(processUrl, processInput).get("id");
    }

    /**
     * Start a new process instance by sending a post request to the processUrl and passing the processInput as
     * the post body. Assertions are made to ensure the process was properly created.
     *
     * @param processUrl the url to send the post request.
     * @param processInput a String containing a json value that will be the process parameter.
     * @return a JsonPath with the result.
     */
    public static JsonPath newProcessInstance(String processUrl, String processInput) {
        JsonPath result = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(processInput)
                .post(processUrl)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath();
        String processInstanceId = result.get("id");
        assertThat(processInstanceId).isNotBlank();
        return result;
    }

    /**
     * Start a new process instance by sending a post request to the processUrl and passing the processInput as
     * the post body. Assertions are made to ensure the process was properly created.
     *
     * @param processUrl the url to send the post request.
     * @param processInput a String containing a json value that will be the process parameter.
     * @param headers a Map containing the http heades to send along with the invocation.
     * @return a JsonPath with the result.
     */
    public static JsonPath newProcessInstance(String processUrl, String processInput, Map<String, ?> headers) {
        JsonPath result = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .headers(headers)
                .body(processInput)
                .post(processUrl)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath();
        String processInstanceId = result.get("id");
        assertThat(processInstanceId).isNotBlank();
        return result;
    }

    /**
     * Asserts that a process instance exists by executing the get getProcessByIdQuery.
     *
     * @param getProcessByIdQuery a query in the form /my-process/{id}.
     * @param processInstanceId the id of the process instance to find
     */
    public static void assertProcessInstanceExists(String getProcessByIdQuery, String processInstanceId) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(getProcessByIdQuery, processInstanceId)
                .then()
                .statusCode(200);
    }

    public static String getProcessInstance(String processUrl) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(processUrl)
                .then()
                .statusCode(200)
                .extract()
                .path("[0].id");
    }

    /**
     * Asserts that a process instance not exists by executing the getProcessByIdQuery.
     * 
     * @param getProcessByIdQuery a query in the form /my-process/{id}.
     * @param processInstanceId the id of the process instance to find
     */
    public static void assertProcessInstanceNotExists(String getProcessByIdQuery, String processInstanceId) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(getProcessByIdQuery, processInstanceId)
                .then()
                .statusCode(404);
    }

    /**
     * Asserts that a process instance has finished by executing the getProcessById query during the specified interval
     * of time. If the http status code 404 is not returned during that time the timeout condition is raised and the
     * assertion fails.
     *
     * @param getProcessByIdQuery a query in the form /my-process/{id}.
     * @param processInstanceId the id of the process to find.
     * @param atLeastTimeoutInSeconds minimum time to wait.
     * @param atMostTimeoutInSeconds maximum time to wait. (the timeout condition is raised when surpassed)
     */
    public static void assertProcessInstanceHasFinished(String getProcessByIdQuery,
            String processInstanceId,
            long atLeastTimeoutInSeconds,
            long atMostTimeoutInSeconds) {
        await()
                .atLeast(atLeastTimeoutInSeconds, SECONDS)
                .atMost(atMostTimeoutInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .get(getProcessByIdQuery, processInstanceId)
                        .then()
                        .statusCode(404));
    }

    public static JsonPath waitForKogitoProcessInstanceEvent(KafkaTestClient kafkaClient, boolean shutdownAfterConsume) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<String> cloudEvent = new AtomicReference<>();

        kafkaClient.consume(KOGITO_PROCESSINSTANCES_EVENTS, rawCloudEvent -> {
            cloudEvent.set(rawCloudEvent);
            countDownLatch.countDown();
        });
        // give some time to consume the event
        assertThat(countDownLatch.await(TIME_OUT_SECONDS, TimeUnit.SECONDS)).isTrue();
        if (shutdownAfterConsume) {
            kafkaClient.shutdown();
        }
        return new JsonPath(cloudEvent.get());
    }

}

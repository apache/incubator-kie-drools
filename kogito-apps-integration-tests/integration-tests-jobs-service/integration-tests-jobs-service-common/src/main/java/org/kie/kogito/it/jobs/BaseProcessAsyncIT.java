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
package org.kie.kogito.it.jobs;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

public abstract class BaseProcessAsyncIT implements JobServiceHealthAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProcessAsyncIT.class);
    public static final String ASYNC = "async";
    public static final Duration TIMEOUT = Duration.ofMinutes(5);

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testAsync() {
        ValidatableResponse created = create(new RequestPayload("Tiago"), ASYNC);
        final String id = created.extract().path("id");
        //null because tasks are async (not executed in the same thread as the create/start request)
        created.body("hello", nullValue());
        created.body("bye", nullValue());

        //check started and executed the hello async task
        await().atMost(TIMEOUT)
                .untilAsserted(() -> {
                    ValidatableResponse response = getById(ASYNC, id);
                    response.body("hello", equalTo("Hello Tiago"));
                    response.body("bye", nullValue());
                });

        //signal to continue to bye async task
        ValidatableResponse complete = signal(ASYNC, id, "bye");
        complete.body("bye", nullValue());

        await().atMost(TIMEOUT)
                .untilAsserted(() -> {
                    LOGGER.info("Checking bye equalTo(\"Bye Tiago\") assertion");
                    getById(ASYNC, id).body("bye", equalTo("Bye Tiago"));
                });

        signal(ASYNC, id, "complete");
        //check completed and removed
        await().atMost(TIMEOUT)
                .untilAsserted(() -> getWithStatusCode(ASYNC, id, 404));
    }

    private ValidatableResponse getWithStatusCode(String path, String id, int code) {
        return given()
                .accept(ContentType.JSON)
                .when()
                .get("/" + path + "/{id}", id)
                .then()
                .statusCode(code);
    }

    private ValidatableResponse create(RequestPayload request, String path) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/" + path)
                .then()
                .statusCode(201);
    }

    private ValidatableResponse getById(String path, String id) {
        return getWithStatusCode(path, id, 200);
    }

    private ValidatableResponse signal(String path, String id, String signal) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/" + path + "/" + id + "/" + signal)
                .then()
                .statusCode(200);
    }

    /**
     * Simple bean class to send as body on the requests
     */
    private class RequestPayload {

        private String name;

        public RequestPayload(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

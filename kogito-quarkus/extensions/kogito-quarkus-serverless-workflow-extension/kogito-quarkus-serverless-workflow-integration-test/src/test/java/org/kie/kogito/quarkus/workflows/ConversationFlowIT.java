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
package org.kie.kogito.quarkus.workflows;

import java.util.Collections;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@QuarkusIntegrationTest
class ConversationFlowIT {

    private static WireMockServer subtractionService;
    private static WireMockServer multiplicationService;

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeAll
    public static void startSink() {
        multiplicationService =
                startServer(9090,
                        "{  \"product\": 37.808 }", p -> p.withHeader("pepe", new EqualToPattern("pepa")));
        subtractionService =
                startServer(9191,
                        "{ \"difference\": 68.0 }", p -> p);
    }

    private static WireMockServer startServer(final int port, final String response, UnaryOperator<MappingBuilder> function) {
        final WireMockServer server = new WireMockServer(port);
        server.start();
        server.stubFor(function.apply(post(urlEqualTo("/")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }

    @AfterAll
    public static void stopSink() {
        if (subtractionService != null) {
            subtractionService.stop();
        }
        if (multiplicationService != null) {
            multiplicationService.stop();
        }
    }

    @Test
    void sanityVerification() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("fahrenheit", "100")))
                .post("/fahrenheit_to_celsius")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("workflowdata.fahrenheit", is("100"))
                .body("workflowdata.celsius", is(37.808f)); //values from mock server

        await()
                .atMost(10, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> subtractionService.verify(1, postRequestedFor(urlEqualTo("/"))));

        await()
                .atMost(10, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> multiplicationService.verify(1, postRequestedFor(urlEqualTo("/"))));
    }
}

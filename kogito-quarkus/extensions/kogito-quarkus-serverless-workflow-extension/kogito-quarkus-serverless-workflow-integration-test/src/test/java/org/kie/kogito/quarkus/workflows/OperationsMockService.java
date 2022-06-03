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

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class OperationsMockService implements QuarkusTestResourceLifecycleManager {

    private static WireMockServer subtractionService;
    private static WireMockServer multiplicationService;

    public static final String SUBTRACTION_SERVICE_MOCK_URL = "subtraction-service-mock.url";
    public static final String MULTIPLICATION_SERVICE_MOCK_URL = "multiplication-service-mock.url";

    @Override
    public Map<String, String> start() {
        multiplicationService =
                startServer("{  \"product\": 37.808 }", p -> p.withHeader("pepe", new EqualToPattern("pepa")));
        subtractionService =
                startServer("{ \"difference\": 68.0 }", p -> p);

        Map<String, String> result = new HashMap<>();
        result.put(MULTIPLICATION_SERVICE_MOCK_URL, multiplicationService.baseUrl());
        result.put(SUBTRACTION_SERVICE_MOCK_URL, subtractionService.baseUrl());
        return result;
    }

    @Override
    public void stop() {
        if (multiplicationService != null) {
            multiplicationService.stop();
        }
        if (subtractionService != null) {
            subtractionService.stop();
        }
    }

    private static WireMockServer startServer(final String response, UnaryOperator<MappingBuilder> function) {
        final WireMockServer server = new WireMockServer(options().dynamicPort());
        server.start();
        server.stubFor(function.apply(post(urlEqualTo("/")))
                .withPort(server.port())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }
}

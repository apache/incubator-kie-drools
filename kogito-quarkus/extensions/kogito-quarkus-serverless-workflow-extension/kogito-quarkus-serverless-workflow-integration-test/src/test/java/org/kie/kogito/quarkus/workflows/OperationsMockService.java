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
import java.util.Map;
import java.util.function.UnaryOperator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class OperationsMockService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer subtractionService;
    private WireMockServer multiplicationService;

    @Override
    public Map<String, String> start() {
        multiplicationService =
                this.startServer(9090,
                        "{\"multiplication\": { \"leftElement\": \"68.0\", \"rightElement\": \"0.5556\", \"product\": \"37.808\" }}", p -> p.withHeader("pepe", new EqualToPattern("pepa")));
        subtractionService =
                this.startServer(9191,
                        "{\"subtraction\": { \"leftElement\": \"100\", \"rightElement\": \"32\", \"difference\": \"68.0\" }}", p -> p);
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (subtractionService != null) {
            subtractionService.stop();
        }
        if (multiplicationService != null) {
            multiplicationService.stop();
        }
    }

    private WireMockServer startServer(final int port, final String response, UnaryOperator<MappingBuilder> function) {
        final WireMockServer server = new WireMockServer(port);
        server.start();
        server.stubFor(function.apply(post(urlEqualTo("/")))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }
}

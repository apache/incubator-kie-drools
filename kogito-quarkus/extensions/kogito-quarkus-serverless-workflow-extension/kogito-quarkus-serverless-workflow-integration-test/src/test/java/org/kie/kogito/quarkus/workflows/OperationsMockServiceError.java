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
package org.kie.kogito.quarkus.workflows;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class OperationsMockServiceError implements QuarkusTestResourceLifecycleManager {

    private WireMockServer errorService;

    public static final String ERROR_SERVICE_MOCK_URL = "error-service-mock.url";

    @Override
    public Map<String, String> start() {
        errorService =
                startServer("{ \"difference\": 68.0 }", p -> p);

        Map<String, String> result = new HashMap<>();
        result.put(ERROR_SERVICE_MOCK_URL, errorService.baseUrl());
        return result;
    }

    @Override
    public void stop() {
        if (errorService != null) {
            errorService.shutdown();
        }
    }

    private static WireMockServer startServer(final String response, UnaryOperator<MappingBuilder> function) {
        final WireMockServer server = new WireMockServer(options().dynamicPort());
        server.start();
        server.stubFor(function.apply(post(urlEqualTo("/")))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }
}

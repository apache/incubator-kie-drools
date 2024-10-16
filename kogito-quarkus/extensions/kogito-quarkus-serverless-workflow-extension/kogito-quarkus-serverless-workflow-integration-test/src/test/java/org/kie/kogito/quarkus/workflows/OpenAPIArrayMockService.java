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

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class OpenAPIArrayMockService implements QuarkusTestResourceLifecycleManager {

    private static WireMockServer arrayService;
    private static WireMockServer petService;

    @Override
    public Map<String, String> start() {
        arrayService = startServer("[1,2,3,4]", post(urlEqualTo("/testArray")).withRequestBody(equalToJson("[1,2,3,4]")));
        petService = startServer("{\"name\":\"Maya\", \"photoUrls\":[]}", get(urlEqualTo("/pet/4")));
        return Map.of("array-service-mock.url", arrayService.baseUrl(), "petstore-service-mock.url", petService.baseUrl());
    }

    @Override
    public void stop() {
        if (arrayService != null) {
            arrayService.stop();
        }
        if (petService != null) {
            petService.stop();
        }
    }

    private static WireMockServer startServer(final String response, MappingBuilder mappingBuilder) {
        final WireMockServer server = new WireMockServer(options().dynamicPort());
        server.start();
        server.stubFor(mappingBuilder
                .withPort(server.port())
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        return server;
    }
}

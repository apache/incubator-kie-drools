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

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ExternalServiceMock implements QuarkusTestResourceLifecycleManager {

    public static final String SUCCESSFUL_QUERY = "SUCCESSFUL_QUERY";
    public static final String GENERATE_ERROR_QUERY = "GENERATE_ERROR_QUERY";

    public static final String EXTERNAL_SERVICE_MOCK_URL = "external-service-mock.url";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        // mock a successful invocation
        stubFor(post("/external-service/sendRequest")
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .withRequestBody(equalToJson("{\"query\" : \"" + SUCCESSFUL_QUERY + "\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // mock a failing invocation
        stubFor(post("/external-service/sendRequest")
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .withRequestBody(equalToJson("{\"query\" : \"" + GENERATE_ERROR_QUERY + "\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        return Collections.singletonMap(EXTERNAL_SERVICE_MOCK_URL, wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}

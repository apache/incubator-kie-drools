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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

/**
 * Mock OTLP collector using WireMock to simulate real OpenTelemetry Collector HTTP endpoints.
 * Supports OTLP/HTTP protocol for traces, metrics, and logs.
 */
public class OtlpMockCollector {

    private final WireMockServer wireMockServer;
    private final List<String> receivedRequests = new CopyOnWriteArrayList<>();

    public OtlpMockCollector() {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .dynamicPort()
                .dynamicHttpsPort());
    }

    public void start() {
        wireMockServer.start();
        setupOtlpEndpoints();
    }

    public void stop() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }

    public String getTracesEndpoint() {
        return getBaseUrl() + "/v1/traces";
    }

    public String getMetricsEndpoint() {
        return getBaseUrl() + "/v1/metrics";
    }

    public String getLogsEndpoint() {
        return getBaseUrl() + "/v1/logs";
    }

    private void setupOtlpEndpoints() {
        // Set up OTLP traces endpoint
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/v1/traces"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"partialSuccess\":{}}"))
                .atPriority(1));

        // Set up OTLP metrics endpoint
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/v1/metrics"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"partialSuccess\":{}}"))
                .atPriority(1));

        // Set up OTLP logs endpoint
        wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/v1/logs"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"partialSuccess\":{}}"))
                .atPriority(1));

        // Health check endpoint
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/health"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withBody("OK"))
                .atPriority(1));
    }

    public List<ServeEvent> getReceivedTracesRequests() {
        return wireMockServer.getAllServeEvents().stream()
                .filter(event -> event.getRequest().getUrl().equals("/v1/traces"))
                .toList();
    }

    public List<ServeEvent> getReceivedMetricsRequests() {
        return wireMockServer.getAllServeEvents().stream()
                .filter(event -> event.getRequest().getUrl().equals("/v1/metrics"))
                .toList();
    }

    public List<ServeEvent> getReceivedLogsRequests() {
        return wireMockServer.getAllServeEvents().stream()
                .filter(event -> event.getRequest().getUrl().equals("/v1/logs"))
                .toList();
    }

    public int getTracesRequestCount() {
        return getReceivedTracesRequests().size();
    }

    public int getMetricsRequestCount() {
        return getReceivedMetricsRequests().size();
    }

    public int getLogsRequestCount() {
        return getReceivedLogsRequests().size();
    }

    public void clearRequests() {
        wireMockServer.resetAll();
        setupOtlpEndpoints();
    }

    public boolean isRunning() {
        return wireMockServer.isRunning();
    }

    public int getPort() {
        return wireMockServer.port();
    }
}

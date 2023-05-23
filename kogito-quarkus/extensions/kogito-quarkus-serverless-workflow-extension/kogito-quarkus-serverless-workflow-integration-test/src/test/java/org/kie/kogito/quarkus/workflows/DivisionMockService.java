/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class DivisionMockService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    private static final String DIVISION_SERVICE_MOCK_PORT = "kogito.sw.functions.division.port";

    static final int[] dividends = { 2, 4, 6, 8, 10 };
    static final int divisor = 2;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(options().dynamicPort());
        for (int dividend : dividends) {
            stubFor(dividend, divisor);
        }
        server.start();
        return Map.of(DIVISION_SERVICE_MOCK_PORT, Integer.toString(server.port()));
    }

    private void stubFor(int dividend, int divisor) {
        server.stubFor(get(urlPathEqualTo("/division"))
                .withQueryParam("dividend", new EqualToPattern(Integer.toString(dividend)))
                .withQueryParam("divisor", new EqualToPattern(Integer.toString(divisor)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"result\":" + dividend / divisor + "}")));
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}

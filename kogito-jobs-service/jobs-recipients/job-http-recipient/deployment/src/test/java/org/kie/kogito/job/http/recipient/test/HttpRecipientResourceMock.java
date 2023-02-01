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
package org.kie.kogito.job.http.recipient.test;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

public class HttpRecipientResourceMock implements QuarkusTestResourceLifecycleManager {

    public static final String MOCK_SERVICE_URL = "mock.service.url";
    WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        stubFor(WireMock.post(WireMock.urlMatching(".*")).willReturn(WireMock.ok("POST")));
        stubFor(WireMock.get(WireMock.urlMatching(".*")).willReturn(WireMock.ok("GET")));
        stubFor(WireMock.put(WireMock.urlMatching(".*")).willReturn(WireMock.ok("PUT")));
        stubFor(WireMock.delete(WireMock.urlMatching(".*")).willReturn(WireMock.ok("DELETE")));
        stubFor(WireMock.patch(WireMock.urlMatching(".*")).willReturn(WireMock.ok("PATCH")));
        return Map.of(MOCK_SERVICE_URL, "http://localhost:" + wireMockServer.port());
    }

    @Override
    public synchronized void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

    @Override
    public void inject(TestInjector testInjector) {
        testInjector.injectIntoFields(wireMockServer, new TestInjector.MatchesType(WireMockServer.class));
    }
}

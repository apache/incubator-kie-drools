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

package org.kie.kogito.it.jobs;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

/**
 * Mock the SinkBinding that links the kogito project with the knative Broker.
 */
public class SinkMock implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SinkMock.class);
    private WireMockServer wireMockServer;

    public interface SinkMockAware {
        void setWireMockServer(WireMockServer sink);
    }

    @Override
    public Map<String, String> start() {
        LOGGER.info("Start SinkMock test resource");
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockServer.stubFor(post("/").willReturn(aResponse().withBody("{}").withStatus(200)));
        LOGGER.info("SinkMock test resource started");
        return Collections.singletonMap("kogito.sink-mock.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        LOGGER.info("Stop SinkMock test resource");
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        LOGGER.info("Stop SinkMock test resource stopped");
    }

    @Override
    public void inject(Object testInstance) {
        if (testInstance instanceof SinkMockAware) {
            ((SinkMockAware) testInstance).setWireMockServer(wireMockServer);
        }
    }
}

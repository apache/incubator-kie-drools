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
package org.kie.kogito.it.jobs;

import java.util.Collections;
import java.util.Map;

import org.kie.kogito.test.utils.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import jakarta.ws.rs.core.Response;

import static com.github.tomakehurst.wiremock.client.CountMatchingStrategy.GREATER_THAN_OR_EQUAL;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.request;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * Mock an external JobRecipient to verify the proper execution of jobs service api over http.
 */
public class JobRecipientMock implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRecipientMock.class);
    private WireMockServer wireMockServer;
    public static final String JOB_RECIPIENT_MOCK_URL_PROPERTY = "kogito.job-recipient-mock.url";
    private static final String JOB_LIMIT_REGEX = "\\?limit\\=(\\d+)";
    public static final String JOB_RECIPIENT_MOCK = "job-recipient-mock";
    public static final String JOB_RECIPIENT_MOCK_REGEX = "\\/" + JOB_RECIPIENT_MOCK + JOB_LIMIT_REGEX + "$";
    public static final String FAILING_JOB_RECIPIENT_MOCK = "failing-job-recipient-mock";
    public static final String FAILING_JOB_RECIPIENT_MOCK_REGEX = "\\/" + FAILING_JOB_RECIPIENT_MOCK + JOB_LIMIT_REGEX + "$";

    public interface JobRecipientMockAware {
        void setWireMockServer(WireMockServer jobRecipient);
    }

    @Override
    public Map<String, String> start() {
        LOGGER.info("Start JobRecipientMock test resource");
        int httpPort = SocketUtils.findAvailablePort();
        Testcontainers.exposeHostPorts(httpPort);
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(httpPort));
        wireMockServer.start();

        wireMockServer.stubFor(request("POST", new UrlPattern(new RegexPattern(JOB_RECIPIENT_MOCK_REGEX), true)));

        wireMockServer.stubFor(request("POST", new UrlPattern(new RegexPattern(FAILING_JOB_RECIPIENT_MOCK_REGEX), true))
                .willReturn(ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .withStatusMessage(String.format("The %s service is designed to always fail.", FAILING_JOB_RECIPIENT_MOCK))));

        LOGGER.info("JobRecipientMock test resource started");
        return Collections.singletonMap(JOB_RECIPIENT_MOCK_URL_PROPERTY, String.format("http://host.testcontainers.internal:%s", wireMockServer.port()));
    }

    @Override
    public void stop() {
        LOGGER.info("Stop JobRecipientMock test resource");
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        LOGGER.info("Stop JobRecipientMock test resource stopped");
    }

    @Override
    public void inject(Object testInstance) {
        if (testInstance instanceof JobRecipientMockAware) {
            ((JobRecipientMockAware) testInstance).setWireMockServer(wireMockServer);
        }
    }

    public static void verifyJobWasExecuted(WireMockServer jobRecipient, int waitAtMostInSeconds, String jobId, int limit) {
        await()
                .atMost(waitAtMostInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> jobRecipient.verify(1,
                        postRequestedFor(urlEqualTo("/" + JOB_RECIPIENT_MOCK + "?limit=" + limit))
                                .withHeader("jobId", equalTo(jobId))));
    }

    public static void verifyFailingJobWasExecutedAtLeastCount(WireMockServer jobRecipient, int waitAtMostInSeconds, int atLeastCount, String jobId, int limit) {
        await()
                .atMost(waitAtMostInSeconds, SECONDS)
                .with().pollInterval(1, SECONDS)
                .untilAsserted(() -> jobRecipient.verify(new CountMatchingStrategy(GREATER_THAN_OR_EQUAL, atLeastCount),
                        postRequestedFor(urlEqualTo("/" + FAILING_JOB_RECIPIENT_MOCK + "?limit=" + limit))
                                .withHeader("jobId", equalTo(jobId))));
    }
}

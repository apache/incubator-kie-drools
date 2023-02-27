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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import io.smallrye.mutiny.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServerlessWorkflowCustomFunction.REQUEST_TIMEOUT_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceDiscoveryTestUtil.createServiceIfNotExists;

@QuarkusTest
@WithKubernetesTestServer
class KnativeServerlessWorkflowCustomFunctionTest {

    private static String remoteServiceUrl;

    @KubernetesTestServer
    KubernetesServer mockServer;

    @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME)
    Long requestTimeout;

    @Inject
    KnativeServerlessWorkflowCustomFunction knativeServerlessWorkflowCustomFunction;

    private static KnativeClient knativeClient;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        createWiremockServer();
    }

    @BeforeEach
    void beforeEach() {
        createServiceIfNotExists(mockServer, remoteServiceUrl, "knative/quarkus-greeting.yaml", "test", "serverless-workflow-greeting-quarkus")
                .ifPresent(newKnativeClient -> knativeClient = newKnativeClient);
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        knativeClient.close();
    }

    private static void createWiremockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        remoteServiceUrl = wireMockServer.baseUrl();
    }

    private void mockExecuteTimeoutEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/timeout"))
                .willReturn(aResponse()
                        .withFixedDelay(requestTimeout.intValue() + 500)
                        .withStatus(200)));
    }

    private void mockExecute404Endpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/non_existing_path"))
                .willReturn(aResponse()
                        .withStatus(404)));
    }

    private void mockExecuteWithQueryParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/hello"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "Hello Kogito"))));
    }

    private void mockExecuteWithParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/"))
                .withRequestBody(equalToJson(JsonNodeFactory.instance.objectNode()
                        .put("org", "Acme")
                        .put("project", "Kogito")
                        .toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "Kogito is awesome!"))));
    }

    private void mockExecuteWithEmptyParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("org", "Acme")
                                .put("project", "Kogito"))));
    }

    @Test
    void executeWithEmptyParameters() {
        mockExecuteWithEmptyParametersEndpoint();

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute("serverless-workflow-greeting-quarkus", "/", Map.of());

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("org", "Acme")
                .put("project", "Kogito");

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void executeWithParameters() {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> parameters = Map.of(
                "org", "Acme",
                "project", "Kogito");

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute("serverless-workflow-greeting-quarkus", "/", parameters);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Kogito is awesome!");

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void executeWithQueryParameters() {
        mockExecuteWithQueryParametersEndpoint();

        Map<String, Object> parameters = Map.of();

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute("serverless-workflow-greeting-quarkus", "/hello", parameters);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Hello Kogito");

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void execute404() {
        mockExecute404Endpoint();

        Map<String, Object> parameters = Map.of();
        assertThatCode(() -> knativeServerlessWorkflowCustomFunction.execute("serverless-workflow-greeting-quarkus", "/non_existing_path", parameters))
                .isInstanceOf(WorkItemExecutionException.class)
                .extracting("errorCode")
                .isEqualTo("404");
    }

    @Test
    void executeTimeout() {
        mockExecuteTimeoutEndpoint();

        Map<String, Object> payload = Map.of();

        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute("serverless-workflow-greeting-quarkus", "/timeout", payload));
    }
}
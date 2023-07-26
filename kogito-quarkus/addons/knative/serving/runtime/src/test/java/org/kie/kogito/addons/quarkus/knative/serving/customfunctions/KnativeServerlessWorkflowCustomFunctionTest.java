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

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.InvalidCloudEventException;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.kie.kogito.serverless.workflow.SWFConstants;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.v1.CloudEventV1;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import io.smallrye.mutiny.TimeoutException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.kie.kogito.addons.quarkus.k8s.test.utils.KnativeResourceDiscoveryTestUtil.createServiceIfNotExists;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceRequestClient.APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceRequestClient.REQUEST_TIMEOUT_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.PlainJsonKnativeServiceRequestClient.CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE;

@QuarkusTest
@WithKubernetesTestServer
class KnativeServerlessWorkflowCustomFunctionTest {

    private static final String UNUSED = "unused";

    private static final String NAMESPACE = "test";

    private static final String SERVICENAME = "serverless-workflow-greeting-quarkus";

    private static final String CLOUD_EVENT_PATH = "/cloud-event";

    private static String remoteServiceUrl;

    @KubernetesTestServer
    KubernetesServer mockServer;

    @ConfigProperty(name = REQUEST_TIMEOUT_PROPERTY_NAME)
    Long requestTimeout;

    @Inject
    KnativeServerlessWorkflowCustomFunction knativeServerlessWorkflowCustomFunction;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        createWiremockServer();
    }

    @BeforeEach
    void beforeEach() {
        createServiceIfNotExists(mockServer, "knative/quarkus-greeting.yaml", NAMESPACE, SERVICENAME, remoteServiceUrl);
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
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
                                .put("message", "Kogito is awesome!")
                                .set("object", JsonNodeFactory.instance.objectNode()
                                        .put("long", 42L)
                                        .put("String", "Knowledge is everything")))));
    }

    private void mockExecuteWithArray() {
        wireMockServer.stubFor(post(urlEqualTo("/"))
                .withRequestBody(equalToJson(JsonNodeFactory.instance.arrayNode()
                        .add("Javierito")
                        .add("Pepito")
                        .toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.arrayNode().add(23).add(24))));
    }

    private void mockExecuteCloudEventWithParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo(CLOUD_EVENT_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "CloudEvents are awesome!")
                                .set("object", JsonNodeFactory.instance.objectNode()
                                        .put("long", 42L)
                                        .put("String", "Knowledge is everything")))));
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

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute("unused", SERVICENAME, Map.of());

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

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(UNUSED, SERVICENAME, parameters);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Kogito is awesome!")
                .set("object", JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "Knowledge is everything"));

        assertThat(output).hasToString(expected.toString());
    }

    @Test
    void executeWithArray() {
        mockExecuteWithArray();
        assertThat(knativeServerlessWorkflowCustomFunction.execute(UNUSED, SERVICENAME, Map.of(
                SWFConstants.CONTENT_DATA, List.of("Javierito", "Pepito")))).hasToString(JsonNodeFactory.instance.arrayNode().add(23).add(24).toString());
    }

    @Test
    void executeWithCloudEventWithIdAsPlainJson() {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "id", 42, // KnativeWorkItemHandler receivers this attribute as an Integer
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, SERVICENAME, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @Test
    void executeWithCloudEventWithoutIdAsPlainJson() {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, SERVICENAME, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @Test
    void executeWithCloudEventThatHasOnlyIdMissingAsPlainJson() {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, SERVICENAME, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @Test
    void executeCloudEvent() {
        mockExecuteCloudEventWithParametersEndpoint();

        String source = "https://localhost:8080";

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "id", 42, // KnativeWorkItemHandler receivers this attribute as an Integer
                "source", source,
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String processInstanceId = Instant.now().toString();

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(
                processInstanceId, SERVICENAME + "?asCloudEvent=true&path=" + CLOUD_EVENT_PATH, cloudEvent);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "CloudEvents are awesome!")
                .set("object", JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "Knowledge is everything"));

        assertThat(output).hasToString(expected.toString());

        wireMockServer.verify(postRequestedFor(urlEqualTo(CLOUD_EVENT_PATH))
                .withRequestBody(matchingJsonPath("$.id", equalTo("42")))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void executeCloudEventWithMissingIdShouldNotThrowException() {
        mockExecuteCloudEventWithParametersEndpoint();

        String source = "https://localhost:8080";

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "source", source,
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String operation = SERVICENAME + "?asCloudEvent=true&path=" + CLOUD_EVENT_PATH;

        assertThatNoException()
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, operation, cloudEvent));
    }

    @Test
    void executeWithInvalidCloudEvent() {
        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, SpecVersion.V1.toString(),
                "source", "https://localhost:8080",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        String operation = SERVICENAME + "?asCloudEvent=true&path=" + CLOUD_EVENT_PATH;

        assertThatExceptionOfType(InvalidCloudEventException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, operation, cloudEvent));
    }

    @Test
    void executeWithQueryParameters() {
        mockExecuteWithQueryParametersEndpoint();

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(UNUSED, SERVICENAME + "?path=/hello", Map.of());

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Hello Kogito");

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void execute404() {
        mockExecute404Endpoint();

        Map<String, Object> parameters = Map.of();

        String operation = SERVICENAME + "?path=/non_existing_path";

        assertThatCode(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, operation, parameters))
                .isInstanceOf(WorkItemExecutionException.class)
                .extracting("errorCode")
                .isEqualTo("404");
    }

    @Test
    void executeTimeout() {
        mockExecuteTimeoutEndpoint();

        Map<String, Object> payload = Map.of();

        String operation = SERVICENAME + "?path=/timeout";

        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, operation, payload));
    }
}

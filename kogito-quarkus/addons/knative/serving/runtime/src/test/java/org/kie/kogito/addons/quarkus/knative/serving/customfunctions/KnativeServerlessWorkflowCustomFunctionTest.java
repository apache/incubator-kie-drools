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
import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

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
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServerlessWorkflowCustomFunction.CLOUD_EVENT_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServerlessWorkflowCustomFunction.PATH_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceRequestClient.APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceRequestClient.REQUEST_TIMEOUT_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler.OPERATION_PROPERTY_NAME;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.PlainJsonKnativeServiceRequestClient.CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE;

@QuarkusTest
@WithKubernetesTestServer
class KnativeServerlessWorkflowCustomFunctionTest {

    private static final String UNUSED = "unused";

    private static final String SERVICE_NAME = "serverless-workflow-greeting-quarkus";

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
        createServiceIfNotExists(mockServer, remoteServiceUrl, "knative/quarkus-greeting.yaml", "test", SERVICE_NAME);
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

    private void mockExecuteCloudEventWithParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/cloud-event"))
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

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithEmptyParameters(String service) {
        mockExecuteWithEmptyParametersEndpoint();

        Map<String, Object> metadata = createMetadata(false, service, "/");

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute("unused", metadata, Map.of());

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("org", "Acme")
                .put("project", "Kogito");

        assertThat(output).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithParameters(String service) {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> parameters = Map.of(
                "org", "Acme",
                "project", "Kogito");

        Map<String, Object> metadata = createMetadata(false, service, "/");

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, parameters);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Kogito is awesome!")
                .set("object", JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "Knowledge is everything"));

        assertThat(output).hasToString(expected.toString());
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithCloudEventWithIdAsPlainJson(String service) {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "id", 42, // KnativeWorkItemHandler receivers this attribute as an Integer
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        Map<String, Object> metadata = createMetadata(false, service, "/");

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, metadata, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithCloudEventWithoutIdAsPlainJson(String service) {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        Map<String, Object> metadata = createMetadata(false, service, "/");

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, metadata, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithCloudEventThatHasOnlyIdMissingAsPlainJson(String service) {
        mockExecuteWithParametersEndpoint();

        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0, // KnativeWorkItemHandler receives this attribute as a double
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        Map<String, Object> metadata = createMetadata(false, service, "/");

        String processInstanceId = Instant.now().toString();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(processInstanceId, metadata, cloudEvent))
                .withMessage(CLOUDEVENT_SENT_AS_PLAIN_JSON_ERROR_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeCloudEvent(String service) {
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

        Map<String, Object> metadata = createMetadata(true, service, "/cloud-event");

        String processInstanceId = Instant.now().toString();

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(processInstanceId, metadata, cloudEvent);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "CloudEvents are awesome!")
                .set("object", JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "Knowledge is everything"));

        assertThat(output).hasToString(expected.toString());

        wireMockServer.verify(postRequestedFor(urlEqualTo("/cloud-event"))
                .withRequestBody(matchingJsonPath("$.id", equalTo(source + "_" + processInstanceId)))
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

        Map<String, Object> metadata = createMetadata(true, SERVICE_NAME, "/cloud-event");

        assertThatNoException()
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, cloudEvent));
    }

    private static Map<String, Object> createMetadata(boolean isCloudEvent, String knativeServiceName, String path) {
        return Map.of(
                CLOUD_EVENT_PROPERTY_NAME, isCloudEvent,
                OPERATION_PROPERTY_NAME, knativeServiceName,
                PATH_PROPERTY_NAME, path);
    }

    @Test
    void executeWithInvalidCloudEvent() {
        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, SpecVersion.V1.toString(),
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        Map<String, Object> metadata = createMetadata(true, SERVICE_NAME, "/cloud-event");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, cloudEvent))
                .withMessage("Invalid CloudEvent. The following mandatory attributes are missing: source");
    }

    @ParameterizedTest
    @MethodSource("possibleUriFormats")
    void executeWithQueryParameters(String service) {
        mockExecuteWithQueryParametersEndpoint();

        Map<String, Object> parameters = Map.of();

        Map<String, Object> metadata = createMetadata(false, service, "/hello");

        JsonNode output = knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, parameters);

        JsonNode expected = JsonNodeFactory.instance.objectNode()
                .put("message", "Hello Kogito");

        assertThat(output).isEqualTo(expected);
    }

    @Test
    void execute404() {
        mockExecute404Endpoint();

        Map<String, Object> parameters = Map.of();

        Map<String, Object> metadata = createMetadata(false, SERVICE_NAME, "/non_existing_path");

        assertThatCode(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, parameters))
                .isInstanceOf(WorkItemExecutionException.class)
                .extracting("errorCode")
                .isEqualTo("404");
    }

    @Test
    void executeTimeout() {
        mockExecuteTimeoutEndpoint();

        Map<String, Object> payload = Map.of();

        Map<String, Object> metadata = createMetadata(false, SERVICE_NAME, "/timeout");

        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> knativeServerlessWorkflowCustomFunction.execute(UNUSED, metadata, payload));
    }

    private static Stream<Arguments> possibleUriFormats() {
        return Stream.of(Arguments.of(SERVICE_NAME), Arguments.of("serving.knative.dev/v1/Service/serverless-workflow-greeting-quarkus"));
    }
}
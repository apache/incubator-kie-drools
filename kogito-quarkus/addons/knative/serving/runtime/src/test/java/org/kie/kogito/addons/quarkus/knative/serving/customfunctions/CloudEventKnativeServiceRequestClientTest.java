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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.utils.InvalidCloudEventException;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.cloudevents.SpecVersion;
import io.cloudevents.core.v1.CloudEventV1;
import io.quarkus.test.junit.QuarkusTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.cloudevents.core.v1.CloudEventV1.DATACONTENTTYPE;
import static io.cloudevents.core.v1.CloudEventV1.ID;
import static io.cloudevents.core.v1.CloudEventV1.SOURCE;
import static io.cloudevents.core.v1.CloudEventV1.SPECVERSION;
import static io.cloudevents.core.v1.CloudEventV1.TIME;
import static io.cloudevents.core.v1.CloudEventV1.TYPE;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeServiceRequestClient.APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8;
import static org.kie.kogito.event.cloudevents.utils.CloudEventUtils.DATA;

@QuarkusTest
class CloudEventKnativeServiceRequestClientTest {

    private static WireMockServer wireMockServer;

    @Inject
    CloudEventKnativeServiceRequestClient client;

    @Test
    void cloudEventWithoutIdMustHaveGeneratedId() {
        mockServer();

        String processInstanceId = "process1";
        String source = "https://localhost:8080";
        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0,
                "source", source,
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        client.sendRequest(processInstanceId, URI.create(wireMockServer.baseUrl()), "/cloud-event", cloudEvent);

        String expectedCloudEventId = CloudEventKnativeServiceRequestClient.generateCloudEventId(processInstanceId, cloudEvent);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/cloud-event"))
                .withRequestBody(matchingJsonPath("$.id", equalTo(expectedCloudEventId)))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void cloudEventWithIdMustBeSentAsIs() {
        mockServer();

        String processInstanceId = "process1";
        Map<String, Object> cloudEvent = Map.of(
                CloudEventV1.SPECVERSION, 1.0,
                "id", 42,
                "source", "https://localhost:8080",
                "type", "org.kie.kogito.test",
                "data", Map.of(
                        "org", "Acme",
                        "project", "Kogito"));

        client.sendRequest(processInstanceId, URI.create(wireMockServer.baseUrl()), "/cloud-event", cloudEvent);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/cloud-event"))
                .withRequestBody(matchingJsonPath("$.id", equalTo("42")))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void invalidCloudEventMustThrowException() {
        String processInstanceId = "process1";

        Map<String, Object> cloudEvent = new HashMap<>();
        cloudEvent.put(SPECVERSION, SpecVersion.V1.toString());
        cloudEvent.put(ID, "abc-123");
        cloudEvent.put(SOURCE, "/myapp");
        cloudEvent.put(TYPE, "com.example.someevent");
        cloudEvent.put(DATACONTENTTYPE, "application/json");
        cloudEvent.put(TIME, "invalid");
        cloudEvent.put(DATA, "{\"foo\":\"bar\"}");

        URI uri = URI.create(wireMockServer.baseUrl());

        Assertions.assertThatExceptionOfType(InvalidCloudEventException.class)
                .isThrownBy(() -> client.sendRequest(processInstanceId, uri, "/cloud-event", cloudEvent));
    }

    private static void mockServer() {
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

    private static void createWiremockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
    }

    @BeforeAll
    static void beforeAll() {
        createWiremockServer();
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
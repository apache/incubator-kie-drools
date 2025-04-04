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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions.it;

import java.net.HttpURLConnection;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.kie.kogito.addons.quarkus.k8s.test.utils.KubeTestUtils.createKnativeServiceIfNotExists;
import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler.APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8;

@QuarkusTest
@WithKubernetesTestServer
class KnativeServingAddonIT {

    public static final String AT_LEAST_ONE_NON_WHITE_CHARACTER_REGEX = ".*\\S.*";
    private static final String NAMESPACE = "default";
    private static final String SERVICENAME = "serverless-workflow-greeting-quarkus";
    private static final String CLOUD_EVENT_PATH = "/cloud-event";
    private static WireMockServer wireMockServer;

    private static String remoteServiceUrl;

    @ConfigProperty(name = "kogito.sw.functions.greet_with_timeout.timeout")
    Long requestTimeout;

    @KubernetesTestServer
    KubernetesServer mockServer;

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

    private static void createWiremockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        remoteServiceUrl = wireMockServer.baseUrl();
    }

    @BeforeEach
    void beforeEach() {
        createKnativeServiceIfNotExists(mockServer.getClient(), "knative/quarkus-greeting.yaml", NAMESPACE, SERVICENAME, remoteServiceUrl);
    }

    @Test
    void executeHttpGet() {
        mockExecuteHttpGetEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"hbelmiro\" }").when()
                .post("/getKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("Hello"));

        wireMockServer.verify(getRequestedFor(urlEqualTo("/plainJsonFunction?name=hbelmiro")));
    }

    @Test
    void executeWithEmptyParameters() {
        mockExecuteWithEmptyParametersEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\":{}}").when()
                .post("/emptyParamsKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.org", is("Acme"))
                .body("workflowdata.project", is("Kogito"));
    }

    @Test
    void executeWithParameters() {
        mockExecuteWithParametersEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"hbelmiro\" }").when()
                .post("/plainJsonKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("Hello"));
    }

    @Test
    void executeWithArray() {
        mockExecuteWithArrayEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON).when()
                .post("/arrayKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is(JsonNodeFactory.instance.arrayNode().add(23).add(24).toPrettyString()));
    }

    @Test
    void executeWithParametersShouldSendOnlyFunctionArgs() {
        mockExecuteWithParametersEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"hbelmiro\", \"should_not_be_sent\" : \"value\" }").when()
                .post("/plainJsonKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("Hello"));
    }

    @Test
    void executeWithCloudEventWithIdAsPlainJson() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON).when()
                .post("/cloudEventWithIdAsPlainJson")
                .then()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    void executeWithCloudEventWithoutIdAsPlainJson() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON).when()
                .post("/cloudEventWithoutIdAsPlainJson")
                .then()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Test
    void executeCloudEvent() {
        mockExecuteCloudEventWithParametersEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("/cloudEvent")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("CloudEvents are awesome!"))
                .body("workflowdata.object", is(JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "xpto").toPrettyString()));

        wireMockServer.verify(postRequestedFor(urlEqualTo(CLOUD_EVENT_PATH))
                .withRequestBody(matchingJsonPath("$.id", equalTo("42")))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void executeCloudEventWithMissingIdShouldNotThrowException() {
        mockExecuteCloudEventWithParametersEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("/cloudEventWithMissingId")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("CloudEvents are awesome!"))
                .body("workflowdata.object", is(JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "xpto").toPrettyString()));

        wireMockServer.verify(postRequestedFor(urlEqualTo(CLOUD_EVENT_PATH))
                .withRequestBody(matchingJsonPath("$.id", WireMock.matching(AT_LEAST_ONE_NON_WHITE_CHARACTER_REGEX)))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void cloudEventWithIdMustBeSentAsIs() {
        mockExecuteCloudEventWithParametersEndpoint();

        String id = "42";

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"id\": \"" + id + "\" }").when()
                .post("/cloudEventWithIdAsParam")
                .then()
                .statusCode(HttpURLConnection.HTTP_CREATED)
                .body("workflowdata.message", is("CloudEvents are awesome!"))
                .body("workflowdata.object", is(JsonNodeFactory.instance.objectNode()
                        .put("long", 42L)
                        .put("String", "xpto").toPrettyString()));

        wireMockServer.verify(postRequestedFor(urlEqualTo(CLOUD_EVENT_PATH))
                .withRequestBody(matchingJsonPath("$.id", equalTo(id)))
                .withHeader("Content-Type", equalTo(APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)));
    }

    @Test
    void executeWithInvalidCloudEvent() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON).when()
                .post("/invalidCloudEvent")
                .then()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    @Test
    void execute404() {
        mockExecute404Endpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON).when()
                .post("/serviceNotFound")
                .then()
                .statusCode(HttpURLConnection.HTTP_NOT_FOUND);
    }

    @Test
    void executeTimeout() {
        mockExecuteTimeoutEndpoint();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"hbelmiro\" }").when()
                .post("/timeoutKnativeFunction")
                .then()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
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

    private void mockExecuteCloudEventWithParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo(CLOUD_EVENT_PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_CLOUDEVENTS_JSON_CHARSET_UTF_8)
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "CloudEvents are awesome!")
                                .put("object", JsonNodeFactory.instance.objectNode()
                                        .put("long", 42L)
                                        .put("String", "xpto").toPrettyString()))));
    }

    private void mockExecuteWithEmptyParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/emptyParamsKnativeFunction"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("org", "Acme")
                                .put("project", "Kogito"))));
    }

    private void mockExecuteWithParametersEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/plainJsonFunction"))
                .withRequestBody(equalToJson(JsonNodeFactory.instance.objectNode()
                        .put("name", "hbelmiro")
                        .toString()))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "Hello"))));
    }

    private void mockExecuteHttpGetEndpoint() {
        wireMockServer.stubFor(get(urlEqualTo("/plainJsonFunction?name=hbelmiro"))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", "Hello"))));
    }

    private void mockExecuteWithArrayEndpoint() {
        wireMockServer.stubFor(post(urlEqualTo("/arrayFunction"))
                .withRequestBody(equalToJson(JsonNodeFactory.instance.objectNode()
                        .set("array", JsonNodeFactory.instance.arrayNode().add("Javierito").add("Pepito"))
                        .toString()))
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(JsonNodeFactory.instance.objectNode()
                                .put("message", JsonNodeFactory.instance.arrayNode().add(23).add(24).toPrettyString()))));
    }
}

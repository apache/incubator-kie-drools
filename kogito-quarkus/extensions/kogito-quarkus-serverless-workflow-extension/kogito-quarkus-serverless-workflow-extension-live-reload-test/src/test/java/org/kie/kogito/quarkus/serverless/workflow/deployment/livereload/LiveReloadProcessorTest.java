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
package org.kie.kogito.quarkus.serverless.workflow.deployment.livereload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.test.utils.SocketUtils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import io.grpc.Server;
import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

public class LiveReloadProcessorTest {

    private static final int PORT = SocketUtils.findAvailablePort();

    @RegisterExtension
    public final static QuarkusDevModeTest test = createTest();

    private static WireMockServer wireMockServer;

    private static QuarkusDevModeTest createTest() {
        configureWiremockServer();

        return new QuarkusDevModeTest()
                .withApplicationRoot(jar -> {
                    try {
                        jar.addAsResource(new StringAsset(applicationProperties(wireMockServer.baseUrl())), "/application.properties");
                        jar.add(new StringAsset(new String(Files.readAllBytes(Path.of("src/main/proto/greeting.proto")))), "src/main/proto/greeting.proto");
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    private static void configureWiremockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().extensions(new ResponseTemplateTransformer(false)).dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(post(urlEqualTo("/echo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"echoedMsgType\": \"{{jsonPath request.body '$.msgType'}}\"}")
                        .withTransformers("response-template")));
    }

    private static String applicationProperties(String wireMockBaseUrl) {
        return Stream.of(
                "quarkus.rest-client.\"enum_parameter_yaml\".url=" + wireMockBaseUrl,
                "quarkus.grpc.clients.Greeter.host=localhost",
                "quarkus.grpc.clients.Greeter.port=" + PORT,
                "quarkus.grpc.server.port=" + PORT,
                "quarkus.grpc.server.test-port=" + PORT,
                "quarkus.devservices.enabled=false",
                "quarkus.smallrye-openapi.management.enabled=true")
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @AfterAll
    static void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void testOpenApi() throws IOException {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(Map.of())
                .post("/openapienumparameter")
                .then()
                .statusCode(404);

        try (FileInputStream inputStream = new FileInputStream("src/test/resources/openAPIEnumParameter.sw.json")) {
            test.addResourceFile("openAPIEnumParameter.sw.json", new String(Objects.requireNonNull(inputStream).readAllBytes()));
        }

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(Map.of())
                .post("/openapienumparameter")
                .then()
                .statusCode(201)
                .body("workflowdata.echoedMsgType", is("text"));
    }

    @Test
    void testGrpc() throws InterruptedException, IOException {
        Server server = GreeterService.buildServer(PORT);
        server.start();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        try {
            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("{\"name\" : \"John\", \"language\":\"English\"}").when()
                    .post("/jsongreet")
                    .then()
                    .statusCode(404);

            try (FileInputStream inputStream = new FileInputStream("src/test/resources/rpcgreet.sw.json")) {
                test.addResourceFile("rpcgreet.sw.json", new String(Objects.requireNonNull(inputStream).readAllBytes()));
            }

            given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("{\"name\" : \"John\", \"language\":\"English\"}").when()
                    .post("/jsongreet")
                    .then()
                    .statusCode(201)
                    .body("workflowdata.message", containsString("Hello"));
        } finally {
            server.shutdownNow();
            server.awaitTermination();
        }
    }

    @Test
    void testAsyncApi() throws IOException {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", Collections.emptyMap()))
                .post("/asyncEventPublisher")
                .then()
                .statusCode(404);

        try (FileInputStream inputStream = new FileInputStream("src/test/resources/asyncPublisher.sw.json")) {
            test.addResourceFile("asyncPublisher.sw.json", new String(Objects.requireNonNull(inputStream).readAllBytes()));
        }

        String id = given()
                .contentType(ContentType.JSON)
                .when()
                .body(Collections.singletonMap("workflowdata", Collections.emptyMap()))
                .post("/asyncEventPublisher")
                .then()
                .statusCode(201)
                .extract().path("id");

        assertThat(id).isNotBlank();
    }
}

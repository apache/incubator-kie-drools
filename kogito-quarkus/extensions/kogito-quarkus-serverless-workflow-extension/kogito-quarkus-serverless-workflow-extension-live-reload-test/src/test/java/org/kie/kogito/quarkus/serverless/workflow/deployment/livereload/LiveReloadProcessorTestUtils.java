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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.shrinkwrap.api.asset.StringAsset;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import io.quarkus.test.QuarkusDevModeTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class LiveReloadProcessorTestUtils {

    static QuarkusDevModeTest createTest(WireMockServer wireMockServer, int port) {
        return new QuarkusDevModeTest()
                .withApplicationRoot(jar -> {
                    try {
                        jar.addAsResource(new StringAsset(applicationProperties(wireMockServer.baseUrl(), port)), "/application.properties");
                        jar.add(new StringAsset(new String(Files.readAllBytes(Path.of("src/main/proto/greeting.proto")))), "src/main/proto/greeting.proto");
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    static WireMockServer configureWiremockServer() {
        WireMockServer wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().extensions(new ResponseTemplateTransformer(null, false, null, java.util.Collections.emptyList())).dynamicPort());
        wireMockServer.start();

        wireMockServer.stubFor(post(urlEqualTo("/echo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"echoedMsgType\": \"{{jsonPath request.body '$.msgType'}}\"}")
                        .withTransformers("response-template")));

        return wireMockServer;
    }

    private static String applicationProperties(String wireMockBaseUrl, int port) {
        return Stream.of(
                "quarkus.rest-client.\"enum_parameter_yaml\".url=" + wireMockBaseUrl,
                "quarkus.grpc.clients.Greeter.host=localhost",
                "quarkus.grpc.clients.Greeter.port=" + port,
                "quarkus.grpc.server.port=" + port,
                "quarkus.grpc.server.test-port=" + port,
                "quarkus.devservices.enabled=false",
                "quarkus.smallrye-openapi.management.enabled=true",
                "quarkus.smallrye-health.management.enabled=true")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}

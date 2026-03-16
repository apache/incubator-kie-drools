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
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.test.utils.SocketUtils;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.LiveReloadProcessorTestUtils.configureWiremockServer;
import static org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.LiveReloadProcessorTestUtils.createTest;

public class AsyncApiLiveReloadProcessorTest {

    private static final int PORT = SocketUtils.findAvailablePort();

    private static final WireMockServer wireMockServer = configureWiremockServer();

    @RegisterExtension
    public static QuarkusDevModeTest test = createTest(wireMockServer, PORT);

    @AfterAll
    static void tearDown() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
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

        // Wait for Quarkus to complete the hot reload (max 60 seconds)
        await().atMost(Duration.ofSeconds(60))
                .untilAsserted(() -> {
                    String id = given()
                            .contentType(ContentType.JSON)
                            .when()
                            .body(Collections.singletonMap("workflowdata", Collections.emptyMap()))
                            .post("/asyncEventPublisher")
                            .then()
                            .statusCode(201)
                            .extract().path("id");

                    assertThat(id).isNotBlank();
                });
    }
}

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
import java.util.Objects;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.test.utils.SocketUtils;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.grpc.Server;
import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.LiveReloadProcessorTestUtils.configureWiremockServer;
import static org.kie.kogito.quarkus.serverless.workflow.deployment.livereload.LiveReloadProcessorTestUtils.createTest;

public class GrpcLiveReloadProcessorTest {

    private static final int PORT = SocketUtils.findAvailablePort();

    private static final WireMockServer wireMockServer = configureWiremockServer();

    @RegisterExtension
    public static QuarkusDevModeTest test = createTest(wireMockServer, PORT);

    private Server server;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void setup() throws IOException {
        server = GreeterService.buildServer(PORT);
        server.start();
    }

    @AfterEach
    public void dispose() throws InterruptedException {
        if (!server.isShutdown() && !server.isTerminated()) {
            server.shutdownNow();
            server.awaitTermination();
        }
    }

    @AfterAll
    static void tearDown() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Test
    void testGrpc() throws IOException {
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
                .get("/q/health")
                .then()
                .statusCode(200)
                .body("status", equalTo("UP"));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\" : \"John\", \"language\":\"English\"}").when()
                .post("/jsongreet")
                .then()
                .statusCode(201)
                .body("workflowdata.message", containsString("Hello"));
    }
}

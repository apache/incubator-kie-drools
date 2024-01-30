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
package org.kie.kogito.quarkus.it.openapi.client;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.quarkus.it.openapi.client.mocks.AuthSecurityMockService;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@QuarkusTestResource(AuthSecurityMockService.class)
class ApiWithSecurityContextIT {

    // injected by quarkus
    WireMockServer authWithApiKeyServer2;
    WireMockServer authWithApiKeyServer3;
    WireMockServer authWithApiKeyServer2NoAuth;
    WireMockServer authWithApiKeyServer3NoAuth;

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void verifyAuthHeadersOpenApi2_0() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("foo", "bar")))
                .post("/sec20")
                .then()
                .statusCode(201);

        // verify if the headers were correctly sent
        authWithApiKeyServer2
                .verify(postRequestedFor(urlEqualTo(AuthSecurityMockService.SEC_20.getPath()))
                        .withHeader("X-Client-Id", matching("Basic amF2aWVyaXRvOmZ1bGFuaXRv"))
                        .withHeader("Authorization", matching("Basic amF2aWVyaXRvOmZ1bGFuaXRv")));
    }

    @Test
    void verifyAuthHeadersOpenApi2_0NoAuth() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("foo", "bar")))
                .post("/sec20noAuth")
                .then()
                .statusCode(201);

        // verify if the headers were correctly sent
        authWithApiKeyServer2NoAuth
                .verify(postRequestedFor(urlEqualTo(AuthSecurityMockService.SEC_20_NO_AUTH.getPath()))
                        .withHeader("X-Client-Id", matching("12345")));
    }

    @Test
    void verifyAuthHeadersOpenApi3_0() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("foo", "bar")))
                .post("/sec30")
                .then()
                .statusCode(201);

        authWithApiKeyServer3
                .verify(postRequestedFor(urlEqualTo(AuthSecurityMockService.SEC_30.getPath()))
                        .withHeader("X-Client-Id", matching("Bearer mytoken,Bearer mytoken"))
                        .withHeader("Authorization", matching("Bearer mytoken")));
    }

    @Test
    void verifyAuthHeadersOpenApi3_0NoAuth() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        Collections
                                .singletonMap(
                                        "workflowdata",
                                        Collections.singletonMap("foo", "bar")))
                .post("/sec30noAuth")
                .then()
                .statusCode(201);

        authWithApiKeyServer3NoAuth
                .verify(postRequestedFor(urlEqualTo(AuthSecurityMockService.SEC_30_NO_AUTH.getPath()))
                        .withHeader("X-Client-Id", matching("12345")));
    }

}

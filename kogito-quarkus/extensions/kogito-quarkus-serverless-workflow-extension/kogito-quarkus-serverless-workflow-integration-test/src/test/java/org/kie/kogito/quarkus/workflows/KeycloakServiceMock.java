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
package org.kie.kogito.quarkus.workflows;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Lightweight Keycloak mock to use when an OidcClient is required, and we don't want/need to start a full Keycloak
 * container as part of the tests, etc. Keep the things simple.
 */
public class KeycloakServiceMock implements QuarkusTestResourceLifecycleManager {

    public static final String KEY_CLOAK_SERVICE_URL = "keycloak.mock.service.url";
    public static final String KEY_CLOAK_SERVICE_TOKEN_PATH = "keycloak.mock.service.token-path";
    public static final String REALM = "kogito-tests";
    public static final String KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE = "/realms/" + REALM + "/protocol/openid-connect/token";
    public static final String CLIENT_ID = "kogito-app";
    public static final String SECRET = "secret";
    public static final String KEYCLOAK_ACCESS_TOKEN = "KEYCLOAK_ACCESS_TOKEN";
    public static final String KEYCLOAK_REFRESH_TOKEN = "KEYCLOAK_REFRESH_TOKEN";
    public static final String KEYCLOAK_SESSION_STATE = "KEYCLOAK_SESSION_STATE";

    public static final String AUTH_REQUEST_BODY = "grant_type=client_credentials";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        stubFor(post(KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE)
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED))
                .withBasicAuth(CLIENT_ID, SECRET)
                .withRequestBody(equalTo(AUTH_REQUEST_BODY))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(getTokenResult())));

        Map<String, String> properties = new HashMap<>();
        properties.put(KEY_CLOAK_SERVICE_URL, wireMockServer.baseUrl());
        properties.put(KEY_CLOAK_SERVICE_TOKEN_PATH, KEY_CLOAK_SERVICE_TOKEN_PATH_VALUE);
        return properties;
    }

    private static String getTokenResult() {
        return "{\n" +
                "    \"access_token\": \"" + KEYCLOAK_ACCESS_TOKEN + "\",\n" +
                "    \"expires_in\": 300,\n" +
                "    \"refresh_expires_in\": 1800,\n" +
                "    \"refresh_token\": \"" + KEYCLOAK_REFRESH_TOKEN + "\",\n" +
                "    \"token_type\": \"bearer\",\n" +
                "    \"not-before-policy\": 0,\n" +
                "    \"session_state\": \"" + KEYCLOAK_SESSION_STATE + "\",\n" +
                "    \"scope\": \"email profile\"\n" +
                "}";
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}

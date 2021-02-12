/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.process.service.client;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class WireMockKeycloakResource implements QuarkusTestResourceLifecycleManager {

    public static final String KEY_CLOAK_SERVICE_URL = "keycloak.service.url";
    public static final String REALM = "kogito-tests";
    public static final String CLIENT_ID = "kogito-backend-service";
    public static final String SECRET = "secret";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String SESSION_STATE = "SESSION_STATE";
    public static final String KEYCLOAK_USER = "KeycloakUser";
    public static final String KEYCLOAK_PASSWORD = "KeycloakPassword";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        stubFor(post(buildTokenRequestUrl(REALM))
                        .withHeader(CONTENT_TYPE, equalTo(APPLICATION_FORM_URLENCODED))
                        .withBasicAuth(CLIENT_ID, SECRET)
                        .withRequestBody(equalTo(getAuthRequestBody()))
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                            .withBody(getTokenResult())

                        )
        );

        return Collections.singletonMap(KEY_CLOAK_SERVICE_URL, wireMockServer.baseUrl());
    }

    private static UrlPattern buildTokenRequestUrl(String realm) {
        return urlEqualTo("/realms/" + realm + "/protocol/openid-connect/token");
    }

    private static String getAuthRequestBody() {
        return "grant_type=password&username=" + KEYCLOAK_USER + "&password=" + KEYCLOAK_PASSWORD;
    }

    private static String getTokenResult() {
        return "{\n" +
                "    \"access_token\": \"" + ACCESS_TOKEN + "\",\n" +
                "    \"expires_in\": 300,\n" +
                "    \"refresh_expires_in\": 1800,\n" +
                "    \"refresh_token\": \"" + REFRESH_TOKEN + "\",\n" +
                "    \"token_type\": \"bearer\",\n" +
                "    \"not-before-policy\": 0,\n" +
                "    \"session_state\": \"" + SESSION_STATE + "\",\n" +
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

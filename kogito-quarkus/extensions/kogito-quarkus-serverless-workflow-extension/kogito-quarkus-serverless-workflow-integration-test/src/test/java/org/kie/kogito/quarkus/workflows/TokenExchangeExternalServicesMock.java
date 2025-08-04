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

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import jakarta.ws.rs.core.HttpHeaders;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.kie.kogito.quarkus.workflows.KeycloakServiceMock.KEYCLOAK_ACCESS_TOKEN;
import static org.kie.kogito.quarkus.workflows.KeycloakServiceMock.KEYCLOAK_EXCHANGED_ACCESS_TOKEN;

public class TokenExchangeExternalServicesMock implements QuarkusTestResourceLifecycleManager {

    public static final String BASE_AND_PROPAGATED_AUTHORIZATION_TOKEN = "BASE_AND_PROPAGATED_AUTHORIZATION_TOKEN";

    private static final String BEARER = "Bearer ";

    public static final String TOKEN_PROPAGATION_EXTERNAL_SERVICE_MOCK_URL = "exchange-external-service-mock.url";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        // stub the /token-exchange-external-service/withExchange invocation with the expected token
        stubForExternalService("/token-exchange-external-service/withExchange", KEYCLOAK_EXCHANGED_ACCESS_TOKEN);

        // stub the /token-exchange-external-service/withExchangeAndPropagation invocation with the expected token
        stubForExternalService("/token-exchange-external-service/withExchangeAndPropagation", BASE_AND_PROPAGATED_AUTHORIZATION_TOKEN);

        // stub token-exchange-external-service/withoutExchange invocation with the expected token, no propagation nor
        //  exchange are produced in this case but the service must receive the token provided by Keycloak since it has
        // oauth2 security configured.
        stubForExternalService("/token-exchange-external-service/withoutExchange", KEYCLOAK_ACCESS_TOKEN);

        return Collections.singletonMap(TOKEN_PROPAGATION_EXTERNAL_SERVICE_MOCK_URL, wireMockServer.baseUrl());
    }

    private static void stubForExternalService(String tokenPropagationExternalServiceUrl, String authorizationToken) {
        stubFor(post(tokenPropagationExternalServiceUrl)
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo(BEARER + authorizationToken))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

}

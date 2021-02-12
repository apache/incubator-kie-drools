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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.kie.kogito.taskassigning.process.service.client.WireMockKeycloakResource.ACCESS_TOKEN;

public class WireMockProcessResource implements QuarkusTestResourceLifecycleManager {

    public static final String PROCESS_SERVICE_URL = "process.service.url";

    public static final String PROCESS_ID = "ProcessId";
    public static final String BASIC_AUTH_PROCESS_ID = "BasicAuthProcessId";
    public static final String KEYCLOAK_AUTH_PROCESS_ID = "KeyCloakAuthProcessId";

    public static final String AUTH_USER = "authUser";
    public static final String AUTH_PASSWORD = "authPassword";
    public static final String PROCESS_INSTANCE_ID = "ProcessInstanceId";
    public static final String TASK_ID = "TaskId";
    public static final String WORKITEM_ID = "WorkitemId";
    public static final String USER = "user";
    public static final String GROUP1 = "group1";
    public static final String GROUP2 = "group2";
    public static final String PHASE1 = "phase1";
    public static final String PHASE2 = "phase2";

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        stubFor(get(buildGetPhasesUrl(PROCESS_ID))
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                            .withBody(getPhasesResponse())
                        )
        );

        stubFor(get(buildGetPhasesUrl(BASIC_AUTH_PROCESS_ID))
                        .withBasicAuth(AUTH_USER, AUTH_PASSWORD)
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                            .withBody(getPhasesResponse())
                        )
        );

        stubFor(get(buildGetPhasesUrl(KEYCLOAK_AUTH_PROCESS_ID))
                        .withHeader(AUTHORIZATION, equalTo("Bearer " + ACCESS_TOKEN))
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                            .withBody(getPhasesResponse())
                        )
        );

        stubFor(post(buildTransitionTaskUrl())
                        .willReturn(aResponse()
                                            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                            .withBody("{}")
                        )
        );

        return Collections.singletonMap(PROCESS_SERVICE_URL, wireMockServer.baseUrl());
    }

    private static UrlPattern buildGetPhasesUrl(String processId) {
        return urlEqualTo("/" + processId + "/" + PROCESS_INSTANCE_ID + "/" + TASK_ID + "/" + WORKITEM_ID + "/schema" +
                                  "?user=" + USER + "&group=" + GROUP1 + "&group=" + GROUP2);
    }

    private static String getPhasesResponse() {
        return "{ \"phases\" : [\"" + PHASE1 + "\", \"" + PHASE2 + "\"] }";
    }

    private static UrlPattern buildTransitionTaskUrl() {
        return urlEqualTo("/" + PROCESS_ID + "/" + PROCESS_INSTANCE_ID + "/" + TASK_ID + "/" + WORKITEM_ID +
                                  "?phase=" + PHASE1 + "&user=" + USER + "&group=" + GROUP1 + "&group=" + GROUP2);
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}

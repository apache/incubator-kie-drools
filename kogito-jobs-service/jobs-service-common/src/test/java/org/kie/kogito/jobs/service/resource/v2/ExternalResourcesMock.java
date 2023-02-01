/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.resource.v2;

import java.util.Collections;
import java.util.Map;

import org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.BinaryEqualToPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.BINARY_VALUE;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.EXTERNAL_RESOURCE_FOR_BINARY_PAYLOAD;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.EXTERNAL_RESOURCE_FOR_JSON_PAYLOAD;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.EXTERNAL_RESOURCE_FOR_STRING_PAYLOAD;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_HEADER_1;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_HEADER_1_VALUE;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_HEADER_2;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_HEADER_2_VALUE;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_QUERY_PARAM_1;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.HTTP_QUERY_PARAM_1_VALUE;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.PROPERTY_1;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.PROPERTY_1_VALUE;
import static org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesIT.TEXT_PLAIN_VALUE;

/**
 * This resource emulates external resources that can be used in different jobs service tests, for example to check
 * that the v2 Api can manage different HttpRecipientPayloads, etc. If you mock invocations on this class, please
 * indicate to which particular test they are linked. In this way we can maintain the coexistence of different tests
 * that use this resource under control.
 */
public class ExternalResourcesMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    private String baseUrl;

    public static final String EXTERNAL_RESOURCES_MOCK_URL = "external-resources-mock.url";

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        setUpBaseHttpRecipientPayloadTypesIT();
        baseUrl = wireMockServer.baseUrl();
        return Collections.singletonMap(EXTERNAL_RESOURCES_MOCK_URL, baseUrl);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Override
    public void inject(Object testInstance) {
        if (testInstance instanceof BaseHttpRecipientPayloadTypesIT) {
            ((BaseHttpRecipientPayloadTypesIT) testInstance).setExternalResourcesServer(wireMockServer);
        }
    }

    /**
     * Mock the invocations used by the BaseHttpRecipientPayloadTypesIT.
     */
    private void setUpBaseHttpRecipientPayloadTypesIT() {
        // json payload invocation
        stubFor(post(addQueryParams(EXTERNAL_RESOURCE_FOR_JSON_PAYLOAD))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .withHeader(HTTP_HEADER_1, equalTo(HTTP_HEADER_1_VALUE))
                .withHeader(HTTP_HEADER_2, equalTo(HTTP_HEADER_2_VALUE))
                .withRequestBody(equalToJson("{\"" + PROPERTY_1 + "\" : \"" + PROPERTY_1_VALUE + "\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // string payload invocation
        stubFor(post(addQueryParams(EXTERNAL_RESOURCE_FOR_STRING_PAYLOAD))
                .withHeader(CONTENT_TYPE, equalTo(TEXT_PLAIN))
                .withHeader(HTTP_HEADER_1, equalTo(HTTP_HEADER_1_VALUE))
                .withHeader(HTTP_HEADER_2, equalTo(HTTP_HEADER_2_VALUE))
                .withRequestBody(equalTo(TEXT_PLAIN_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // binary payload invocation
        stubFor(post(addQueryParams(EXTERNAL_RESOURCE_FOR_BINARY_PAYLOAD))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_OCTET_STREAM))
                .withHeader(HTTP_HEADER_1, equalTo(HTTP_HEADER_1_VALUE))
                .withHeader(HTTP_HEADER_2, equalTo(HTTP_HEADER_2_VALUE))
                .withRequestBody(new BinaryEqualToPattern(BINARY_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));
    }

    private static String addQueryParams(String url) {
        return String.format("%s?%s=%s", url, HTTP_QUERY_PARAM_1, HTTP_QUERY_PARAM_1_VALUE);
    }
}

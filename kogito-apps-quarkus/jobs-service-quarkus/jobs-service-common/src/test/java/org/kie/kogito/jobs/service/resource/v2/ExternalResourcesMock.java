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
package org.kie.kogito.jobs.service.resource.v2;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.kie.kogito.jobs.service.resource.v2.http.recipient.BaseHttpRecipientPayloadTypesTest;
import org.kie.kogito.jobs.service.resource.v2.sink.recipient.BaseSinkRecipientPayloadTypesTest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.BinaryEqualToPattern;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

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

    private static final String LIMIT_QUERY_PARAM = "limit";

    private static final String LIMIT_QUERY_PARAM_VALUE = "0";

    public interface ExternalResourcesMockAware {
        void setExternalResourcesServer(WireMockServer externalResourcesServer);
    }

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        setUpBaseHttpRecipientPayloadTypesIT();
        setUpBaseSinkRecipientPayloadTypesIT();

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
        if (testInstance instanceof ExternalResourcesMockAware) {
            ((ExternalResourcesMockAware) testInstance).setExternalResourcesServer(wireMockServer);
        }
    }

    /**
     * Mock the invocations used by the BaseHttpRecipientPayloadTypesIT.
     */
    private void setUpBaseHttpRecipientPayloadTypesIT() {
        // json payload invocation
        stubFor(post(new UrlPathPattern(equalTo(BaseHttpRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_JSON_PAYLOAD), false))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1_VALUE))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2_VALUE))
                .withQueryParam(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1_VALUE))
                .withQueryParam(LIMIT_QUERY_PARAM, equalTo(LIMIT_QUERY_PARAM_VALUE))
                .withRequestBody(equalToJson("{\"" + BaseHttpRecipientPayloadTypesTest.HTTP_PROPERTY_1 + "\" : \"" + BaseHttpRecipientPayloadTypesTest.HTTP_PROPERTY_1_VALUE + "\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // string payload invocation
        stubFor(post(new UrlPathPattern(equalTo(BaseHttpRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_STRING_PAYLOAD), false))
                .withHeader(CONTENT_TYPE, equalTo(TEXT_PLAIN))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1_VALUE))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2_VALUE))
                .withQueryParam(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1_VALUE))
                .withQueryParam(LIMIT_QUERY_PARAM, equalTo(LIMIT_QUERY_PARAM_VALUE))
                .withRequestBody(equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_TEXT_PLAIN_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // binary payload invocation
        stubFor(post(new UrlPathPattern(equalTo(BaseHttpRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_BINARY_PAYLOAD), false))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_OCTET_STREAM))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_1_VALUE))
                .withHeader(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_HEADER_2_VALUE))
                .withQueryParam(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1, equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1_VALUE))
                .withQueryParam(LIMIT_QUERY_PARAM, equalTo(LIMIT_QUERY_PARAM_VALUE))
                .withRequestBody(new BinaryEqualToPattern(BaseHttpRecipientPayloadTypesTest.HTTP_BINARY_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // job with custom timeout
        stubFor(post(new UrlPathPattern(equalTo(BaseHttpRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_CUSTOM_TIMEOUT), false))
                .withHeader(CONTENT_TYPE, equalTo(TEXT_PLAIN))
                .withQueryParam(LIMIT_QUERY_PARAM, equalTo(LIMIT_QUERY_PARAM_VALUE))
                .withRequestBody(equalTo(BaseHttpRecipientPayloadTypesTest.HTTP_TEXT_PLAIN_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));
    }

    /**
     * Mock the invocations used by the BaseSinkRecipientPayloadTypesIT.
     */
    private void setUpBaseSinkRecipientPayloadTypesIT() {
        // binary mode with json payload invocation
        stubFor(applySinkRecipientBinaryModeCommonMappings(post(BaseSinkRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_BINARY_MODE_JSON_PAYLOAD))
                .withRequestBody(equalToJson("{\"" + BaseSinkRecipientPayloadTypesTest.SINK_PROPERTY_1 + "\" : \"" + BaseSinkRecipientPayloadTypesTest.SINK_PROPERTY_1_VALUE + "\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // binary mode with binary payload invocation
        stubFor(applySinkRecipientBinaryModeCommonMappings(post(BaseSinkRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_BINARY_MODE_BINARY_PAYLOAD))
                .withRequestBody(new BinaryEqualToPattern(BaseSinkRecipientPayloadTypesTest.SINK_BINARY_VALUE))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // structured mode with json payload invocation
        stubFor(applySinkRecipientStructuredModeCommonMappings(post(BaseSinkRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_JSON_PAYLOAD))
                .withRequestBody(matchingJsonPath("data." + BaseSinkRecipientPayloadTypesTest.SINK_PROPERTY_1, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_PROPERTY_1_VALUE)))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));

        // structured mode with binary payload invocation
        stubFor(applySinkRecipientStructuredModeCommonMappings(post(BaseSinkRecipientPayloadTypesTest.EXTERNAL_RESOURCE_FOR_STRUCTURED_MODE_BINARY_PAYLOAD))
                .withRequestBody(matchingJsonPath("data_base64", equalTo(Base64.getEncoder().encodeToString(BaseSinkRecipientPayloadTypesTest.SINK_BINARY_VALUE))))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody("{}")));
    }

    private static MappingBuilder applySinkRecipientBinaryModeCommonMappings(MappingBuilder mappingBuilder) {
        return mappingBuilder
                .withHeader(CONTENT_TYPE, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_DATACONTENTTYPE))
                .withHeader("ce-specversion", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SPECVERSION.toString()))
                .withHeader("ce-type", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_TYPE))
                .withHeader("ce-dataschema", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_DATASCHEMA.toString()))
                .withHeader("ce-source", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SOURCE.toString()))
                .withHeader("ce-subject", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SUBJECT))
                .withHeader("ce-" + BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_1_NAME, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_1_VALUE))
                .withHeader("ce-" + BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_2_NAME, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_2_VALUE))
                .withHeader("ce-limit", equalTo("0"));
    }

    private static MappingBuilder applySinkRecipientStructuredModeCommonMappings(MappingBuilder mappingBuilder) {
        return mappingBuilder
                .withHeader(CONTENT_TYPE, equalTo("application/cloudevents+json"))
                .withRequestBody(matchingJsonPath("specversion", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SPECVERSION.toString())))
                .withRequestBody(matchingJsonPath("type", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_TYPE)))
                .withRequestBody(matchingJsonPath("datacontenttype", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_DATACONTENTTYPE)))
                .withRequestBody(matchingJsonPath("dataschema", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_DATASCHEMA.toString())))
                .withRequestBody(matchingJsonPath("source", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SOURCE.toString())))
                .withRequestBody(matchingJsonPath("subject", equalTo(BaseSinkRecipientPayloadTypesTest.SINK_CE_SUBJECT)))
                .withRequestBody(matchingJsonPath(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_1_NAME, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_1_VALUE)))
                .withRequestBody(matchingJsonPath(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_2_NAME, equalTo(BaseSinkRecipientPayloadTypesTest.SINK_EXTENSION_2_VALUE)))
                .withRequestBody(matchingJsonPath("limit", equalTo("0")));
    }

    private static String addQueryParams(String url) {
        return String.format("%s?%s=%s", url, BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1, BaseHttpRecipientPayloadTypesTest.HTTP_QUERY_PARAM_1_VALUE);
    }
}

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
package org.kie.kogito.serverless.workflow.executor;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.utils.RestWorkflowUtils.URL;
import static org.kie.kogito.serverless.workflow.utils.RestWorkflowUtils.getOpenApiPrefix;

class OpenAPIWorkflowApplicationTest {

    private static final String URI_PROPERTY = getOpenApiPrefix("spec_yaml") + "." + URL;

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @BeforeAll
    static void init() {
        System.setProperty(URI_PROPERTY, wm.baseUrl());
    }

    @AfterAll
    static void cleanup() {
        System.clearProperty(URI_PROPERTY);
    }

    @Test
    void openAPIInvocation() throws IOException {
        final double fahrenheit = 100;
        final double difference = fahrenheit - 32.0;
        final double product = difference * 0.5556;
        wm.stubFor(post("/multiply").willReturn(aResponse().withStatus(200).withJsonBody(ObjectMapperFactory.get().createObjectNode().put("product", product))));
        wm.stubFor(post("/substract").willReturn(aResponse().withStatus(200).withJsonBody(ObjectMapperFactory.get().createObjectNode().put("difference", difference))));
        URL resource = Thread.currentThread().getContextClassLoader().getResource("fahrenheit-to-celsius.sw.json");
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            ObjectNode node = (ObjectNode) application.execute(ServerlessWorkflowUtils.getWorkflow(resource), Collections.singletonMap("fahrenheit", fahrenheit)).getWorkflowdata();
            assertThat(node.get("product").asDouble()).isEqualByComparingTo(product);
        }
    }

    @Test
    void stringContentTypeInvocation() throws IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("stringResource.sw.json");
        wm.stubFor(post("/stringResource/reverse").withRequestBody(equalTo("redrum")).willReturn(aResponse().withStatus(200).withBody("murder")));
        try (StaticWorkflowApplication application = StaticWorkflowApplication.create()) {
            ObjectNode node = (ObjectNode) application.execute(ServerlessWorkflowUtils.getWorkflow(resource), Collections.singletonMap("name", "redrum")).getWorkflowdata();
            assertThat(node.get("response")).isEqualTo(new TextNode("murder"));
        }
    }
}

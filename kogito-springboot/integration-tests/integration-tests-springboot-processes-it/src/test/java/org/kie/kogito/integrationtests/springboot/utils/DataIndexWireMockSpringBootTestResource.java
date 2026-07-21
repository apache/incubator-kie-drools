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
package org.kie.kogito.integrationtests.springboot.utils;

import java.util.Map;

import org.kie.kogito.test.resources.ConditionalSpringBootTestResource;
import org.kie.kogito.test.resources.TestResource;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.Collections.singletonMap;

public class DataIndexWireMockSpringBootTestResource extends ConditionalSpringBootTestResource<DataIndexWireMockSpringBootTestResource.KogitoDataIndexWiremockTestResource> {

    public static final String KOGITO_DATA_INDEX_URL_WIREMOCK_PROPERTY = "kogito.dataindex.http.url";
    public static KogitoDataIndexWiremockTestResource testResource = new KogitoDataIndexWiremockTestResource();

    public DataIndexWireMockSpringBootTestResource() {
        super(testResource);
    }

    @Override
    protected Map<String, String> getProperties() {
        return singletonMap(KOGITO_DATA_INDEX_URL_WIREMOCK_PROPERTY, String.format("http://localhost:%s", getTestResource().getMappedPort()));
    }

    public static class Conditional extends DataIndexWireMockSpringBootTestResource {

        public Conditional() {
            super();
            enableConditional();
        }
    }

    public static class KogitoDataIndexWiremockTestResource implements TestResource {
        private final static String jsonString = "{\n" +
                "  \"data\": {\n" +
                "    \"ProcessInstances\": [\n" +
                "      {\n" +
                "        \"id\": \"piId\",\n" +
                "        \"processId\": \"processId\",\n" +
                "        \"nodes\": [\n" +
                "          {\n" +
                "            \"definitionId\": \"_9861B686-DF6B-4B1C-B370-F9898EEB47FD\",\n" +
                "            \"exit\": \"2020-10-11T06:49:47.26Z\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"definitionId\": \"_8B62D3CA-5D03-4B2B-832B-126469288BB4\",\n" +
                "            \"exit\": null\n" +
                "          }\n" +
                "        ]\n" +
                "      } " +
                "    ]\n" +
                "  }\n" +
                "}";
        private WireMockServer server;

        public KogitoDataIndexWiremockTestResource() {
        }

        @Override
        public void stop() {
            if (server != null) {
                server.stop();
            }
        }

        @Override
        public String getResourceName() {
            return "KogitoDataIndexWiremockTestResource";
        };

        @Override
        public void start() {
            server = new WireMockServer(options().dynamicPort());
            server.start();
            configureFor(server.port());
            stubFor(post(urlEqualTo("/graphql"))
                    .willReturn(okJson(jsonString)));

        }

        @Override
        public int getMappedPort() {
            return server.port();
        };
    }

}

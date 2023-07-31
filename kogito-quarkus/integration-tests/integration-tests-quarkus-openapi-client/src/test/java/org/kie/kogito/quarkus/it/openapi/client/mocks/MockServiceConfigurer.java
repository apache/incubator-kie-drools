/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.it.openapi.client.mocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public abstract class MockServiceConfigurer implements QuarkusTestResourceLifecycleManager {

    private final Map<String, WireMockServer> servers = new HashMap<>();

    public MockServiceConfigurer(MockServerConfig... configs) {
        Arrays.stream(configs).forEach(c -> {
            final WireMockServer server = new WireMockServer(c.getPort());
            server.stubFor(post(urlEqualTo("/oauth/token")).willReturn(aResponse().withHeader("Content-Type", "application/json").withJsonBody(
                    ObjectMapperFactory.get().createObjectNode().put("access_token", "mytoken"))));
            server.stubFor(post(urlEqualTo(c.getPath()))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withBody(c.getResponse())));
            servers.put(c.getBeanName(), server);
        });
    }

    @Override
    public final void inject(TestInjector testInjector) {
        servers.forEach((bean, s) -> testInjector.injectIntoFields(s, field -> bean.equals(field.getName())));
    }

    @Override
    public final Map<String, String> start() {
        servers.forEach((b, server) -> {
            if (server != null) {
                server.start();
                this.doStub(server);
            }
        });
        final Map<String, String> properties = new HashMap<>();
        properties.put("quarkus.rest-client.swagger2_0_security_yaml.url", "http://localhost:" + AuthSecurityMockService.SEC_20.getPort() + "/iq9MzY");
        properties.put("quarkus.rest-client.swagger2_0_security_no_auth_yaml.url", "http://localhost:" + AuthSecurityMockService.SEC_20_NO_AUTH.getPort());

        properties.put("quarkus.rest-client.openapi3_0_security_yaml.url", "http://localhost:" + AuthSecurityMockService.SEC_30.getPort());
        properties.put("quarkus.oidc-client.oauth.auth-server-url", "http://localhost:" + AuthSecurityMockService.SEC_30.getPort());
        properties.put("quarkus.rest-client.openapi3_0_security_no_auth_yaml.url", "http://localhost:" + AuthSecurityMockService.SEC_30_NO_AUTH.getPort());
        return properties;
    }

    @Override
    public final void stop() {
        servers.forEach((b, server) -> {
            if (server != null) {
                server.stop();
            }
        });
    }

    protected void doStub(WireMockServer server) {
        // to be implemented by the child class
    }

}

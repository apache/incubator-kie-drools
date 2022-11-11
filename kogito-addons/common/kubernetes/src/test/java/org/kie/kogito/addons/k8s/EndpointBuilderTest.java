/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.k8s;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointBuilderTest {

    @Test
    public void testBuildFromService() {
        final EndpointBuilder builder = new EndpointBuilder();
        final Map<Service, String> testCases = new HashMap<>();

        final Service httpService = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc1")
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withName("http").withPort(8080).build())
                        .build())
                .build();
        testCases.put(httpService, "http://127.0.0.1:8080");

        final Service httpsService = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc2")
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withName("https").withPort(8443).build())
                        .build())
                .build();
        testCases.put(httpsService, "https://127.0.0.1:8443");

        final Service httpAndHttpsService = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc2")
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withName("http").withPort(8080).build(), new ServicePortBuilder().withName("https").withPort(8443).build())
                        .build())
                .build();
        testCases.put(httpAndHttpsService, "https://127.0.0.1:8443");

        final Service secureServiceNonConventionalPort = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc3")
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withName("https").withPort(5443).build())
                        .build())
                .build();
        testCases.put(secureServiceNonConventionalPort, "https://127.0.0.1:5443");

        final Service randomService = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc4")
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withPort(8775).build())
                        .build())
                .build();
        testCases.put(randomService, "http://127.0.0.1:8775");

        final Service multiplePorts = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc5")
                .withLabels(Collections.singletonMap(EndpointBuilder.PRIMARY_PORT_NAME, "niceport"))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withPort(8080).withName("niceport").build(),
                                new ServicePortBuilder().withPort(8775).withName("randomport").build())
                        .build())
                .build();
        testCases.put(multiplePorts, "http://127.0.0.1:8080");

        testCases.forEach((s, e) -> {
            final Endpoint endpoint = builder.buildFrom(s);
            assertThat(endpoint.getUrl()).as("Failed to assert test case for service " + s.getMetadata().getName()).isEqualTo(e);
        });
    }

    @Test
    public void testMultiplePortsWithPrimaryLabel() {
        final Service multiplePorts = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc5")
                .withLabels(Collections.singletonMap(EndpointBuilder.PRIMARY_PORT_NAME, "niceport"))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withPort(8080).withName("niceport").build(),
                                new ServicePortBuilder().withPort(8775).withName("randomport").build())
                        .build())
                .build();
        final Endpoint endpoint = new EndpointBuilder().buildFrom(multiplePorts);
        assertThat(endpoint.getUrl()).isEqualTo("http://127.0.0.1:8080");
        assertThat(endpoint.getSecondaryURLs()).hasSize(1);
        assertThat(endpoint.getSecondaryUrl("randomport")).isEqualTo("http://127.0.0.1:8775");
        assertThat(endpoint.getLabels()).isNotEmpty();
    }

    @Test
    public void testMultipleValidPorts() {
        final Service multiplePorts = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc7")
                .withLabels(Collections.singletonMap(EndpointBuilder.PRIMARY_PORT_NAME, "niceport"))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withPort(8080).withName("http").build(),
                                new ServicePortBuilder().withPort(8443).withName("https").build(),
                                new ServicePortBuilder().withPort(8778).withName("randomport").build())
                        .build())
                .build();
        final Endpoint endpoint = new EndpointBuilder().buildFrom(multiplePorts);
        assertThat(endpoint.getUrl()).isEqualTo("https://127.0.0.1:8443");
        assertThat(endpoint.getSecondaryURLs()).hasSize(2);
        assertThat(endpoint.getSecondaryUrl("http")).isEqualTo("http://127.0.0.1:8080");
        assertThat(endpoint.getSecondaryUrl("randomport")).isEqualTo("http://127.0.0.1:8778");
        assertThat(endpoint.getLabels()).isNotEmpty();
    }

    @Test
    public void testMultipleValidPortsAndLabel() {
        final Service multiplePorts = new ServiceBuilder()
                .withNewMetadata()
                .withName("svc7")
                .withLabels(Collections.singletonMap(EndpointBuilder.PRIMARY_PORT_NAME, "niceport"))
                .endMetadata()
                .withSpec(new ServiceSpecBuilder().withClusterIP("127.0.0.1")
                        .withPorts(new ServicePortBuilder().withPort(8080).withName("http").build(),
                                new ServicePortBuilder().withPort(8443).withName("https").build(),
                                new ServicePortBuilder().withPort(8181).withName("niceport").build())
                        .build())
                .build();
        final Endpoint endpoint = new EndpointBuilder().buildFrom(multiplePorts);
        assertThat(endpoint.getUrl()).isEqualTo("http://127.0.0.1:8181");
        assertThat(endpoint.getSecondaryURLs()).hasSize(2);
        assertThat(endpoint.getSecondaryUrl("http")).isEqualTo("http://127.0.0.1:8080");
        assertThat(endpoint.getSecondaryUrl("https")).isEqualTo("https://127.0.0.1:8443");
        assertThat(endpoint.getLabels()).isNotEmpty();
    }
}

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
package org.kie.kogito.addons.quarkus.k8s;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointDiscovery;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@WithKubernetesTestServer
public class KubernetesServiceEndpointDiscoveryTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Named("default")
    @Inject
    EndpointDiscovery endpointDiscovery;

    private void createServiceIfNotExist(final String name, Map<String, String> labels, Integer... ports) {
        if (mockServer.getClient().services().inNamespace("test").withName(name).get() != null) {
            return;
        }
        final List<ServicePort> sPorts = new ArrayList<>();
        for (Integer integer : ports) {
            final ServicePort port = new ServicePort();
            port.setPort(integer);
            sPorts.add(port);
        }
        final Service svc = new ServiceBuilder()
                .withNewMetadata()
                .withName(name).withNamespace("test")
                .withLabels(labels)
                .and().withSpec(new ServiceSpec()).build();
        svc.getSpec().setClusterIP("127.0.0.1");
        svc.getSpec().setPorts(sPorts);

        mockServer.getClient().resource(svc).createOrReplace();
    }

    @Test
    public void testGetURLOnStandardPort() {
        createServiceIfNotExist("svc1", Collections.emptyMap(), 80, 8776);
        final Optional<Endpoint> endpoint = endpointDiscovery.findEndpoint("test", "svc1");
        assertTrue(endpoint.isPresent());
        assertFalse(endpoint.get().getUrl().isEmpty());
        try {
            new URL(endpoint.get().getUrl());
        } catch (MalformedURLException e) {
            fail("The generated URL " + endpoint.get().getUrl() + " is invalid"); //verbose
        }
    }

    @Test
    public void testGetURLOnRandomPort() {
        createServiceIfNotExist("svc2", Collections.singletonMap("app", "test1"), 8778);
        final List<Endpoint> endpoints = endpointDiscovery.findEndpoint("test", Collections.singletonMap("app", "test1"));
        assertFalse(endpoints.isEmpty());
        assertFalse(endpoints.get(0).getUrl().isEmpty());
        try {
            new URL(endpoints.get(0).getUrl());
        } catch (MalformedURLException e) {
            fail("The generated URL " + endpoints.get(0).getUrl() + " is invalid"); //verbose
        }
    }
}

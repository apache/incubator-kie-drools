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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.KnativeRouteEndpointDiscovery;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Route;
import io.fabric8.knative.serving.v1.RouteBuilder;
import io.fabric8.knative.serving.v1.RouteStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@WithKubernetesTestServer
public class KnativeRouteEndpointDiscoveryTest {

    @Inject
    KubernetesClient kubernetesClient;
    KnativeClient knativeClient;

    @BeforeEach
    public void setup() {
        knativeClient = kubernetesClient.adapt(KnativeClient.class);
    }

    @Test
    public void testBaseCase() {
        final KnativeRouteEndpointDiscovery endpointDiscovery = new KnativeRouteEndpointDiscovery(null);
        endpointDiscovery.setKnativeClient(knativeClient);

        // configure mock
        final RouteStatus status = new RouteStatus();
        status.setUrl("http://192.168.2.32");
        final Route route = new RouteBuilder().withNewMetadata().withName("ksvc1").withNamespace("test").and().withStatus(status).build();
        knativeClient.routes().create(route);

        final Optional<Endpoint> endpoint = endpointDiscovery.findEndpoint("test", "ksvc1");
        assertTrue(endpoint.isPresent());

        try {
            new URL(endpoint.get().getUrl());
        } catch (MalformedURLException e) {
            fail("The generated URL " + endpoint.get().getUrl() + " is invalid"); //verbose
        }
    }

    @Test
    public void testQueryByLabels() {
        final KnativeRouteEndpointDiscovery endpointDiscovery = new KnativeRouteEndpointDiscovery(null);
        endpointDiscovery.setKnativeClient(knativeClient);
        final Map<String, String> labels = Collections.singletonMap("app", "serverlessapp");

        // configure mock
        final RouteStatus status = new RouteStatus();
        status.setUrl("http://192.168.2.32");
        final Route route = new RouteBuilder()
                .withNewMetadata()
                .withLabels(labels)
                .withName("ksvc2").withNamespace("test")
                .and().withStatus(status)
                .build();
        knativeClient.routes().create(route);

        final List<Endpoint> endpoint = endpointDiscovery.findEndpoint("test", labels);
        assertFalse(endpoint.isEmpty());

        try {
            new URL(endpoint.get(0).getUrl());
        } catch (MalformedURLException e) {
            fail("The generated URL " + endpoint.get(0).getUrl() + " is invalid"); //verbose
        }
    }

    @Test
    public void testRouteWithoutStatus() {
        final KnativeRouteEndpointDiscovery endpointDiscovery = new KnativeRouteEndpointDiscovery(null);
        endpointDiscovery.setKnativeClient(knativeClient);

        // configure mock
        final Route route = new RouteBuilder().withNewMetadata().withName("ksvc3").withNamespace("test").and().build();
        knativeClient.routes().create(route);

        final Optional<Endpoint> endpoint = endpointDiscovery.findEndpoint("test", "ksvc3");
        assertTrue(endpoint.isEmpty());
    }
}

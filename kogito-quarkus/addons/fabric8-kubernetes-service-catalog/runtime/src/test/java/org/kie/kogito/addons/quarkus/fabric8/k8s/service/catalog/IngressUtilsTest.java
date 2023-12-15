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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test covers the queryIngressByName method from {@link IngressUtils}
 */
@QuarkusTest
@WithKubernetesTestServer
public class IngressUtilsTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    KubernetesResourceDiscovery discovery;

    private final String namespace = "serverless-workflow-greeting-quarkus";

    @Test
    public void testIngressNotFound() {
        Ingress ingress = mockServer.getClient()
                .network().v1().ingresses()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("ingress/ingress-with-ip.yaml")).item();
        mockServer.getClient().resource(ingress).inNamespace(namespace).createOrReplace();

        assertEquals(Optional.empty(),
                discovery.query(KubernetesResourceUri.parse("ingresses.v1.networking.k8s.io/" + namespace + "/invalid")));
    }

    @Test
    public void testIngressWithIP() {
        var kubeURI = KubernetesResourceUri.parse("ingresses.v1.networking.k8s.io/" + namespace + "/process-quarkus-ingress");

        Ingress ingress = mockServer.getClient().network().v1().ingresses().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("ingress/ingress-with-ip.yaml")).item();

        mockServer.getClient().resource(ingress).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("http://80.80.25.9:80", url.get());
    }

    @Test
    public void testIngressWithTLS() {
        var kubeURI = KubernetesResourceUri.parse("ingresses.v1.networking.k8s.io/" + namespace + "/hello-app-ingress-tls");

        Ingress ingress = mockServer.getClient().network().v1().ingresses().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("ingress/ingress-with-tls-and-host.yaml")).item();

        mockServer.getClient().resource(ingress).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("https://80.80.25.9:443", url.get());
    }
}

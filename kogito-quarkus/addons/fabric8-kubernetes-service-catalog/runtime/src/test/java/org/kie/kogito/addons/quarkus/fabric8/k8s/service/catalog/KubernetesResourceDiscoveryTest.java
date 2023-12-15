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

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This tests also covers the queryServiceByName method from {@link ServiceUtils}
 */
@QuarkusTest
@WithKubernetesTestServer
public class KubernetesResourceDiscoveryTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    KubernetesResourceDiscovery kubernetesResourceDiscovery;

    private final String namespace = "serverless-workflow-greeting-quarkus";

    @Test
    public void testServiceNodePort() {
        var kubeURI = KubernetesResourceUri.parse("services.v1/" + namespace + "/process-quarkus-example-pod-service");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).item();

        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testServiceNodePortCustomPortName() {
        var kubeURI = KubernetesResourceUri.parse("services.v1/" + namespace + "/custom-port-name-service?port-name=my-custom-port");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).item();
        service.getMetadata().setName("custom-port-name-service");
        service.getSpec().getPorts().get(0).setName("my-custom-port");
        service.getSpec().getPorts().get(0).setPort(8089);
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:8089", url.get());
    }

    @Test
    public void testServiceClusterIP() {
        var kubeURI = KubernetesResourceUri.parse("services.v1/" + namespace + "/process-quarkus-example-pod-clusterip-svc");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).item();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testServiceExternalName() {
        var kubeURI = KubernetesResourceUri.parse("services.v1/" + namespace + "/process-quarkus-example-pod");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-external-name.yaml")).item();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://my-public.domain.org:80", url.get());
    }

    @Test
    public void testNotFoundService() {
        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).item();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        assertEquals(Optional.empty(),
                kubernetesResourceDiscovery.query(KubernetesResourceUri.parse("services.v1/" + namespace + "/service-1")));
    }

    @Test
    public void testNotSupportedTypeService() {
        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).item();
        service.getSpec().setType(KubeConstants.LOAD_BALANCER_TYPE);
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        assertEquals(Optional.empty(),
                kubernetesResourceDiscovery.query(KubernetesResourceUri.parse("services.v1/" + namespace + "/process-quarkus-example-pod-clusterip-svc")));
    }

    @Test
    public void testServiceWithoutNamespace() {
        var kubeURI = KubernetesResourceUri.parse("services.v1/process-quarkus-example-pod-service");

        Service service = mockServer.getClient().services().inNamespace("test")
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).item();
        mockServer.getClient().resource(service).inNamespace("test").createOrReplace();

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testNotFoundKnativeService() {
        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        io.fabric8.knative.serving.v1.Service service = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).item();
        service.getMetadata().setName("test");

        // ItemWritableOperation#create is deprecated. However, we can't use the new method while Quarkus LTS is not greater than 2.16.
        knativeClient.services().inNamespace(namespace).create(service);

        assertEquals(Optional.empty(),
                kubernetesResourceDiscovery.query(KubernetesResourceUri.parse("services.v1.serving.knative.dev/" + namespace + "/invalid")));
    }

    @Test
    public void testKnativeService() {
        var kubeURI = KubernetesResourceUri.parse("services.v1.serving.knative.dev/" + namespace + "/serverless-workflow-greeting-quarkus");

        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        io.fabric8.knative.serving.v1.Service kService = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).item();

        // ItemWritableOperation#create is deprecated. However, we can't use the new method while Quarkus LTS is not greater than 2.16.
        knativeClient.services().inNamespace(namespace).create(kService);

        Optional<String> url = kubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io", url.get());
    }
}

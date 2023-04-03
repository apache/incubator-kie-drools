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
package org.kie.kogito.addons.quarkus.k8s.discovery;

import java.net.URI;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.k8s.KubeConstants;
import org.kie.kogito.addons.quarkus.k8s.discovery.utils.ServiceUtils;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This tests also covers the queryServiceByName method from {@link ServiceUtils}
 */
@QuarkusTest
@WithKubernetesTestServer
public class VanillaKubernetesResourceDiscoveryTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    VanillaKubernetesResourceDiscovery vanillaKubernetesResourceDiscovery;

    private final String namespace = "serverless-workflow-greeting-quarkus";

    @Test
    public void testServiceNodePort() {
        var kubeURI = VanillaKubernetesResourceUri.parse("v1/Service/" + namespace + "/process-quarkus-example-pod-service");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).get();

        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testServiceNodePortCustomPortName() {
        var kubeURI = VanillaKubernetesResourceUri.parse("v1/Service/" + namespace + "/custom-port-name-service?port-name=my-custom-port");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).get();
        service.getMetadata().setName("custom-port-name-service");
        service.getSpec().getPorts().get(0).setName("my-custom-port");
        service.getSpec().getPorts().get(0).setPort(8089);
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:8089", url.get());
    }

    @Test
    public void testServiceClusterIP() {
        var kubeURI = VanillaKubernetesResourceUri.parse("v1/service/" + namespace + "/process-quarkus-example-pod-clusterip-svc");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).get();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testServiceExternalName() {
        var kubeURI = VanillaKubernetesResourceUri.parse("v1/Service/" + namespace + "/process-quarkus-example-pod");

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-external-name.yaml")).get();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://my-public.domain.org:80", url.get());
    }

    @Test
    public void testNotFoundService() {
        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).get();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        assertEquals(Optional.empty(),
                vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse("v1/service/" + namespace + "/service-1")));
    }

    @Test
    public void testNotSupportedTypeService() {
        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-clusterip.yaml")).get();
        service.getSpec().setType(KubeConstants.LOAD_BALANCER_TYPE);
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        assertEquals(Optional.empty(),
                vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse("v1/service/" + namespace + "/process-quarkus-example-pod-clusterip-svc")));
    }

    @Test
    public void testServiceWithoutNamespace() {
        var kubeURI = VanillaKubernetesResourceUri.parse("v1/Service/process-quarkus-example-pod-service");

        Service service = mockServer.getClient().services().inNamespace("test")
                .load(this.getClass().getClassLoader().getResourceAsStream("service/service-node-port.yaml")).get();
        mockServer.getClient().resource(service).inNamespace("test").createOrReplace();

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.10:80", url.get());
    }

    @Test
    public void testNotFoundKnativeService() {
        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        io.fabric8.knative.serving.v1.Service service = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).get();
        service.getMetadata().setName("test");

        // ItemWritableOperation#create is deprecated. However, we can't use the new method while Quarkus LTS is not greater than 2.16.
        knativeClient.services().inNamespace(namespace).create(service);

        assertEquals(Optional.empty(),
                vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse("serving.knative.dev/v1/Service/" + namespace + "/invalid")));
    }

    @Test
    public void testKnativeService() {
        var kubeURI = VanillaKubernetesResourceUri.parse("serving.knative.dev/v1/Service/" + namespace + "/serverless-workflow-greeting-quarkus");

        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        io.fabric8.knative.serving.v1.Service kService = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).get();

        // ItemWritableOperation#create is deprecated. However, we can't use the new method while Quarkus LTS is not greater than 2.16.
        knativeClient.services().inNamespace(namespace).create(kService);

        Optional<String> url = vanillaKubernetesResourceDiscovery.query(kubeURI).map(URI::toString);
        assertEquals("http://serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io", url.get());
    }
}

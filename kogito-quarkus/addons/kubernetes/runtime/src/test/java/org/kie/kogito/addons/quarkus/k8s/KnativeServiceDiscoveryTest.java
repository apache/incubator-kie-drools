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
package org.kie.kogito.addons.quarkus.k8s;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@WithKubernetesTestServer
public class KnativeServiceDiscoveryTest {

    @KubernetesTestServer
    public KubernetesServer mockServer;

    @Inject
    KubernetesClient kubernetesClient;
    KubeResourceDiscovery kubeResourceDiscovery;
    private final String namespace = "test";

    @Test
    public void testNotFoundKnativeService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(kubernetesClient);
        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        Service service = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).get();
        service.getMetadata().setName("test");
        knativeClient.services().inNamespace(namespace).resource(service).create();
        assertEquals(Optional.empty(),
                kubeResourceDiscovery.query(new KubeURI("knative:serving.knative.dev/v1/Service/" + namespace + "/invalid")));
    }

    @Test
    public void testKnativeService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(kubernetesClient);
        KubeURI kubeURI = new KubeURI("knative:serving.knative.dev/v1/Service/" + namespace + "/serverless-workflow-greeting-quarkus");

        KnativeClient knativeClient = mockServer.getClient().adapt(KnativeClient.class);
        Service kService = knativeClient.services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("knative/quarkus-greeting.yaml")).get();
        knativeClient.services().inNamespace(namespace).resource(kService).create();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertEquals("http://serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io", url.get());
    }

}

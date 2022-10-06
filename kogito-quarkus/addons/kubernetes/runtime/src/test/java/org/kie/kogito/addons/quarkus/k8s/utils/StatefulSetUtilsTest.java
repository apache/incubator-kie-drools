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
package org.kie.kogito.addons.quarkus.k8s.utils;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.k8s.KubeResourceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;

import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This tests also covers the queryServiceByLabelOrSelector method from {@link ServiceUtils}
 * and queryPodByOwnerReference from {@link PodUtils}
 */
@QuarkusTest
@WithKubernetesTestServer
public class StatefulSetUtilsTest {

    @KubernetesTestServer
    KubernetesServer mockServer;
    KubeResourceDiscovery kubeResourceDiscovery;
    private final String namespace = "serverless-workflow-greeting-quarkus";

    @BeforeEach
    public void removeResources() {
        mockServer.getClient().apps().statefulSets().inNamespace(namespace).delete();
        mockServer.getClient().services().inNamespace(namespace).delete();
    }

    @Test
    public void testNotFoundStatefulSet() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-no-service.yaml")).get();
        statefulSet.getMetadata().setName("test");
        mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();
        assertEquals(Optional.empty(),
                kubeResourceDiscovery.query(new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/invalid")));
    }

    @Test
    public void testStatefulSetWithService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        KubeURI kubeURI = new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/example-statefulset-with-service");

        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset.yaml")).get();
        mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-service.yaml")).get();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertEquals("http://10.10.10.11:80", url.get());
    }

    @Test
    public void testStatefulSetWithServiceWithCustomPortName() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        KubeURI kubeURI = new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/custom-port-statefulset?port-name=my-custom-port-stateful");
        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset.yaml")).get();
        statefulSet.getMetadata().setName("custom-port-statefulset");
        statefulSet.getSpec().getTemplate().getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder().withName("test-port").withContainerPort(4000).build());
        statefulSet.getMetadata().getLabels().put("app-custom", "custom-port-statefulset");
        statefulSet.getMetadata().getLabels().remove("app");
        mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-service.yaml")).get();
        service.getMetadata().setName("custom-port-name-service-statefulset");
        service.getSpec().getPorts().add(new ServicePortBuilder()
                .withName("my-custom-port-stateful")
                .withTargetPort(new IntOrString("test-port"))
                .withPort(4009).build());
        service.getSpec().getSelector().put("app-custom", "custom-port-statefulset");
        service.getSpec().getSelector().remove("app");
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertEquals("http://10.10.10.11:4009", url.get());
    }

    @Test
    public void testStatefulSetNoService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        KubeURI kubeURI = new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/process-quarkus-example-statefulset-no-service");

        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-no-service.yaml")).get();
        StatefulSet createdDeployment = mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-pod-no-service.yaml")).get();
        pod.getMetadata().setName("pod-deployment-no-service");
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdDeployment.getMetadata().getUid());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertEquals("http://172.17.0.11:8080", url.get());
    }

    @Test
    public void testStatefulSetNoService2Replicas() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        KubeURI kubeURI = new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/example-statefulset-no-service-2-replicas");

        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-no-service.yaml")).get();
        statefulSet.getMetadata().setName("example-statefulset-no-service-2-replicas");
        statefulSet.getStatus().setReplicas(2);
        StatefulSet createdstatefulSet = mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-pod-no-service.yaml")).get();
        pod.getMetadata().setName("pod-2-replicas");
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdstatefulSet.getMetadata().getUid());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertTrue(url.isEmpty());
    }

    @Test
    public void testStatefulSetNoServiceCustomPortName() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getClient());
        KubeURI kubeURI = new KubeURI("kubernetes:apps/v1/statefulset/" + namespace + "/custom-port-statefulset-1?port-name=my-custom-port");

        StatefulSet statefulSet = mockServer.getClient().apps().statefulSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-no-service.yaml")).get();
        statefulSet.getMetadata().setName("custom-port-statefulset-1");
        statefulSet.getSpec().getTemplate().getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder().withName("test-port").withContainerPort(4000).build());
        StatefulSet createdStatefulSet = mockServer.getClient().resource(statefulSet).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("statefulset/statefulset-pod-no-service.yaml")).get();
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdStatefulSet.getMetadata().getUid());
        pod.getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder()
                        .withName("my-custom-port")
                        .withContainerPort(4010).build());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        assertEquals("http://172.17.0.11:4010", url.get());
    }
}

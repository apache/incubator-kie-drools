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

import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This tests also covers the queryServiceByLabelOrSelector method from {@link ServiceUtils}
 * and queryPodByOwnerReference from {@link PodUtils}
 */
@QuarkusTest
@WithKubernetesTestServer
public class DeploymentUtilsTest {

    @KubernetesTestServer
    KubernetesServer mockServer;

    @Inject
    KubernetesResourceDiscovery discovery;

    private final String namespace = "serverless-workflow-greeting-quarkus";

    @Test
    public void testNotFoundDeployment() {
        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment.yaml")).item();
        deployment.getMetadata().setName("test");
        mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();
        assertEquals(Optional.empty(),
                discovery.query(KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/invalid")));
    }

    @Test
    public void testDeploymentWithService() {
        var kubeURI = KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/example-deployment-with-service");

        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment.yaml")).item();
        mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment-service.yaml")).item();
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.11:80", url.get());
    }

    @Test
    public void testDeploymentWithServiceWithCustomPortName() {
        var kubeURI = KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/custom-port-deployment?port-name=my-custom-port");

        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment.yaml")).item();
        deployment.getMetadata().setName("custom-port-deployment");
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder().withName("test-port").withContainerPort(4000).build());
        mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();

        Service service = mockServer.getClient().services().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment-service.yaml")).item();
        service.getMetadata().setName("custom-port-name-service");
        service.getSpec().getPorts().add(new ServicePortBuilder()
                .withName("my-custom-port")
                .withTargetPort(new IntOrString("test-port"))
                .withPort(4009).build());
        mockServer.getClient().resource(service).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("http://10.10.10.11:4009", url.get());
    }

    @Test
    public void testDeploymentNoService() {
        var kubeURI = KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/example-deployment-no-service");

        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment-no-service.yaml")).item();
        Deployment createdDeployment = mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();

        ReplicaSet rs = mockServer.getClient().apps().replicaSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/replica-set-deployment-no-service.yaml")).item();
        rs.getMetadata().getOwnerReferences().get(0).setUid(createdDeployment.getMetadata().getUid());
        ReplicaSet createdRs = mockServer.getClient().resource(rs).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/pod-deployment-no-service.yaml")).item();
        pod.getMetadata().setName("pod-deployment-no-service");
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdRs.getMetadata().getUid());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("http://172.17.0.11:8080", url.get());
    }

    @Test
    public void testDeploymentNoService2Replicas() {
        var kubeURI = KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/example-deployment-no-service-2-replicas");

        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment-no-service.yaml")).item();
        deployment.getMetadata().setName("example-deployment-no-service-2-replicas");
        deployment.getStatus().setReplicas(2);
        Deployment createdDeployment = mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();

        ReplicaSet rs = mockServer.getClient().apps().replicaSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/replica-set-deployment-no-service.yaml")).item();
        rs.getMetadata().setName("rs-2-replicas");
        rs.getMetadata().getOwnerReferences().get(0).setUid(createdDeployment.getMetadata().getUid());
        ReplicaSet createdRs = mockServer.getClient().resource(rs).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/pod-deployment-no-service.yaml")).item();
        pod.getMetadata().setName("pod-2-replicas");
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdRs.getMetadata().getUid());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertTrue(url.isEmpty());
    }

    @Test
    public void testDeploymentNoServiceCustomPortName() {
        var kubeURI = KubernetesResourceUri.parse("deployments.v1.apps/" + namespace + "/custom-port-deployment-1?port-name=my-custom-port");

        Deployment deployment = mockServer.getClient().apps().deployments().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/deployment-no-service.yaml")).item();
        deployment.getMetadata().setName("custom-port-deployment-1");
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder().withName("test-port").withContainerPort(4000).build());
        Deployment createdDeployment = mockServer.getClient().resource(deployment).inNamespace(namespace).createOrReplace();

        ReplicaSet rs = mockServer.getClient().apps().replicaSets().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/replica-set-deployment-no-service.yaml")).item();
        rs.getMetadata().setName("custom-port-rs");
        rs.getMetadata().getOwnerReferences().get(0).setUid(createdDeployment.getMetadata().getUid());
        ReplicaSet createdRs = mockServer.getClient().resource(rs).inNamespace(namespace).createOrReplace();

        Pod pod = mockServer.getClient().pods().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deployment/pod-deployment-no-service.yaml")).item();
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdRs.getMetadata().getUid());
        pod.getSpec().getContainers().get(0).getPorts()
                .add(new ContainerPortBuilder()
                        .withName("my-custom-port")
                        .withContainerPort(4009).build());
        mockServer.getClient().resource(pod).inNamespace(namespace).createOrReplace();

        Optional<String> url = discovery.query(kubeURI).map(URI::toString);
        assertEquals("http://172.17.0.11:4009", url.get());
    }
}

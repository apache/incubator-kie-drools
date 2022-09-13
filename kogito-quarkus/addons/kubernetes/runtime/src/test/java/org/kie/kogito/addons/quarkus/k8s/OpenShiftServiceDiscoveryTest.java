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

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.OpenShiftTestServer;
import io.quarkus.test.kubernetes.client.WithOpenShiftTestServer;

@QuarkusTest
@WithOpenShiftTestServer
public class OpenShiftServiceDiscoveryTest {

    @OpenShiftTestServer
    OpenShiftServer mockServer;
    KubeResourceDiscovery kubeResourceDiscovery;
    private final String namespace = "serverless-workflow-greeting-quarkus";

    @BeforeEach
    public void removeResources() {
        mockServer.getOpenshiftClient().deploymentConfigs().inNamespace(namespace).delete();
        mockServer.getOpenshiftClient().routes().inNamespace(namespace).delete();
    }

    @Test
    public void testNotFoundDeploymentConfig() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getOpenshiftClient());
        DeploymentConfig deployment = mockServer.getOpenshiftClient().deploymentConfigs().inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/deployment-config.yaml")).get();
        deployment.getMetadata().setName("test");
        mockServer.getOpenshiftClient().deploymentConfigs().inNamespace(namespace).create(deployment);
        Assertions.assertEquals(Optional.empty(),
                kubeResourceDiscovery.query(new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/" + namespace + "/invalid")));
    }

    @Test
    public void testDeploymentConfigWithService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getKubernetesClient());
        KubeURI kubeURI = new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/" + namespace + "/example-dc-with-service");

        DeploymentConfig deploymentConfig = mockServer.getOpenshiftClient()
                .deploymentConfigs()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/deployment-config.yaml")).get();
        mockServer.getOpenshiftClient().deploymentConfigs().inNamespace(namespace).create(deploymentConfig);

        Service service = mockServer.getOpenshiftClient()
                .services()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/deployment-config-service.yaml")).get();
        mockServer.getOpenshiftClient().services().inNamespace(namespace).create(service);

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        Assert.assertEquals("http://10.10.10.12:80", url.get());
    }

    @Test
    public void testDeploymentConfigWithoutService() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getKubernetesClient());
        KubeURI kubeURI = new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/" + namespace + "/example-dc-no-service");

        DeploymentConfig deploymentConfig = mockServer.getOpenshiftClient()
                .deploymentConfigs()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/deployment-config-no-service.yaml")).get();
        DeploymentConfig createdDc = mockServer.getOpenshiftClient().deploymentConfigs().inNamespace(namespace).create(deploymentConfig);

        ReplicationController rc = mockServer.getOpenshiftClient()
                .replicationControllers()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/replication-controller-dc-no-svc.yaml")).get();
        rc.getMetadata().getOwnerReferences().get(0).setUid(createdDc.getMetadata().getUid());
        ReplicationController createdRc = mockServer.getOpenshiftClient().replicationControllers().inNamespace(namespace).create(rc);

        Pod pod = mockServer.getOpenshiftClient()
                .pods()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("deploymentConfig/pod-deployment-config-no-service.yaml")).get();
        pod.getMetadata().setName("example-dc-no-service-1-phlx4");
        pod.getMetadata().getOwnerReferences().get(0).setUid(createdRc.getMetadata().getUid());
        mockServer.getOpenshiftClient().pods().inNamespace(namespace).create(pod);

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        Assert.assertEquals("http://172.17.25.190:8080", url.get());
    }

    @Test
    public void testNotFoundRoute() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getKubernetesClient());

        Route route = mockServer.getOpenshiftClient()
                .routes()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("route/route.yaml")).get();
        mockServer.getOpenshiftClient().routes().inNamespace(namespace).create(route);

        Assertions.assertEquals(Optional.empty(),
                kubeResourceDiscovery.query(new KubeURI("openshift:route.openshift.io/v1/route/" + namespace + "/invalid")));
    }

    @Test
    public void testRoute() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getKubernetesClient());
        KubeURI kubeURI = new KubeURI("openshift:route.openshift.io/v1/route/" + namespace + "/test-route");

        Route route = mockServer.getOpenshiftClient()
                .routes()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("route/route.yaml")).get();
        mockServer.getOpenshiftClient().routes().inNamespace(namespace).create(route);

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        Assert.assertEquals("http://test-route.org:80", url.get());
    }

    @Test
    public void testRouteTLS() {
        kubeResourceDiscovery = new KubeResourceDiscovery(mockServer.getKubernetesClient());
        KubeURI kubeURI = new KubeURI("openshift:route.openshift.io/v1/route/" + namespace + "/test-route-tls");

        Route route = mockServer.getOpenshiftClient()
                .routes()
                .inNamespace(namespace)
                .load(this.getClass().getClassLoader().getResourceAsStream("route/route-tls.yaml")).get();
        mockServer.getOpenshiftClient().routes().inNamespace(namespace).create(route);

        Optional<String> url = kubeResourceDiscovery.query(kubeURI);
        Assert.assertEquals("https://secure-test-route-tls:443", url.get());
    }
}

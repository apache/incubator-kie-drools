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
package org.kie.kogito.addons.quarkus.k8s.discovery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.DEPLOYMENT;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.DEPLOYMENT_CONFIG;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.INGRESS;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.KNATIVE_SERVICE;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.POD;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.ROUTE;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.SERVICE;
import static org.kie.kogito.addons.quarkus.k8s.discovery.GVK.STATEFUL_SET;

public class KubeURIParserGVKTest {

    @Test
    public void testValidGVK() {
        VanillaKubernetesResourceUri address = VanillaKubernetesResourceUri.parse("deployments.v1.apps/default/kogito-app-1");
        assertThat(address.getGvk()).isEqualTo(DEPLOYMENT);
        Assertions.assertEquals("kogito-app-1", address.getResourceName());

        VanillaKubernetesResourceUri url1 = VanillaKubernetesResourceUri.parse("deploymentconfigs.v1.apps.openshift.io/default/kogito-app-1");
        assertThat(url1.getGvk()).isEqualTo(DEPLOYMENT_CONFIG);
        Assertions.assertEquals("kogito-app-1", url1.getResourceName());

        VanillaKubernetesResourceUri address2 = VanillaKubernetesResourceUri.parse("statefulsets.v1.apps/namespace2/kogito-app-1");
        Assertions.assertEquals(STATEFUL_SET, address2.getGvk());
        Assertions.assertEquals("kogito-app-1", address2.getResourceName());

        VanillaKubernetesResourceUri address3 = VanillaKubernetesResourceUri.parse("statefulsets.v1.apps/namespace2/kogito-app-1");
        Assertions.assertEquals(STATEFUL_SET, address3.getGvk());
        Assertions.assertEquals("kogito-app-1", address3.getResourceName());

        VanillaKubernetesResourceUri address4 = VanillaKubernetesResourceUri.parse("services.v1/namespace2/kogito-app-1");
        Assertions.assertEquals(SERVICE, address4.getGvk());
        Assertions.assertEquals("kogito-app-1", address4.getResourceName());

        VanillaKubernetesResourceUri address5 = VanillaKubernetesResourceUri.parse("services.v1/namespace2/kogito-app-1");
        Assertions.assertEquals(SERVICE, address5.getGvk());
        Assertions.assertEquals("kogito-app-1", address5.getResourceName());

        VanillaKubernetesResourceUri address6 = VanillaKubernetesResourceUri.parse("routes.v1.route.openshift.io/namespace10/kogito-app-1");
        Assertions.assertEquals(ROUTE, address6.getGvk());
        Assertions.assertEquals("kogito-app-1", address6.getResourceName());

        VanillaKubernetesResourceUri address7 = VanillaKubernetesResourceUri.parse("ingresses.v1.networking.k8s.io/namespace9/kogito-app-1");
        Assertions.assertEquals(INGRESS, address7.getGvk());
        Assertions.assertEquals("kogito-app-1", address7.getResourceName());

        VanillaKubernetesResourceUri address8 = VanillaKubernetesResourceUri.parse("pods.v1/namespace9/kogito-app-1");
        Assertions.assertEquals(POD, address8.getGvk());
        Assertions.assertEquals("kogito-app-1", address8.getResourceName());

        VanillaKubernetesResourceUri url9 = VanillaKubernetesResourceUri.parse("deploymentconfigs.v1.apps.openshift.io/default/kogito-app-1");
        Assertions.assertEquals(DEPLOYMENT_CONFIG, url9.getGvk());
        Assertions.assertEquals("kogito-app-1", url9.getResourceName());

        VanillaKubernetesResourceUri url10 = VanillaKubernetesResourceUri.parse("services.v1.serving.knative.dev/default/knative-app-1");
        Assertions.assertEquals(KNATIVE_SERVICE, url10.getGvk());
        Assertions.assertEquals("knative-app-1", url10.getResourceName());
    }

    @Test
    public void testInvalidGVK() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("authorization.openshift.io/v1/roleBinding/default/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("v40/invalid/deployment/default/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("authorization.openshift.io/v1/roleBinding/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("deploymentconfigs.v1.apps.openshift.ios/default/kogito-app2");
        });
    }

    @Test
    public void testValidGVKWithNoNamespace() {
        VanillaKubernetesResourceUri url = VanillaKubernetesResourceUri.parse("deployments.v1.apps/kogito-app-1");
        Assertions.assertEquals(DEPLOYMENT, url.getGvk());
        Assertions.assertEquals("kogito-app-1", url.getResourceName());

        VanillaKubernetesResourceUri url1 = VanillaKubernetesResourceUri.parse("services.v1/kogito-app-2");
        Assertions.assertEquals(SERVICE, url1.getGvk());
        Assertions.assertEquals("kogito-app-2", url1.getResourceName());

        VanillaKubernetesResourceUri url2 = VanillaKubernetesResourceUri.parse("services.v1/kogito-app-2");
        Assertions.assertEquals(SERVICE, url2.getGvk());
        Assertions.assertEquals("kogito-app-2", url2.getResourceName());

        VanillaKubernetesResourceUri url3 = VanillaKubernetesResourceUri.parse("deploymentconfigs.v1.apps.openshift.io/kogito-app-3");
        Assertions.assertEquals(DEPLOYMENT_CONFIG, url3.getGvk());
        Assertions.assertEquals("kogito-app-3", url3.getResourceName());

    }

    @Test
    public void testEmptyGVKValues() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("kubernetes:roleBinding/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            VanillaKubernetesResourceUri.parse("apps.openshift.io/deploymentconfig/kogito-app-3");
        });
    }
}

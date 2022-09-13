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
package org.kie.kogito.addons.quarkus.k8s.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KubeURIParserGVKTest {

    @Test
    public void testValidGVK() {
        KubeURI url = new KubeURI("kubernetes:apps/v1/deployment/default/kogito-app-1");
        Assertions.assertEquals("apps/v1/deployment", url.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url.getResourceName());

        KubeURI url1 = new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/default/kogito-app-1");
        Assertions.assertEquals("apps.openshift.io/v1/deploymentconfig", url1.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url1.getResourceName());

        KubeURI url2 = new KubeURI("kubernetes:apps/v1/statefulset/namespace2/kogito-app-1");
        Assertions.assertEquals("apps/v1/statefulset", url2.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url2.getResourceName());

        KubeURI url3 = new KubeURI("kubernetes:apps/v1/statefulset/namespace2/kogito-app-1");
        Assertions.assertEquals("apps/v1/statefulset", url3.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url3.getResourceName());

        KubeURI url4 = new KubeURI("kubernetes:v1/Service/namespace2/kogito-app-1");
        Assertions.assertEquals("v1/service", url4.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url4.getResourceName());

        KubeURI url5 = new KubeURI("kubernetes:v1/service/namespace2/kogito-app-1");
        Assertions.assertEquals("v1/service", url5.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url5.getResourceName());

        KubeURI url6 = new KubeURI("kubernetes:route.openshift.io/v1/route/namespace10/kogito-app-1");
        Assertions.assertEquals("route.openshift.io/v1/route", url6.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url6.getResourceName());

        KubeURI url7 = new KubeURI("kubernetes:networking.k8s.io/v1/ingress/namespace9/kogito-app-1");
        Assertions.assertEquals("networking.k8s.io/v1/ingress", url7.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url7.getResourceName());

        KubeURI url8 = new KubeURI("kubernetes:v1/pod/namespace9/kogito-app-1");
        Assertions.assertEquals("v1/pod", url8.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url8.getResourceName());

        KubeURI url9 = new KubeURI("openshift:apps.openshift.io/v1/deploymentConfig/default/kogito-app-1");
        Assertions.assertEquals("apps.openshift.io/v1/deploymentconfig", url9.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url9.getResourceName());

        KubeURI url10 = new KubeURI("openshift:serving.knative.dev/v1/service/default/knative-app-1");
        Assertions.assertEquals("serving.knative.dev/v1/service", url10.getGvk().getGVK());
        Assertions.assertEquals("knative-app-1", url10.getResourceName());
    }

    @Test
    public void testInvalidGVK() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("kubernetes:authorization.openshift.io/v1/roleBinding/default/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("kubernetes:v40/invalid/deployment/default/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("kubernetes:authorization.openshift.io/v1/roleBinding/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("openshift:apps.openshift.io/v1/deploymentconfigs/default/kogito-app2");
        });
    }

    @Test
    public void testValidGVKWithNoNamespace() {
        KubeURI url = new KubeURI("kubernetes:apps/v1/deployment/kogito-app-1");
        Assertions.assertEquals("apps/v1/deployment", url.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-1", url.getResourceName());

        KubeURI url1 = new KubeURI("kubernetes:v1/Service/kogito-app-2");
        Assertions.assertEquals("v1/service", url1.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-2", url1.getResourceName());

        KubeURI url2 = new KubeURI("openshift:v1/Service/kogito-app-2");
        Assertions.assertEquals("v1/service", url2.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-2", url2.getResourceName());

        KubeURI url3 = new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/kogito-app-3");
        Assertions.assertEquals("apps.openshift.io/v1/deploymentconfig", url3.getGvk().getGVK());
        Assertions.assertEquals("kogito-app-3", url3.getResourceName());

    }

    @Test
    public void testEmptyGVKValues() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("kubernetes:roleBinding/kogito-app-3");
        });

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("openshift:apps.openshift.io/deploymentconfig/kogito-app-3");
        });
    }
}

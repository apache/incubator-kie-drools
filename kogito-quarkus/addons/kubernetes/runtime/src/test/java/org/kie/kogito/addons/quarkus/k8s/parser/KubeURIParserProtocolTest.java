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

public class KubeURIParserProtocolTest {

    @Test
    public void testK8sProtocol() {
        KubeURI url = new KubeURI("kubernetes:apps/v1/deployment/default/kogito-app-1");
        Assertions.assertEquals("kubernetes", url.getProtocol());
    }

    @Test
    public void testKnativeProtocol() {
        KubeURI url = new KubeURI("knative:apps/v1/deployment/default/kogito-app2");
        Assertions.assertEquals("knative", url.getProtocol());
    }

    @Test
    public void testOCPProtocol() {
        KubeURI url = new KubeURI("openshift:apps.openshift.io/v1/deploymentconfig/default/kogito-app2");
        Assertions.assertEquals("openshift", url.getProtocol());
    }

    @Test()
    public void testInvalidProtocol() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI("ocp:apps/v1/deployment/default/kogito-app-3");
        });
    }

    @Test
    public void testFullURL() {
        KubeURI url = new KubeURI("kubernetes:apps/v1/deployment/default/kogito-app-3");
        Assertions.assertEquals("kubernetes", url.getProtocol());
    }

    @Test
    public void testEmptyProtocol() {
        Assertions.assertThrowsExactly(ArrayIndexOutOfBoundsException.class, () -> {
            new KubeURI("apps/v1/deployment/default/kogito-app-1");
        });
    }

    @Test
    public void testEmptyProtocolWithDoubleDots() {
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
            new KubeURI(":apps/v1/deployment/default/kogito-app-1");
        });
    }
}

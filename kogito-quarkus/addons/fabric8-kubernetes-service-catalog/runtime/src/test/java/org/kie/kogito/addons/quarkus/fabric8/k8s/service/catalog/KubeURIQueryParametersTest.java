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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KubeURIQueryParametersTest {

    @Test
    public void testQueryParameterCustomPortName() {
        StringBuilder kubernetesKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/process-quarkus-example-pod-service");
        StringBuilder knativeKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/greeting-quarkus-cli");
        StringBuilder ocpKubeURIBuilder = new StringBuilder("deploymentconfigs.v1.apps.openshift.io/serverless-workflow-greeting-quarkus/example-deployment-no-service");

        kubernetesKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-kubernetes");
        var k8sUrl = KubernetesResourceUri.parse(kubernetesKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-kubernetes", k8sUrl.getCustomPortName());

        knativeKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=").append("my-special-port-knative");
        var knativeUrl = KubernetesResourceUri.parse(knativeKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-knative", knativeUrl.getCustomPortName());

        ocpKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-ocp");
        var ocpUrl = KubernetesResourceUri.parse(ocpKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-ocp", ocpUrl.getCustomPortName());
    }

    @Test
    public void testQueryParameterCustomPortNameAndNumber() {
        StringBuilder kubernetesKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/process-quarkus-example-pod-service");
        StringBuilder knativeKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/greeting-quarkus-cli");
        StringBuilder ocpKubeURIBuilder = new StringBuilder("deploymentconfigs.v1.apps.openshift.io/serverless-workflow-greeting-quarkus/example-deployment-no-service");

        kubernetesKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-kubernetes")
                .append("&")
                .append("=")
                .append("9000");
        var k8sUrl = KubernetesResourceUri.parse(kubernetesKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-kubernetes", k8sUrl.getCustomPortName());

        knativeKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=").append("my-special-port-knative")
                .append("&")
                .append("=")
                .append("9001");
        var knativeUrl = KubernetesResourceUri.parse(knativeKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-knative", knativeUrl.getCustomPortName());

        ocpKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-ocp")
                .append("&")
                .append("=")
                .append("9002");
        var ocpUrl = KubernetesResourceUri.parse(ocpKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-ocp", ocpUrl.getCustomPortName());
    }

    @Test
    public void testQueryParameterCustomLabel() {
        StringBuilder kubernetesKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/process-quarkus-example-pod-service");
        StringBuilder knativeKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/greeting-quarkus-cli");
        StringBuilder ocpKubeURIBuilder = new StringBuilder("deploymentconfigs.v1.apps.openshift.io/serverless-workflow-greeting-quarkus/example-deployment-no-service");

        kubernetesKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("kubernetes-label")
                .append("=")
                .append("my-special-label-kubernetes");
        var k8sUrl = KubernetesResourceUri.parse(kubernetesKubeURIBuilder.toString());
        Assertions.assertEquals(Collections.singletonMap("kubernetes-label", "my-special-label-kubernetes"), k8sUrl.getCustomLabel());

        knativeKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("knative-label")
                .append("=")
                .append("my-special-label-knative");
        var knativeUrl = KubernetesResourceUri.parse(knativeKubeURIBuilder.toString());
        Assertions.assertEquals(Collections.singletonMap("knative-label", "my-special-label-knative"), knativeUrl.getCustomLabel());

        ocpKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("ocp-label")
                .append("=")
                .append("my-special-label-ocp");
        var ocpUrl = KubernetesResourceUri.parse(ocpKubeURIBuilder.toString());
        Assertions.assertEquals(Collections.singletonMap("ocp-label", "my-special-label-ocp"), ocpUrl.getCustomLabel());
    }

    @Test
    public void testQueryParameterAllCustomParameters() {
        StringBuilder kubernetesKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/process-quarkus-example-pod-service");
        StringBuilder knativeKubeURIBuilder = new StringBuilder("services.v1/serverless-workflow-greeting-quarkus/greeting-quarkus-cli");
        StringBuilder ocpKubeURIBuilder = new StringBuilder("deploymentconfigs.v1.apps.openshift.io/serverless-workflow-greeting-quarkus/example-deployment-no-service");

        kubernetesKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-kubernetes")
                .append("&")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("my-custom-label")
                .append("=")
                .append("my-special-label-kubernetes")
                .append(";")
                .append("my-other-custom-label")
                .append("=")
                .append("my-other- special-label-kubernetes");

        var k8sUrl = KubernetesResourceUri.parse(kubernetesKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-kubernetes", k8sUrl.getCustomPortName());
        Assertions.assertEquals(Map.of(
                "my-custom-label", "my-special-label-kubernetes",
                "my-other-custom-label", "my-other- special-label-kubernetes"),
                k8sUrl.getCustomLabel());

        knativeKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=").append("my-special-port-knative")
                .append("&")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("knative-label")
                .append("=")
                .append("my-special-label-knative");
        ;
        var knativeUrl = KubernetesResourceUri.parse(knativeKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-knative", knativeUrl.getCustomPortName());
        Assertions.assertEquals(Collections.singletonMap("knative-label", "my-special-label-knative"), knativeUrl.getCustomLabel());

        ocpKubeURIBuilder
                .append("?")
                .append(KubeConstants.CUSTOM_PORT_NAME_PROPERTY)
                .append("=")
                .append("my-special-port-ocp")
                .append("&")
                .append(KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY)
                .append("=")
                .append("ocp-label")
                .append("=")
                .append("my-special-label-ocp");
        var ocpUrl = KubernetesResourceUri.parse(ocpKubeURIBuilder.toString());
        Assertions.assertEquals("my-special-port-ocp", ocpUrl.getCustomPortName());
        Assertions.assertEquals(Collections.singletonMap("ocp-label", "my-special-label-ocp"), ocpUrl.getCustomLabel());
    }

}

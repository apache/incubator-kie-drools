/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client;

import java.net.MalformedURLException;
import java.net.URL;

import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.OperationSupport;
import okhttp3.OkHttpClient;

/**
 * Wraps the Kubernetes Client setup and configuration, exposing infrastructure components to connect to a Kubernetes cluster reliably. 
 * Most used when there's a need to customize the Fabric8 Kubernetes Client.
 */
public final class KogitoKubeConfig {

    public static final String KNATIVE_ISTIO_NAMESPACE = "istio-system";
    private static final String NAMESPACE_REPLACE = "@ns@";
    private static final String KNATIVE_SERVICE_SERVICE_URL = "apis/serving.knative.dev/v1alpha1/namespaces/" + NAMESPACE_REPLACE + "/services";
    private static final String KNATIVE_ISTIO_GATEWAY_URL = "api/v1/namespaces/" + KNATIVE_ISTIO_NAMESPACE + "/services/istio-ingressgateway";

    private KubernetesClient kubernetesClient;

    public KogitoKubeConfig() {
        // disable kube config file deserialization due to reflection usage
        System.setProperty(Config.KUBERNETES_AUTH_TRYKUBECONFIG_SYSTEM_PROPERTY, "false");
        this.kubernetesClient = new DefaultKubernetesClient();
    }

    /**
     * Highly customizable config client. Most used in integration tests. 90% of the time you won't need to use this constructor.
     * 
     * @param kubernetesClient
     */
    public KogitoKubeConfig(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public OkHttpClient getHttpClient() {
        return ((BaseClient) this.kubernetesClient).getHttpClient();
    }

    public URL getMasterUrl() {
        return this.kubernetesClient.getMasterUrl();
    }

    public String getKNativeServiceServiceURL(String namespace) throws MalformedURLException {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("A namespace should be provided when using KNative service operations");
        }
        final StringBuilder sb = new StringBuilder(this.getMasterUrl().toString());
        sb.append(KNATIVE_SERVICE_SERVICE_URL.replaceFirst(NAMESPACE_REPLACE, namespace));
        return sb.toString();
    }

    public String getKNativeIstioGatewayURL() throws MalformedURLException {
        final StringBuilder sb = new StringBuilder(this.getMasterUrl().toString());
        sb.append(KNATIVE_ISTIO_GATEWAY_URL);
        return sb.toString();
    }

    public URL getServiceOperationURL(final String namespace) throws MalformedURLException {
        final OperationSupport services = ((OperationSupport) this.kubernetesClient.services());
        return services.getNamespacedUrl(namespace);
    }

}

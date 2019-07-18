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
package org.kie.kogito.cloud.workitems.service.discovery;

import org.kie.kogito.cloud.kubernetes.client.DefaultKogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceDiscoveryFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscoveryFactory.class);

    private static ServiceDiscoveryFactory factory;
    private KogitoKubeClient kubeClient;
    private boolean istioEnv;
    private String istioGatewayUrl;

    private ServiceDiscoveryFactory(final KogitoKubeClient kubeClient) {
        this.kubeClient = kubeClient;
        this.discoverIstioGatewayUrl();
    }

    public static ServiceDiscovery build() {
        return build(new DefaultKogitoKubeClient());
    }

    public static ServiceDiscovery build(final KogitoKubeClient kubeClient) {
        factory = new ServiceDiscoveryFactory(kubeClient);
        if (factory.isIstioEnv()) {
            return new IstioServiceDiscovery(kubeClient, factory.getIstioGatewayUrl());
        } else {
            return new KubernetesServiceDiscovery(kubeClient);
        }
    }

    public boolean isIstioEnv() {
        return istioEnv;
    }

    public String getIstioGatewayUrl() {
        return istioGatewayUrl;
    }

    private void discoverIstioGatewayUrl() {
        LOGGER.debug("Trying to discover Istio Gateway URL");
        try {
            final String clusterIp =
                    (String) kubeClient.istioGateway()
                                       .get()
                                       .asMapWalker(true)
                                       .mapToMap(BaseServiceDiscovery.KEY_SPEC)
                                       .asMap()
                                       .get(BaseServiceDiscovery.KEY_CLUSTER_IP);
            if (clusterIp == null || clusterIp.isEmpty()) {
                LOGGER.info("Not in Istio environment");
                this.istioEnv = false;
                this.istioGatewayUrl = null;
            } else {
                this.istioEnv = true;
                this.istioGatewayUrl =
                        new StringBuilder()
                                           .append(BaseServiceDiscovery.DEFAULT_PROTOCOL)
                                           .append(clusterIp)
                                           .append(":")
                                           .append(BaseServiceDiscovery.DEFAULT_PORT)
                                           .append("/")
                                           .toString();
                LOGGER.info("Discovered Istio Gateway URL {}. Will use Istio as default service discovery mechanism", this.istioGatewayUrl);
            }
        } catch (Exception ex) {
            this.istioEnv = false;
            this.istioGatewayUrl = null;
            LOGGER.warn("Failed to look up for Istio Gateway URL: '{}'. Enable debug logging to view the full stack trace. Failing back to standard Service API.", ex.getMessage());
            LOGGER.debug("Error while trying to fetch for Istio Gateway URL", ex);
        }
    }
}

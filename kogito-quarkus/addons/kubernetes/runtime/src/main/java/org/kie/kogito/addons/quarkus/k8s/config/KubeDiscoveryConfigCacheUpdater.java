/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.net.URI;
import java.util.Optional;

import org.kie.kogito.addons.quarkus.k8s.KubernetesProtocol;
import org.kie.kogito.addons.quarkus.k8s.discovery.KnativeServiceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.KnativeServiceUri;
import org.kie.kogito.addons.quarkus.k8s.discovery.OpenShiftResourceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.VanillaKubernetesResourceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.VanillaKubernetesResourceUri;
import org.slf4j.LoggerFactory;

class KubeDiscoveryConfigCacheUpdater {

    private final VanillaKubernetesResourceDiscovery vanillaKubernetesResourceDiscovery;

    private final OpenShiftResourceDiscovery openShiftResourceDiscovery;

    private final KnativeServiceDiscovery knativeServiceDiscovery;

    KubeDiscoveryConfigCacheUpdater(VanillaKubernetesResourceDiscovery vanillaKubernetesResourceDiscovery,
            OpenShiftResourceDiscovery openShiftResourceDiscovery, KnativeServiceDiscovery knativeServiceDiscovery) {
        this.vanillaKubernetesResourceDiscovery = vanillaKubernetesResourceDiscovery;
        this.openShiftResourceDiscovery = openShiftResourceDiscovery;
        this.knativeServiceDiscovery = knativeServiceDiscovery;
    }

    Optional<URI> update(String rawAddress) {
        String[] protoAndValues = rawAddress.split(":");
        if (protoAndValues.length <= 1) {
            LoggerFactory.getLogger(KubeDiscoveryConfigCacheUpdater.class.getName())
                    .error("the provided URI {} is not valid", rawAddress);
        }

        KubernetesProtocol protocol = KubernetesProtocol.from(protoAndValues[0]);

        switch (protocol) {
            case VANILLA_KUBERNETES:
                return vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse(protoAndValues[1]));
            case OPENSHIFT:
                return openShiftResourceDiscovery.query(VanillaKubernetesResourceUri.parse(protoAndValues[1]));
            case KNATIVE:
                String[] splitValues = protoAndValues[1].split("/");

                switch (splitValues.length) {
                    case 1:
                        return knativeServiceDiscovery.query(new KnativeServiceUri(null, splitValues[0]));
                    case 2:
                        return knativeServiceDiscovery.query(new KnativeServiceUri(splitValues[0], splitValues[1]));
                    default:
                        return vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse(protoAndValues[1]));
                }
            default:
                throw new UnsupportedOperationException("Unsupported protocol: " + protocol);
        }
    }
}

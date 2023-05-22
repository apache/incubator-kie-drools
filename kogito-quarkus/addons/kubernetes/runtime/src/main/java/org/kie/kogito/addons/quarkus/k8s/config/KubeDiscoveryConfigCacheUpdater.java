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

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalog;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalogKey;
import org.slf4j.LoggerFactory;

class KubeDiscoveryConfigCacheUpdater {

    private final KubernetesServiceCatalog kubernetesServiceCatalog;

    KubeDiscoveryConfigCacheUpdater(KubernetesServiceCatalog kubernetesServiceCatalog) {
        this.kubernetesServiceCatalog = kubernetesServiceCatalog;
    }

    Optional<URI> update(String rawAddress) {
        return kubernetesServiceCatalog.getServiceAddress(createServiceCatalogKey(rawAddress));
    }

    private static KubernetesServiceCatalogKey createServiceCatalogKey(String rawAddress) {
        String[] protoAndValues = rawAddress.split(":");
        if (protoAndValues.length <= 1) {
            LoggerFactory.getLogger(KubeDiscoveryConfigCacheUpdater.class.getName())
                    .error("the provided URI {} is not valid", rawAddress);
        }

        KubernetesProtocol protocol = KubernetesProtocol.from(protoAndValues[0]);
        String coordinates = protoAndValues[1];

        return new KubernetesServiceCatalogKey(protocol, coordinates);
    }
}

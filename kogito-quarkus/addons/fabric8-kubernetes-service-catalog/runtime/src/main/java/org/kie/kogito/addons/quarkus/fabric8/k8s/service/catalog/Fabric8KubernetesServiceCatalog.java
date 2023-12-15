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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalog;
import org.kie.kogito.addons.k8s.resource.catalog.KubernetesServiceCatalogKey;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
final class Fabric8KubernetesServiceCatalog implements KubernetesServiceCatalog {

    private final Map<KubernetesServiceCatalogKey, URI> services = new ConcurrentHashMap<>();

    private final KnativeServiceDiscovery knativeServiceDiscovery;

    private final KubernetesResourceDiscovery kubernetesResourceDiscovery;

    private final OpenShiftResourceDiscovery openShiftResourceDiscovery;

    @Inject
    Fabric8KubernetesServiceCatalog(KnativeServiceDiscovery knativeServiceDiscovery, KubernetesResourceDiscovery kubernetesResourceDiscovery,
            OpenShiftResourceDiscovery openShiftResourceDiscovery) {
        this.knativeServiceDiscovery = knativeServiceDiscovery;
        this.kubernetesResourceDiscovery = kubernetesResourceDiscovery;
        this.openShiftResourceDiscovery = openShiftResourceDiscovery;
    }

    @Override
    public Optional<URI> getServiceAddress(KubernetesServiceCatalogKey key) {
        Function<String, Optional<URI>> function;

        switch (key.getProtocol()) {
            case KNATIVE:
                String[] splitCoordinates = key.getCoordinates().split("/");

                if (splitCoordinates.length == 1) {
                    function = coordinates -> knativeServiceDiscovery.query(new KnativeServiceUri(null, coordinates));
                } else if (GVK.isValid(splitCoordinates[0])) {
                    function = coordinates -> kubernetesResourceDiscovery.query(KubernetesResourceUri.parse(coordinates));
                } else {
                    function = coordinates -> knativeServiceDiscovery.query(new KnativeServiceUri(splitCoordinates[0], splitCoordinates[1]));
                }
                break;
            case OPENSHIFT:
                function = coordinates -> openShiftResourceDiscovery.query(KubernetesResourceUri.parse(coordinates));
                break;
            case KUBERNETES:
                function = coordinates -> kubernetesResourceDiscovery.query(KubernetesResourceUri.parse(coordinates));
                break;
            default:
                throw new UnsupportedOperationException("Unsupported protocol: " + key.getProtocol());
        }

        return Optional.ofNullable(services.computeIfAbsent(key, k -> function.apply(k.getCoordinates()).orElse(null)));
    }
}

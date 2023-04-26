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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.addons.quarkus.k8s.discovery.KnativeServiceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.KnativeServiceUri;
import org.kie.kogito.addons.quarkus.k8s.discovery.VanillaKubernetesResourceDiscovery;
import org.kie.kogito.addons.quarkus.k8s.discovery.VanillaKubernetesResourceUri;

@ApplicationScoped
final class KnativeServiceRegistry {

    private final Map<String, URI> services = new ConcurrentHashMap<>();

    private final KnativeServiceDiscovery knativeServiceDiscovery;

    private final VanillaKubernetesResourceDiscovery vanillaKubernetesResourceDiscovery;

    @Inject
    KnativeServiceRegistry(KnativeServiceDiscovery knativeServiceDiscovery,
            VanillaKubernetesResourceDiscovery vanillaKubernetesResourceDiscovery) {
        this.knativeServiceDiscovery = knativeServiceDiscovery;
        this.vanillaKubernetesResourceDiscovery = vanillaKubernetesResourceDiscovery;
    }

    Optional<URI> getServiceAddress(String serviceName) {
        String[] splitServiceName = serviceName.split("/");
        final Function<String, Optional<URI>> function;
        switch (splitServiceName.length) {
            case 1:
                function = k -> knativeServiceDiscovery.query(new KnativeServiceUri(null, serviceName));
                break;
            case 2:
                function = k -> knativeServiceDiscovery.query(new KnativeServiceUri(splitServiceName[0], splitServiceName[1]));
                break;
            default:
                function = k -> vanillaKubernetesResourceDiscovery.query(VanillaKubernetesResourceUri.parse(k));
        }
        return Optional.ofNullable(services.computeIfAbsent(serviceName, k -> function.apply(k).orElse(null)));
    }
}

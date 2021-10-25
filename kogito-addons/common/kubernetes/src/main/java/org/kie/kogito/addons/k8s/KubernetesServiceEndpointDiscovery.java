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
package org.kie.kogito.addons.k8s;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;

public class KubernetesServiceEndpointDiscovery implements EndpointDiscovery {

    private final EndpointBuilder portBuilder = new EndpointBuilder();
    private KubernetesClient kubernetesClient;

    public KubernetesServiceEndpointDiscovery(final KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public Optional<Endpoint> findEndpoint(String namespace, String name) {
        final Service service = kubernetesClient.services().inNamespace(namespace).withName(name).get();
        if (service == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(portBuilder.buildFrom(service));
    }

    @Override
    public List<Endpoint> findEndpoint(String namespace, Map<String, String> labels) {
        final List<Service> services = kubernetesClient.services().inNamespace(namespace).withLabels(labels).list().getItems();
        final List<Endpoint> endpoints = new ArrayList<>();
        services.forEach(s -> {
            final Endpoint endpoint = portBuilder.buildFrom(s);
            if (endpoint != null) {
                endpoints.add(endpoint);
            }
        });
        return endpoints;
    }
}

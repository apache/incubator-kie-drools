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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Compose instances of {@link EndpointDiscovery} to find a given endpoint among the discovery services.
 * The order the services were inserted matters. If the given endpoint is found, the service is returned immediately.
 */
public class EndpointDiscoveryComposite implements EndpointDiscovery {

    private final List<EndpointDiscovery> endpointDiscoveryList = new LinkedList<>();

    public EndpointDiscoveryComposite(EndpointDiscovery... endpointDiscovery) {
        endpointDiscoveryList.addAll(Arrays.asList(endpointDiscovery));
    }

    public void add(final EndpointDiscovery endpointDiscovery) {
        this.endpointDiscoveryList.add(endpointDiscovery);
    }

    @Override
    public Optional<Endpoint> findEndpoint(String namespace, String name) {
        for (EndpointDiscovery e : endpointDiscoveryList) {
            Optional<Endpoint> endpoint = e.findEndpoint(namespace, name);
            if (endpoint.isPresent()) {
                return endpoint;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Endpoint> findEndpoint(String namespace, Map<String, String> labels) {
        List<Endpoint> endpoints = Collections.emptyList();
        for (EndpointDiscovery e : endpointDiscoveryList) {
            endpoints = e.findEndpoint(namespace, labels);
            if (!endpoints.isEmpty()) {
                return endpoints;
            }
        }
        return endpoints;
    }
}

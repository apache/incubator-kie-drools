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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Route;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

/**
 * Performs the discovery operations for Knative Routes
 *
 * @see <a href="https://rohaan.medium.com/accessing-knative-rest-api-using-fabric8-knative-client-443a16ac43f7">Accessing Knative REST API using Fabric8 Knative Client</a>
 * @see <a href="https://github.com/knative/specs/blob/main/specs/serving/knative-api-specification-1.0.md#route-2">Knative Service</a>
 */
public class KnativeRouteEndpointDiscovery implements EndpointDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(KnativeRouteEndpointDiscovery.class);

    KnativeClient knativeClient;

    public KnativeRouteEndpointDiscovery(final KubernetesClient kubernetesClient) {
        this.adaptKnativeClientFromKube(kubernetesClient);
    }

    /**
     * Defines the {@link KnativeClient} directly.
     */
    public void setKnativeClient(KnativeClient knativeClient) {
        this.knativeClient = knativeClient;
    }

    /**
     * Creates a {@link KnativeClient} from the {@link KubernetesClient} instance.
     * Either this method or {@link #setKnativeClient(KnativeClient)} must be called by child classes
     * before calling the discovery methods.
     */
    public final void adaptKnativeClientFromKube(final KubernetesClient kubernetesClient) {
        try {
            if (kubernetesClient != null && kubernetesClient.isAdaptable(KnativeClient.class)) {
                knativeClient = kubernetesClient.adapt(KnativeClient.class);
            } else {
                LOGGER.warn("Impossible to adapt Fabric8 Kubernetes Client to Knative Client. Discovery operations for Knative won't be performed.");
            }
        } catch (KubernetesClientException ex) {
            // when running on a local environment, the client might try to ping the cluster.
            // instead of returning `false` from isAdaptable, it's throwing the exception.
            // We catch it here to avoid initialization errors on such envs
            LOGGER.warn("Error trying to adapt current Kubernetes Client to Knative. Turn on DEBUG to see the full stack trace: {}", ex.getMessage());
            LOGGER.debug("Stack trace: ", ex);
        }
    }

    @Override
    public Optional<Endpoint> findEndpoint(String namespace, String name) {
        if (knativeClient == null) {
            LOGGER.debug("Knative Client unavailable, skipping Knative Endpoints discovery");
            return Optional.empty();
        }
        final Route route = knativeClient.routes().inNamespace(namespace).withName(name).get();
        if (route == null || route.getStatus() == null) {
            return Optional.empty();
        }
        return Optional.of(new Endpoint(route.getStatus().getUrl()));
    }

    @Override
    public List<Endpoint> findEndpoint(String namespace, Map<String, String> labels) {
        if (knativeClient == null) {
            LOGGER.debug("Knative Client unavailable, skipping Knative Endpoints discovery");
            return Collections.emptyList();
        }
        final List<Route> routes = knativeClient.routes().inNamespace(namespace).withLabels(labels).list().getItems();
        final List<Endpoint> endpoints = new ArrayList<>();
        routes.forEach(r -> endpoints.add(new Endpoint(r.getStatus().getUrl())));
        return endpoints;
    }
}

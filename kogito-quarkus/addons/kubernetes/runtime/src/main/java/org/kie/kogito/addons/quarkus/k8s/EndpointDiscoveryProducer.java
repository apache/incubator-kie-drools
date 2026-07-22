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
package org.kie.kogito.addons.quarkus.k8s;

import org.kie.kogito.addons.k8s.KnativeRouteEndpointDiscovery;
import org.kie.kogito.addons.k8s.KubernetesServiceEndpointDiscovery;
import org.kie.kogito.addons.k8s.ServiceAndThenRouteEndpointDiscovery;

import io.fabric8.kubernetes.client.KubernetesClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@ApplicationScoped
public class EndpointDiscoveryProducer {

    @Inject
    KubernetesClient kubernetesClient;

    @Produces
    @Singleton
    @Default
    @Named("default")
    public ServiceAndThenRouteEndpointDiscovery endpointDiscovery() {
        final KubernetesServiceEndpointDiscovery kubernetesServiceEndpointDiscovery = new KubernetesServiceEndpointDiscovery(kubernetesClient);
        final KnativeRouteEndpointDiscovery knativeRouteEndpointDiscovery = new KnativeRouteEndpointDiscovery(kubernetesClient);
        return new CachedServiceAndThenRouteEndpointDiscovery(kubernetesServiceEndpointDiscovery, knativeRouteEndpointDiscovery);
    }
}

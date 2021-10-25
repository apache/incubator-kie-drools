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
package org.kie.kogito.addons.springboot.k8s;

import org.kie.kogito.addons.k8s.EndpointDiscovery;
import org.kie.kogito.addons.k8s.KnativeRouteEndpointDiscovery;
import org.kie.kogito.addons.k8s.KubernetesServiceEndpointDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fabric8.kubernetes.client.KubernetesClient;

@Configuration
public class EndpointDiscoveryConfig {

    @Bean
    public EndpointDiscovery endpointDiscovery(KubernetesClient kubernetesClient) {
        final KubernetesServiceEndpointDiscovery kubernetesServiceEndpointDiscovery = new KubernetesServiceEndpointDiscovery(kubernetesClient);
        final KnativeRouteEndpointDiscovery knativeRouteEndpointDiscovery = new KnativeRouteEndpointDiscovery(kubernetesClient);
        return new CacheableServiceAndThenRouteEndpointDiscovery(kubernetesServiceEndpointDiscovery, knativeRouteEndpointDiscovery);
    }

}

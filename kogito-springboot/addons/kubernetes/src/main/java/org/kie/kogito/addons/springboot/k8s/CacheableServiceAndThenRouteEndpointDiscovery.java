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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.addons.k8s.CacheNames;
import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.KnativeRouteEndpointDiscovery;
import org.kie.kogito.addons.k8s.KubernetesServiceEndpointDiscovery;
import org.kie.kogito.addons.k8s.ServiceAndThenRouteEndpointDiscovery;
import org.springframework.cache.annotation.Cacheable;

public class CacheableServiceAndThenRouteEndpointDiscovery extends ServiceAndThenRouteEndpointDiscovery {

    public CacheableServiceAndThenRouteEndpointDiscovery(KubernetesServiceEndpointDiscovery kubernetesServiceEndpointDiscovery,
            KnativeRouteEndpointDiscovery knativeRouteEndpointDiscovery) {
        super(kubernetesServiceEndpointDiscovery, knativeRouteEndpointDiscovery);
    }

    @Cacheable(cacheNames = CacheNames.CACHE_BY_NAME, cacheManager = CachingConfig.CACHE_MANAGER)
    @Override
    public Optional<Endpoint> findEndpoint(String namespace, String name) {
        return super.findEndpoint(namespace, name);
    }

    @Cacheable(cacheNames = CacheNames.CACHE_BY_LABELS, cacheManager = CachingConfig.CACHE_MANAGER)
    @Override
    public List<Endpoint> findEndpoint(String namespace, Map<String, String> labels) {
        return super.findEndpoint(namespace, labels);
    }
}

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
package org.kie.kogito.addons.quarkus.k8s;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class KubeDiscoveryConfigCache {

    private static final Logger logger = LoggerFactory.getLogger(KubeDiscoveryConfigCache.class);

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private final KubeResourceDiscovery kResource;

    KubeDiscoveryConfigCache(KubeResourceDiscovery kResource) {
        this.kResource = kResource;
    }

    Optional<String> get(String configName, String configValue) {
        try {
            if (isValidURI(configValue)) {
                String cachedValue = cache.computeIfAbsent(configName, k -> kResource
                        .query(new KubeURI(configValue)).orElse(null));
                return Optional.ofNullable(cachedValue);
            }
        } catch (RuntimeException e) {
            logger.error("Service Discovery has failed", e);
        }
        return Optional.ofNullable(configValue);
    }

    private boolean isValidURI(String value) {
        return value != null && !value.isBlank() && KubeConstants.SUPPORTED_PROTOCOLS
                .stream()
                .anyMatch(protocol -> value.startsWith(protocol + ":"));
    }
}

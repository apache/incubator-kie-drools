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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KubeDiscoveryConfigCache {

    private static final Logger logger = LoggerFactory.getLogger(KubeDiscoveryConfigCache.class);

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private final KubeDiscoveryConfigCacheUpdater updater;

    KubeDiscoveryConfigCache(KubeDiscoveryConfigCacheUpdater updater) {
        this.updater = updater;
    }

    Optional<String> get(String configName, String configValue) {
        try {
            String cachedValue = cache.computeIfAbsent(configName, k -> updater.update(configValue).map(URI::toString).orElse(null));
            return Optional.ofNullable(cachedValue);
        } catch (RuntimeException e) {
            logger.error("Service Discovery has failed on property [{}={}]", configName, configValue, e);
        }
        return Optional.ofNullable(configValue);
    }
}

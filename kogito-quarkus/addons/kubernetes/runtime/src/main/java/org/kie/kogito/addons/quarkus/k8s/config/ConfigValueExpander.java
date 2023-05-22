/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol;

import io.smallrye.config.ConfigValue;

class ConfigValueExpander {

    private final KubeDiscoveryConfigCache kubeDiscoveryConfigCache;

    ConfigValueExpander(KubeDiscoveryConfigCache kubeDiscoveryConfigCache) {
        this.kubeDiscoveryConfigCache = kubeDiscoveryConfigCache;
    }

    ConfigValue expand(ConfigValue configValue) {
        if (configValue == null || !valueContainsDiscovery(configValue)) {
            return configValue;
        }

        return kubeDiscoveryConfigCache.get(configValue.getName(), configValue.getValue())
                .map(configValue::withValue)
                .orElse(configValue);
    }

    private boolean valueContainsDiscovery(ConfigValue configValue) {
        for (KubernetesProtocol protocol : KubernetesProtocol.values()) {
            String value = configValue.getValue();
            if (value != null && value.startsWith(protocol.getValue() + ":")) {
                return true;
            }
        }
        return false;
    }
}

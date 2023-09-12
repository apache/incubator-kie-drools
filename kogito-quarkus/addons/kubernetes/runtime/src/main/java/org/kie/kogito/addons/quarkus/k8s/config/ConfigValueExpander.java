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
package org.kie.kogito.addons.quarkus.k8s.config;

import java.util.Arrays;

import org.kie.kogito.addons.k8s.resource.catalog.KubernetesProtocol;

import io.smallrye.config.ConfigValue;

class ConfigValueExpander {

    private final KubeDiscoveryConfigCache kubeDiscoveryConfigCache;

    ConfigValueExpander(KubeDiscoveryConfigCache kubeDiscoveryConfigCache) {
        this.kubeDiscoveryConfigCache = kubeDiscoveryConfigCache;
    }

    ConfigValue expand(ConfigValue configValue) {
        if (configValue != null && configValue.getRawValue() != null) {
            String serviceCoordinates = extractServiceCoordinates(configValue.getRawValue());
            if (serviceCoordinates != null) {
                return kubeDiscoveryConfigCache.get(configValue.getName(), serviceCoordinates)
                        .map(value -> interpolate(configValue.getRawValue(), value))
                        .map(configValue::withValue)
                        .orElse(configValue);
            }
        }

        return configValue;
    }

    public static String interpolate(String input, String replacement) {
        int startIndex = input.indexOf("${");
        int endIndex = input.indexOf("}", startIndex);

        return input.substring(0, startIndex) + replacement + input.substring(endIndex + 1);
    }

    static String extractServiceCoordinates(String rawValue) {
        int startIndex = rawValue.indexOf("${");
        int endIndex = rawValue.indexOf("}", startIndex);

        if (startIndex != -1 && endIndex != -1) {
            String substring = rawValue.substring(startIndex + 2, endIndex);

            boolean isKubernetesServiceCoordinate = Arrays.stream(KubernetesProtocol.values())
                    .map(KubernetesProtocol::getValue)
                    .anyMatch(protocol -> substring.startsWith(protocol + ":"));

            if (isKubernetesServiceCoordinate) {
                return substring;
            }
        }

        return null;
    }
}

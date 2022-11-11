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

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.kogito.addons.quarkus.k8s.parser.KubeURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.smallrye.config.ConfigSourceInterceptor;
import io.smallrye.config.ConfigSourceInterceptorContext;
import io.smallrye.config.ConfigValue;

public class KubeDiscoveryConfigSourceInterceptor implements ConfigSourceInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getName());
    private transient KubernetesClient client;
    private Map<String, String> cache = new ConcurrentHashMap<>();

    private KubeResourceDiscovery kResource;

    public KubeDiscoveryConfigSourceInterceptor() {
        logger.debug("Configuring k8s client...");
        this.client = new DefaultKubernetesClient();
        this.kResource = new KubeResourceDiscovery(client);
    }

    @Override
    public ConfigValue getValue(ConfigSourceInterceptorContext context, String s) {
        ConfigValue configValue = context.proceed(s);
        try {
            if (isValidURI(configValue)) {
                String cachedValue = cache.computeIfAbsent(configValue.getName(), k -> kResource
                        .query(new KubeURI(configValue.getValue())).orElse(null));
                if (cachedValue != null) {
                    return configValue.withValue(cachedValue);
                }
            }
        } catch (RuntimeException e) {
            logger.error("Service Discovery has failed", e);
        }
        return configValue;
    }

    private boolean isValidURI(ConfigValue value) {
        return value != null && value.getValue() != null && !value.getValue().isBlank() && KubeConstants.SUPPORTED_PROTOCOLS
                .stream()
                .anyMatch(value.getValue()::startsWith);
    }
}

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StaticConfig implements Config {

    private final Addons addons;
    private final Map<Class<? extends KogitoConfig>, KogitoConfig> configMap = new HashMap<>();

    public StaticConfig(Addons addons,
                        KogitoConfig ... configs) {
        this(addons, Arrays.asList(configs));
    }

    protected StaticConfig(Addons addons,
                           Iterable<KogitoConfig> configs) {
        this.addons = addons;
        configs.forEach(this::loadConfig);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends KogitoConfig> T get(Class<T> clazz) {
        return (T) configMap.entrySet().stream()
                .filter(entry -> clazz.isAssignableFrom(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Addons addons() {
        return addons;
    }

    private void loadConfig(KogitoConfig config) {
        if(config != null) {
            configMap.put(config.getClass(), config);
        }
    }
}

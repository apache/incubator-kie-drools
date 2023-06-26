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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.kie.kogito.internal.utils.ConversionUtils;

public class MapConfigResolver implements ConfigResolver {

    private final Map<String, Object> map;

    public MapConfigResolver(Map<String, Object> map) {
        this.map = map;
    }

    public MapConfigResolver(Properties props) {
        this((Map) props);
    }

    @Override
    public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(map.get(name)));
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return map.keySet();
    }

    @Override
    public Map<String, Object> asMap() {
        return map;
    }

    @Override
    public <T> Collection<T> getIndexedConfigProperty(String name, Class<T> clazz) {
        return ConversionUtils.convertToCollection(map.get(name), clazz);
    }
}

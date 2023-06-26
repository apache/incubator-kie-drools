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

import org.kie.kogito.internal.utils.ConversionUtils;

public class SystemPropertiesConfigResolver implements ConfigResolver {

    @Override
    public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
        Object value = null;
        if (Integer.class.isAssignableFrom(clazz)) {
            value = Integer.getInteger(name);
        } else if (String.class.isAssignableFrom(clazz) || Object.class.equals(clazz)) {
            value = System.getProperty(name);
        }
        return Optional.ofNullable(clazz.cast(value));
    }

    @Override
    public Iterable getPropertyNames() {
        return System.getProperties().keySet();
    }

    @Override
    public Map asMap() {
        return System.getProperties();
    }

    @Override
    public <T> Collection<T> getIndexedConfigProperty(String name, Class<T> clazz) {
        return ConversionUtils.convertToCollection(System.getProperty(name), clazz);
    }
}

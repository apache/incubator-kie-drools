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
package org.kie.kogito.codegen.api.context;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

import static org.kie.kogito.internal.utils.ConversionUtils.convert;

public interface KogitoApplicationPropertyProvider {

    static KogitoApplicationPropertyProvider of(Properties properties) {
        return new KogitoApplicationPropertyProvider() {
            @Override
            public Optional<String> getApplicationProperty(String property) {
                return Optional.ofNullable(properties.getProperty(property));
            }

            @Override
            public Collection<String> getApplicationProperties() {
                return properties.stringPropertyNames();
            }

            @Override
            public void setApplicationProperty(String key, String value) {
                properties.put(key, value);
            }

            @Override
            public <T> Optional<T> getApplicationProperty(String property, Class<T> clazz) {
                return Optional.ofNullable(convert(properties.getProperty(property), clazz));
            }

            @Override
            public void removeApplicationProperty(String key) {
                properties.remove(key);
            }
        };
    }

    Optional<String> getApplicationProperty(String property);

    <T> Optional<T> getApplicationProperty(String property, Class<T> clazz);

    Collection<String> getApplicationProperties();

    void setApplicationProperty(String key, String value);

    void removeApplicationProperty(String key);
}

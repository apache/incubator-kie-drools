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

package org.kie.kogito.addon.quarkus.common.config;

import java.util.List;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;

public class AbstractAliasConfigSourceFactory<T extends AbstractAliasConfigSource> implements ConfigSourceFactory {

    protected T configSource;

    public AbstractAliasConfigSourceFactory(T configSource) {
        this.configSource = configSource;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        ConfigValue configValue = context.getValue(configSource.getConfigAlias());
        String value = configValue.getValue();
        if (value == null || value.isEmpty()) {
            return List.of();
        } else {
            configSource.setConfigValue(value);
            configSource.setOrdinal(configValue.getSourceOrdinal());
            return List.of(configSource);
        }
    }
}

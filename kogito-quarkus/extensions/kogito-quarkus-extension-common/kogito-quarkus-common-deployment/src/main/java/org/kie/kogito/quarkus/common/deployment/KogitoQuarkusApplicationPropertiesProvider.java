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
package org.kie.kogito.quarkus.common.deployment;

import java.io.InputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.StreamSupport;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.kie.kogito.codegen.api.context.KogitoApplicationPropertyProvider;

import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YAML_FILE_NAME;
import static org.drools.codegen.common.DroolsModelBuildContext.APPLICATION_PROPERTIES_YML_FILE_NAME;
import static org.drools.codegen.common.context.ModelBuildContextUtils.loadYmlProperties;
import static org.kie.kogito.internal.utils.ConversionUtils.convert;

public class KogitoQuarkusApplicationPropertiesProvider implements KogitoApplicationPropertyProvider {

    private final Properties properties;

    public KogitoQuarkusApplicationPropertiesProvider() {
        this.properties = new Properties();
        try (InputStream ymlResourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(APPLICATION_PROPERTIES_YML_FILE_NAME)) {
            if (ymlResourceStream != null) {
                loadYmlProperties(ymlResourceStream, properties);
            }
        } catch (Exception e) {
            // Ignore exception and continue loading properties from yaml file if present
        }
        try (InputStream yamlResourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(APPLICATION_PROPERTIES_YAML_FILE_NAME)) {
            if (yamlResourceStream != null) {
                loadYmlProperties(yamlResourceStream, properties);
            }
        } catch (Exception e) {
            // Ignore exception and continue loading properties from microprofile config
        }
        Config config = ConfigProvider.getConfig();
        // This is needed to preserve the correct property override order
        List<ConfigSource> sortedSources = StreamSupport.stream(config.getConfigSources().spliterator(), false).sorted(new ConfigSourceSorted()).toList();
        sortedSources.forEach(configSource -> configSource.getPropertyNames().forEach(property -> {
            String value = configSource.getValue(property);
            if (value != null) {
                this.properties.put(property, value);
            }
        }));
    }

    @Override
    public Optional<String> getApplicationProperty(String property) {
        return Optional.ofNullable(properties.getProperty(property));
    }

    @Override
    public Collection<String> getApplicationProperties() {
        return properties.stringPropertyNames();
    }

    @Override
    public <T> Optional<T> getApplicationProperty(String property, Class<T> clazz) {
        return Optional.ofNullable(convert(properties.getProperty(property), clazz));
    }

    @Override
    public void setApplicationProperty(String key, String value) {
        System.setProperty(key, value);
        properties.put(key, value);
    }

    @Override
    public void removeApplicationProperty(String key) {
        System.clearProperty(key);
        properties.remove(key);
    }

    private static final class ConfigSourceSorted implements Comparator<ConfigSource> {

        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            return Integer.compare(o1.getOrdinal(), o2.getOrdinal());
        }
    }
}

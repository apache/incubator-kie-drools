/**
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
package org.drools.util;

import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

public class Config {

    public static String getConfig(String propertyName) {
        return ConfigResolverHolder.CONFIG_RESOLVER.getConfig(propertyName);
    }

    public static String getConfig(String propertyName, String defaultValue) {
        return ConfigResolverHolder.CONFIG_RESOLVER.getConfig(propertyName, defaultValue);
    }

    public static Optional<String> getOptionalConfig(String propertyName) {
        return ConfigResolverHolder.CONFIG_RESOLVER.getOptionalConfig(propertyName);
    }

    interface ConfigResolver {
        String getConfig(String propertyName);
        String getConfig(String propertyName, String defaultValue);
        Optional<String> getOptionalConfig(String propertyName);
    }

    private static class ConfigResolverHolder {
        private static final ConfigResolver CONFIG_RESOLVER = createConfigResolver();

        private static ConfigResolver createConfigResolver() {
            try {
                return new MicroprofileConfigResolver(ConfigProvider.getConfig());
            } catch (Throwable e) {
                return new SystemPropertyConfigResolver();
            }
        }
    }

    private static class MicroprofileConfigResolver implements ConfigResolver {
        private final org.eclipse.microprofile.config.Config config;

        private MicroprofileConfigResolver(org.eclipse.microprofile.config.Config config) {
            this.config = config;
        }

        @Override
        public String getConfig(String propertyName) {
            return getConfig(propertyName, null);
        }

        @Override
        public String getConfig(String propertyName, String defaultValue) {
            return getOptionalConfig(propertyName).orElse(defaultValue);
        }

        @Override
        public Optional<String> getOptionalConfig(String propertyName) {
            return config.getOptionalValue(propertyName, String.class);
        }
    }

    private static class SystemPropertyConfigResolver implements ConfigResolver {
        @Override
        public String getConfig(String propertyName) {
            return System.getProperty(propertyName);
        }

        @Override
        public String getConfig(String propertyName, String defaultValue) {
            return System.getProperty(propertyName, defaultValue);
        }

        @Override
        public Optional<String> getOptionalConfig(String propertyName) {
            return Optional.ofNullable( getConfig(propertyName) );
        }
    }
}

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

package org.kie.flyway.initializer.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.kie.flyway.KieFlywayException;
import org.kie.flyway.initializer.KieModuleFlywayConfigLoader;
import org.kie.flyway.model.KieFlywayModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultKieModuleFlywayConfigLoader implements KieModuleFlywayConfigLoader {

    public static String KIE_FLYWAY_DESCRIPTOR_FILE_LOCATION = "META-INF/kie-flyway.properties";

    public static final String MODULE_PREFIX = "module.";
    public static final String MODULE_NAME_KEY = MODULE_PREFIX + "name";
    public static final String MODULE_LOCATIONS_KEY = MODULE_PREFIX + "locations";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKieModuleFlywayConfigLoader.class);

    private final ClassLoader classLoader;

    public DefaultKieModuleFlywayConfigLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public DefaultKieModuleFlywayConfigLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Collection<KieFlywayModuleConfig> loadModuleConfigs() {
        return Optional.ofNullable(this.classLoader).orElse(this.getClass().getClassLoader())
                .resources(KIE_FLYWAY_DESCRIPTOR_FILE_LOCATION)
                .map(this::toModuleFlywayConfig)
                .toList();
    }

    private KieFlywayModuleConfig toModuleFlywayConfig(URL resourceUrl) {
        LOGGER.debug("Loading configuration from {}", resourceUrl);

        try (InputStream inputStream = resourceUrl.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);

            String moduleName = properties.getProperty(MODULE_NAME_KEY);

            if (Objects.isNull(moduleName)) {
                LOGGER.warn("Could not load module name from file `{}`", resourceUrl);
                throw new KieFlywayException("Could not load module name from `" + resourceUrl + "`");
            }

            LOGGER.debug("Loading Kie Flyway Module {}", moduleName);

            Map<String, String[]> locations = properties.keySet()
                    .stream()
                    .map(String::valueOf)
                    .filter(moduleProperty -> moduleProperty.startsWith(MODULE_LOCATIONS_KEY))
                    .map(dbLocationsProperty -> {
                        LOGGER.debug("Loading location: {}", dbLocationsProperty);
                        // Splitting the key (`module.locations.<dbType>`) to obtain the dbType
                        String[] dbLocationsPropertyArray = dbLocationsProperty.split("\\.");
                        if (dbLocationsPropertyArray.length != 3) {
                            throw new KieFlywayException("Cannot load module `" + moduleName + "` config, file has wrong format");
                        }
                        String[] locationsArray = properties.getProperty(dbLocationsProperty).split(",");

                        return Map.entry(dbLocationsPropertyArray[2], locationsArray);
                    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (locations.isEmpty()) {
                LOGGER.warn("Kie Flyway module `{}` has no locations ", moduleName);
            }

            KieFlywayModuleConfig module = new KieFlywayModuleConfig(moduleName, locations);

            LOGGER.debug("Successfully loaded configuration for module {}", module.getModule());

            return module;
        } catch (Exception e) {
            LOGGER.warn("Could not load configuration from {}", resourceUrl, e);
            throw new KieFlywayException("Could not load ModuleFlywayConfig", e);
        }
    }
}

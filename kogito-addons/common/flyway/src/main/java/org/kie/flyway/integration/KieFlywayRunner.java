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

package org.kie.flyway.integration;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.kie.flyway.KieFlywayException;
import org.kie.flyway.initializer.KieFlywayInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieFlywayRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(KieFlywayRunner.class);

    private final ClassLoader classLoader;
    private final KieFlywayConfiguration<? extends KieFlywayNamedModule> configuration;

    private KieFlywayRunner(KieFlywayConfiguration<? extends KieFlywayNamedModule> configuration) {
        this(configuration, Thread.currentThread().getContextClassLoader());
    }

    protected KieFlywayRunner(KieFlywayConfiguration<? extends KieFlywayNamedModule> configuration, ClassLoader classLoader) {
        this.configuration = configuration;
        this.classLoader = classLoader;
    }

    public static KieFlywayRunner get(KieFlywayConfiguration<? extends KieFlywayNamedModule> configuration) {
        return new KieFlywayRunner(configuration);
    }

    public void runFlyway(DataSource dataSource) {
        assertValue(configuration, "Kie Flyway: Cannot run Kie Flyway migration configuration is null.");

        if (!configuration.isEnabled()) {
            LOGGER.warn("Kie Flyway is disabled, skipping initialization.");
            return;
        }

        assertValue(dataSource, "Kie Flyway: Cannot run Kie Flyway migration default datasource is null");

        Collection<String> excludedModules = configuration.getModules()
                .entrySet()
                .stream().filter(entry -> !entry.getValue().isEnabled())
                .map(Map.Entry::getKey)
                .toList();

        KieFlywayInitializer.builder()
                .withDatasource(dataSource)
                .withClassLoader(classLoader)
                .withModuleExclusions(excludedModules)
                .build().migrate();
    }

    private void assertValue(Object value, String message) {
        if (Objects.isNull(value)) {
            LOGGER.warn(message);
            throw new KieFlywayException(message);
        }
    }
}

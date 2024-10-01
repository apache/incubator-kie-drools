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

package org.kie.flyway.initializer;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.kie.flyway.KieFlywayException;
import org.kie.flyway.initializer.impl.DefaultKieModuleFlywayConfigLoader;
import org.kie.flyway.model.KieFlywayModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;

public class KieFlywayInitializer {
    private static final String KIE_FLYWAY_BASELINE_VERSION = "0.0";

    private static final String KIE_FLYWAY_BASELINE_MESSAGE_TEMPLATE = "Kie Flyway Baseline - %s";

    private static final String KIE_FLYWAY_INDEX_TABLE_INDEX_TEMPLATE = "kie_flyway_history_%s";

    private static final Logger LOGGER = LoggerFactory.getLogger(KieFlywayInitializer.class);

    private final KieModuleFlywayConfigLoader configLoader;
    private final DataSource dataSource;
    private final String databaseType;
    private final List<String> moduleExclusions;

    private KieFlywayInitializer(KieModuleFlywayConfigLoader configLoader, DataSource dataSource, Collection<String> moduleExclusions) {
        this.configLoader = configLoader;
        this.dataSource = dataSource;
        this.databaseType = getDataSourceType(dataSource);
        this.moduleExclusions = new ArrayList<>(moduleExclusions);
    }

    public void migrate() {
        LOGGER.debug("Starting Kie Flyway migration.");
        Collection<KieFlywayModuleConfig> configs = configLoader.loadModuleConfigs();

        checkDuplicatedModuleConfigs(configs);

        LOGGER.debug("Found {} configured Kie Flyway modules.", configs.size());

        configs.forEach(this::runFlyway);
    }

    private void checkDuplicatedModuleConfigs(Collection<KieFlywayModuleConfig> configs) {
        List<String> duplicatedModules = configs.stream()
                .collect(groupingBy(kieFlywayModuleConfig -> kieFlywayModuleConfig.getModule().toLowerCase(), Collectors.counting()))
                .entrySet()
                .stream().filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicatedModules.isEmpty()) {
            LOGGER.warn("Cannot run Kie Flyway migration: Duplicated modules found `{}`", String.join(", ", duplicatedModules));
            throw new KieFlywayException("Cannot run Kie Flyway migration: Duplicated Modules found " + String.join(", ", duplicatedModules));
        }
    }

    private String getDataSourceType(DataSource dataSource) {
        try (Connection con = dataSource.getConnection()) {
            return con.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (Exception e) {
            LOGGER.error("Kie Flyway: Couldn't extract database product name from datasource ", e);
            throw new KieFlywayException("Kie Flyway: Couldn't extract database product name from datasource.", e);
        }
    }

    private void runFlyway(KieFlywayModuleConfig config) {
        LOGGER.debug("Running Flyway for module: {}", config.getModule());

        if (moduleExclusions.contains(config.getModule())) {
            LOGGER.debug("Skipping module: {}", config.getModule());
            return;
        }

        String[] locations = config.getDBScriptLocations(databaseType);

        if (Objects.isNull(locations)) {
            LOGGER.warn("Cannot run Flyway migration for module `{}`, cannot find SQL Script locations for db `{}`", config.getModule(), databaseType);
            throw new KieFlywayException("Cannot run Flyway migration for module `" + config.getModule() + "`, cannot find SQL Script locations for db `" + databaseType + "`");
        }

        Flyway.configure()
                .table(KIE_FLYWAY_INDEX_TABLE_INDEX_TEMPLATE.formatted(config.getModule().replaceAll("[^A-Za-z0-9]", "_")).toLowerCase())
                .dataSource(dataSource)
                .createSchemas(true)
                .baselineOnMigrate(true)
                .baselineVersion(KIE_FLYWAY_BASELINE_VERSION)
                .baselineDescription(KIE_FLYWAY_BASELINE_MESSAGE_TEMPLATE.formatted(config.getModule()))
                .locations(locations)
                .load()
                .migrate();

        LOGGER.debug("Flyway migration complete.");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private KieModuleFlywayConfigLoader configLoader;
        private DataSource dataSource;
        private final List<String> moduleExclusions = new ArrayList<>();

        public Builder withClassLoader(ClassLoader classLoader) {
            this.configLoader = new DefaultKieModuleFlywayConfigLoader(classLoader);
            return this;
        }

        public Builder withDatasource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder withModuleExclusions(Collection<String> moduleExclusions) {
            this.moduleExclusions.addAll(moduleExclusions);
            return this;
        }

        public KieFlywayInitializer build() {
            if (Objects.isNull(dataSource)) {
                throw new KieFlywayException("Cannot create KieFlywayInitializer migration, dataSource is null.");
            }

            if (Objects.isNull(configLoader)) {
                LOGGER.warn("ModuleConfigLoader not configured, falling back to default.");
                this.configLoader = new DefaultKieModuleFlywayConfigLoader();
            }

            return new KieFlywayInitializer(configLoader, dataSource, moduleExclusions);
        }
    }

}

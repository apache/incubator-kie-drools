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
package org.kie.kogito.persistence.quarkus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;

import static org.kie.kogito.persistence.quarkus.KogitoAddOnPersistenceJDBCConfigSource.ORDINAL;

public class KogitoAddOnPersistenceJDBCConfigSourceFactory implements ConfigSourceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoAddOnPersistenceJDBCConfigSourceFactory.class);

    static final String FLYWAY_LOCATIONS = "quarkus.flyway.locations";
    static final String DATASOURCE_DB_KIND = "quarkus.datasource.db-kind";
    private static final String LOCATION_PREFIX = "classpath:/db/";
    static final String POSTGRESQL = "postgresql";
    private static final String ANSI = "ansi";

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        ConfigValue flywayLocationsConfigValue = context.getValue(FLYWAY_LOCATIONS);
        return getConfigSourcesInternal(context.getValue(DATASOURCE_DB_KIND).getValue(),
                flywayLocationsConfigValue.getValue(), flywayLocationsConfigValue.getConfigSourceOrdinal());
    }

    Iterable<ConfigSource> getConfigSourcesInternal(String databaseName, String flywayLocationsValue, int flywayLocationsConfigSourceOrdinal) {
        Map<String, String> configuration = new HashMap<>();
        if (databaseName != null) {
            if (flywayLocationsValue == null || flywayLocationsConfigSourceOrdinal == Integer.MIN_VALUE) {
                configuration.put(FLYWAY_LOCATIONS, LOCATION_PREFIX + getDBName(databaseName));
            } else {
                Set<String> locations = Arrays.stream(flywayLocationsValue.split(",")).collect(Collectors.toSet());
                locations.add(LOCATION_PREFIX + getDBName(databaseName));
                configuration.put(FLYWAY_LOCATIONS, String.join(",", locations));
            }
        } else {
            LOGGER.warn("Kogito Flyway must have the property \"quarkus.datasource.db-kind\" to be set to initialize process schema.");
        }
        return List.of(new KogitoAddOnPersistenceJDBCConfigSource(configuration));
    }

    @Override
    public OptionalInt getPriority() {
        return OptionalInt.of(ORDINAL);
    }

    private String getDBName(final String dbKind) {
        if (POSTGRESQL.equals(dbKind)) {
            return POSTGRESQL;
        } else {
            return ANSI;
        }
    }
}

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
package org.kie.kogito.persistence.quarkus;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.quarkus.KogitoAddOnPersistenceJDBCConfigSourceFactory.FLYWAY_LOCATIONS;
import static org.kie.kogito.persistence.quarkus.KogitoAddOnPersistenceJDBCConfigSourceFactory.POSTGRESQL;

class KogitoAddOnPersistenceJDBCConfigSourceFactoryTest {

    private final static KogitoAddOnPersistenceJDBCConfigSourceFactory factory = new KogitoAddOnPersistenceJDBCConfigSourceFactory();

    @Test
    void getConfigSourcesInternalExistingLocations() {
        Set<String> locationsSet = getLocationsSet(getConfigSource(POSTGRESQL, "/path/to/locations", Integer.MAX_VALUE)
                .getValue(FLYWAY_LOCATIONS));

        assertThat(locationsSet).containsExactlyInAnyOrder("/path/to/locations", "classpath:/db/postgresql");
    }

    @Test
    void getConfigSourcesInternalDefaultLocations() {
        Set<String> locationsSet = getLocationsSet(getConfigSource(POSTGRESQL, "/path/to/locations", Integer.MIN_VALUE)
                .getValue(FLYWAY_LOCATIONS));

        assertThat(locationsSet).containsOnly("classpath:/db/postgresql");
    }

    @Test
    void getConfigSourcesInternalNoExistingLocations() {
        Set<String> locationsSet = getLocationsSet(getConfigSource(POSTGRESQL, null, Integer.MAX_VALUE)
                .getValue(FLYWAY_LOCATIONS));

        assertThat(locationsSet).containsExactlyInAnyOrder("classpath:/db/postgresql");
    }

    @Test
    void getConfigSourcesInternalDatabaseNameEmpty() {
        ConfigSource configSource = getConfigSource(null, "/path/to/locations", Integer.MAX_VALUE);
        assertThat(configSource.getPropertyNames()).isEmpty();
    }

    @Test
    void getConfigSourcesInternalEnsuresNoDuplication() {
        Set<String> locationsSet = getLocationsSet(getConfigSource(POSTGRESQL, "classpath:/db/postgresql", Integer.MAX_VALUE)
                .getValue(FLYWAY_LOCATIONS));

        assertThat(locationsSet).containsExactlyInAnyOrder("classpath:/db/postgresql");
    }

    private static ConfigSource getConfigSource(String databaseName, String flywayLocationsValue,
            int flywayLocationsConfigSourceOrdinal) {
        Iterable<ConfigSource> configSources = factory.getConfigSourcesInternal(databaseName, flywayLocationsValue,
                flywayLocationsConfigSourceOrdinal);
        assertThat(configSources).hasSize(1);
        return configSources.iterator().next();
    }

    private static Set<String> getLocationsSet(String locations) {
        return Arrays.stream(locations.split(",")).collect(Collectors.toSet());
    }
}
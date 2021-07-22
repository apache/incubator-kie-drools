/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.inmemory.postgresql.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

public class InmemoryPostgreSQLConfigSourceProvider implements ConfigSourceProvider {

    static final String QUARKUS_DATASOURCE_REACTIVE_URL = "quarkus.datasource.reactive.url";
    static final String QUARKUS_DATASOURCE_JDBC_URL = "quarkus.datasource.jdbc.url";
    static final String QUARKUS_DATASOURCE_USERNAME = "quarkus.datasource.username";
    static final String QUARKUS_DATASOURCE_PASSWORD = "quarkus.datasource.password";

    static final String DEFAULT_DATABASE = "postgres";
    static final String DEFAULT_REACTIVE_URL = "postgresql://localhost:%d/" + DEFAULT_DATABASE;
    static final String DEFAULT_JDBC_URL = "jdbc:postgresql://localhost:%d/" + DEFAULT_DATABASE;
    static final String DEFAULT_USERNAME = "postgres";
    static final String DEFAULT_PASSWORD = "postgres";

    private final int port;

    public InmemoryPostgreSQLConfigSourceProvider(int port) {
        this.port = port;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
        Map<String, String> properties = new HashMap<>();

        properties.put(QUARKUS_DATASOURCE_REACTIVE_URL, String.format(DEFAULT_REACTIVE_URL, port));
        properties.put(QUARKUS_DATASOURCE_JDBC_URL, String.format(DEFAULT_JDBC_URL, port));
        properties.put(QUARKUS_DATASOURCE_USERNAME, DEFAULT_USERNAME);
        properties.put(QUARKUS_DATASOURCE_PASSWORD, DEFAULT_PASSWORD);

        return Collections.singleton(new InmemoryPostgreSQLConfigSource(properties));
    }
}

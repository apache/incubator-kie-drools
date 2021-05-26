/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.testcontainers.quarkus;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.resources.ConditionalQuarkusTestResource;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;

import static org.kie.kogito.testcontainers.KogitoPostgreSqlContainer.POSTGRESQL_CONNECTION_URI;

/**
 * PostgreSQL quarkus resource that works within the test lifecycle.
 */
public class PostgreSqlQuarkusTestResource extends ConditionalQuarkusTestResource<KogitoPostgreSqlContainer> {

    public static final String QUARKUS_DATASOURCE_REACTIVE_URL = "quarkus.datasource.reactive.url";
    public static final String QUARKUS_DATASOURCE_JDBC_URL = "quarkus.datasource.jdbc.url";
    public static final String QUARKUS_DATASOURCE_USERNAME = "quarkus.datasource.username";
    public static final String QUARKUS_DATASOURCE_PASSWORD = "quarkus.datasource.password";

    private static final KogitoPostgreSqlContainer container = new KogitoPostgreSqlContainer();

    public PostgreSqlQuarkusTestResource() {
        super(container);
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> start = super.start();
        if (start.isEmpty()) {
            return start;
        }

        Map<String, String> properties = new HashMap<>(start);
        properties.put(QUARKUS_DATASOURCE_REACTIVE_URL, container.getReactiveUrl());
        properties.put(QUARKUS_DATASOURCE_JDBC_URL, container.getJdbcUrl());
        properties.put(QUARKUS_DATASOURCE_USERNAME, container.getUsername());
        properties.put(QUARKUS_DATASOURCE_PASSWORD, container.getPassword());
        return properties;
    }

    @Override
    protected String getKogitoProperty() {
        return POSTGRESQL_CONNECTION_URI;
    }

    @Override
    protected String getKogitoPropertyValue() {
        return getTestResource().getReactiveUrl();
    }

    public static class Conditional extends PostgreSqlQuarkusTestResource {

        public Conditional() {
            enableConditional();
        }
    }
}
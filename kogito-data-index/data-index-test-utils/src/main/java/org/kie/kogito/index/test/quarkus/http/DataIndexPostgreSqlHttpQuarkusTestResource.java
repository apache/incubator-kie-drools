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

package org.kie.kogito.index.test.quarkus.http;

import java.util.HashMap;
import java.util.Map;

public class DataIndexPostgreSqlHttpQuarkusTestResource extends AbstractDataIndexHttpQuarkusTestResource<DataIndexPostgreSqlHttpResource> {

    public static final String QUARKUS_DATASOURCE_JDBC_URL = "quarkus.datasource.jdbc.url";
    public static final String QUARKUS_DATASOURCE_USERNAME = "quarkus.datasource.username";
    public static final String QUARKUS_DATASOURCE_PASSWORD = "quarkus.datasource.password";
    public static final String DATA_INDEX_MIGRATE_DB = "kogito.data-index.migrate.db";

    private boolean migrateDb = true;

    public DataIndexPostgreSqlHttpQuarkusTestResource() {
        super(new DataIndexPostgreSqlHttpResource());
    }

    @Override
    public Map<String, String> start() {
        if (migrateDb) {
            getTestResource().getDataIndex().migrateDB();
        }
        return super.start();
    }

    @Override
    public void init(Map<String, String> initArgs) {
        if (initArgs.containsKey(DATA_INDEX_MIGRATE_DB)) {
            migrateDb = Boolean.parseBoolean(initArgs.getOrDefault(DATA_INDEX_MIGRATE_DB, "true"));
        }
    }

    @Override
    protected Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>(getDataIndexConnectionProperties());
        properties.put(QUARKUS_DATASOURCE_JDBC_URL, getTestResource().getPostgresql().getJdbcUrl());
        properties.put(QUARKUS_DATASOURCE_USERNAME, getTestResource().getPostgresql().getUsername());
        properties.put(QUARKUS_DATASOURCE_PASSWORD, getTestResource().getPostgresql().getPassword());
        return properties;
    }

}

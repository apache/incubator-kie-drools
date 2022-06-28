/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum DatabaseType {
    ANSI("ansi", "process_instances"),
    ORACLE("Oracle", "PROCESS_INSTANCES"),
    POSTGRES("PostgreSQL", "process_instances");

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseType.class);
    private final String dbIdentifier;
    private final String tableNamePattern;

    DatabaseType(final String dbIdentifier, final String tableNamePattern) {
        this.dbIdentifier = dbIdentifier;
        this.tableNamePattern = tableNamePattern;
    }

    public String getDbIdentifier() {
        return this.dbIdentifier;
    }

    public String getTableNamePattern() {
        return tableNamePattern;
    }

    public static DatabaseType create(final String dbIdentifier) {
        if (ORACLE.getDbIdentifier().equals(dbIdentifier)) {
            return ORACLE;
        } else if (POSTGRES.getDbIdentifier().equals(dbIdentifier)) {
            return POSTGRES;
        } else {
            var msg = String.format("Unrecognized DB (%s), defaulting to ansi", dbIdentifier);
            LOGGER.warn(msg);
            return ANSI;
        }
    }

    public static DatabaseType getDataBaseType(Connection connection) throws SQLException {
        final DatabaseMetaData metaData = connection.getMetaData();
        final String dbProductName = metaData.getDatabaseProductName();
        return DatabaseType.create(dbProductName);
    }
}

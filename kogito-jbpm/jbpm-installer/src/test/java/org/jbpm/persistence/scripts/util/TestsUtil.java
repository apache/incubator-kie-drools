/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.persistence.scripts.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import org.jbpm.persistence.scripts.DatabaseType;
import org.jbpm.persistence.scripts.PersistenceUnit;
import org.jbpm.persistence.scripts.TestPersistenceContext;

/**
 * Contains util methods that are used for testing SQL scripts.
 */
public final class TestsUtil {

    /**
     * Gets SQL scripts for selected database type.
     * @param folderWithDDLs Root folder containing SQL scripts for all database types.
     * @param databaseType Database type.
     * @param sortByName If true, resulting array of SQL script files will be sorted by filename using String
     * comparator.
     * @return Array of SQL script files. If there are no SQL script files found, returns empty array.
     */
    public static File[] getDDLScriptFilesByDatabaseType(final File folderWithDDLs, final DatabaseType databaseType,
            final boolean sortByName) {
        final File folderWithScripts = new File(folderWithDDLs.getPath() + File.separator
                + databaseType.getScriptsFolderName());
        if (folderWithScripts.exists()) {
            final File[] foundFiles = folderWithScripts.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".sql");
                }
            });

            if (foundFiles == null) {
                return new File[0];
            }

            if (sortByName) {
                Arrays.sort(foundFiles, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }

            return foundFiles;
        } else {
            return new File[0];
        }
    }

    /**
     * Gets database type based on dialect property specified in data source properties.
     * @param dataSourceProperties Data source properties.
     * @return Database type based on specified dialect property. If no dialect is specified,
     * returns H2 database type.
     */
    public static DatabaseType getDatabaseType(final Properties dataSourceProperties) {
        final String hibernateDialect = dataSourceProperties.getProperty("dialect");
        if (!"".equals(hibernateDialect)) {
            return getDatabaseTypeBySQLDialect(hibernateDialect);
        } else {
            return DatabaseType.H2;
        }
    }

    /**
     * Gets database type based on specified SQL dialect.
     * @param sqlDialect SQL dialect.
     * @return Database type based on specified SQL dialect.
     * If specified SQL dialect is not supported, throws IllegalArgumentException.
     */
    public static DatabaseType getDatabaseTypeBySQLDialect(final String sqlDialect) {
        if (sqlDialect.contains("DB2Dialect")) {
            return DatabaseType.DB2;
        } else if (sqlDialect.contains("DerbyDialect")) {
            return DatabaseType.DERBY;
        } else if (sqlDialect.contains("H2Dialect")) {
            return DatabaseType.H2;
        } else if (sqlDialect.contains("HSQLDialect")) {
            return DatabaseType.HSQLDB;
        } else if (sqlDialect.contains("MySQL5Dialect")) {
            return DatabaseType.MYSQL5;
        } else if (sqlDialect.contains("MySQL5InnoDBDialect")) {
            return DatabaseType.MYSQLINNODB;
        } else if (sqlDialect.contains("Oracle")) {
            return DatabaseType.ORACLE;
        } else if (sqlDialect.contains("Postgre")) {
            return DatabaseType.POSTGRESQL;
        } else if (sqlDialect.contains("SQLServer2008Dialect") || sqlDialect.contains("SQLServer2012Dialect")) {
            return DatabaseType.SQLSERVER2008;
        } else if (sqlDialect.contains("SQLServerDialect") || sqlDialect.contains("SQLServer2005Dialect")) {
            return DatabaseType.SQLSERVER;
        } else {
            throw new IllegalArgumentException("SQL dialect type " + sqlDialect + " is not supported!");
        }
    }

    public static byte[] hexStringToByteArray(final String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Clears database schema.
     */
    public static void clearSchema() {
        final TestPersistenceContext clearSchemaContext = new TestPersistenceContext();
        clearSchemaContext.init(PersistenceUnit.CLEAR_SCHEMA);
        clearSchemaContext.clean();
    }

    private TestsUtil() {
        // It makes no sense to create instances of util classes.
    }
}

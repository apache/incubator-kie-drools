/**
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
package org.kie.test.util.db.internal;

public enum DatabaseProvider {

    DB2,
    H2,
    MARIADB,
    MSSQL,
    MYSQL,
    ORACLE,
    POSTGRES,
    POSTGRES_PLUS,
    SYBASE;

    public static DatabaseProvider fromDriverClassName(String driverClassName) {
        if (driverClassName == null || driverClassName.isEmpty()) {
            throw new IllegalArgumentException("Driver class name cannot be empty.");
        }

        String sanitizedDriverClassName = driverClassName.trim().toLowerCase();
        if (sanitizedDriverClassName.startsWith("com.ibm.db2")) {
            return DB2;
        } else if (sanitizedDriverClassName.startsWith("org.h2")) {
            return H2;
        } else if (sanitizedDriverClassName.startsWith("com.microsoft.sqlserver")) {
            return MSSQL;
        } else if (sanitizedDriverClassName.startsWith("org.mariadb")) {
            return MARIADB;
        } else if (sanitizedDriverClassName.startsWith("com.mysql")) {
            return MYSQL;
        } else if (sanitizedDriverClassName.startsWith("oracle")) {
            return ORACLE;
        } else if (sanitizedDriverClassName.startsWith("org.postgresql")) {
            return POSTGRES;
        } else if (sanitizedDriverClassName.startsWith("com.edb")) {
            return POSTGRES_PLUS;
        } else if (sanitizedDriverClassName.startsWith("com.sybase")) {
            return SYBASE;
        } else {
            throw new IllegalArgumentException("Unsupported database provider with a driver class:" + driverClassName);
        }
    }
}

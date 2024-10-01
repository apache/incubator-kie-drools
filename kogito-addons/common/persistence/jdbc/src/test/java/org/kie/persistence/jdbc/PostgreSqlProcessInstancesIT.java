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
package org.kie.persistence.jdbc;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgreSqlProcessInstancesIT extends AbstractProcessInstancesIT {

    @Container
    private final static KogitoPostgreSqlContainer PG_CONTAINER = new KogitoPostgreSqlContainer();
    private static PGSimpleDataSource PG_DATA_SOURCE;

    @BeforeAll
    public static void start() {
        PG_DATA_SOURCE = new PGSimpleDataSource();
        PG_DATA_SOURCE.setUrl(PG_CONTAINER.getJdbcUrl());
        PG_DATA_SOURCE.setUser(PG_CONTAINER.getUsername());
        PG_DATA_SOURCE.setPassword(PG_CONTAINER.getPassword());
        initMigration(PG_DATA_SOURCE);
    }

    protected DataSource getDataSource() {
        return PG_DATA_SOURCE;
    }
}

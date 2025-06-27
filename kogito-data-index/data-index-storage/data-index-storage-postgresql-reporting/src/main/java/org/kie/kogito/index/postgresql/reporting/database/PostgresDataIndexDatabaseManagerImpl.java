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
package org.kie.kogito.index.postgresql.reporting.database;

import org.kie.kogito.persistence.postgresql.reporting.database.BasePostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresApplyMappingSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresIndexesSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTableSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerDeleteSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerInsertSqlBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PostgresDataIndexDatabaseManagerImpl extends BasePostgresDatabaseManagerImpl {

    protected PostgresDataIndexDatabaseManagerImpl() {
        //CDI proxy
    }

    private EntityManager em;

    @Inject
    public PostgresDataIndexDatabaseManagerImpl(
            final EntityManager em,
            final PostgresIndexesSqlBuilder indexesSqlBuilder,
            final PostgresTableSqlBuilder tableSqlBuilder,
            final PostgresTriggerDeleteSqlBuilder triggerDeleteSqlBuilder,
            final PostgresTriggerInsertSqlBuilder triggerInsertSqlBuilder,
            final PostgresApplyMappingSqlBuilder applyMappingSqlBuilder) {
        super(indexesSqlBuilder,
                tableSqlBuilder,
                triggerDeleteSqlBuilder,
                triggerInsertSqlBuilder,
                applyMappingSqlBuilder);
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager(final String sourceTableName) {
        return em;
    }

}

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

import java.util.Locale;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.kie.kogito.index.postgresql.model.JobEntityRepository;
import org.kie.kogito.index.postgresql.model.ProcessInstanceEntityRepository;
import org.kie.kogito.index.postgresql.model.UserTaskInstanceEntityRepository;
import org.kie.kogito.persistence.postgresql.reporting.database.BasePostgresDatabaseManagerImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresApplyMappingSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresIndexesSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTableSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerDeleteSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerInsertSqlBuilder;

@ApplicationScoped
public class PostgresDataIndexDatabaseManagerImpl extends BasePostgresDatabaseManagerImpl {

    private ProcessInstanceEntityRepository processInstanceEntityRepository;
    private UserTaskInstanceEntityRepository userTaskInstanceEntityRepository;
    private JobEntityRepository jobEntityRepository;

    protected PostgresDataIndexDatabaseManagerImpl() {
        //CDI proxy
    }

    @Inject
    public PostgresDataIndexDatabaseManagerImpl(final ProcessInstanceEntityRepository processInstanceEntityRepository,
            final UserTaskInstanceEntityRepository userTaskInstanceEntityRepository,
            final JobEntityRepository jobEntityRepository,
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
        this.processInstanceEntityRepository = Objects.requireNonNull(processInstanceEntityRepository);
        this.userTaskInstanceEntityRepository = Objects.requireNonNull(userTaskInstanceEntityRepository);
        this.jobEntityRepository = Objects.requireNonNull(jobEntityRepository);
    }

    @Override
    protected EntityManager getEntityManager(final String sourceTableName) {
        switch (sourceTableName.toLowerCase(Locale.ROOT)) {
            case "processes":
                return processInstanceEntityRepository.getEntityManager();
            case "tasks":
                return userTaskInstanceEntityRepository.getEntityManager();
            case "jobs":
                return jobEntityRepository.getEntityManager();
            default:
                throw new IllegalArgumentException(String.format("There is no repository defined for '%s'.", sourceTableName));
        }
    }

}

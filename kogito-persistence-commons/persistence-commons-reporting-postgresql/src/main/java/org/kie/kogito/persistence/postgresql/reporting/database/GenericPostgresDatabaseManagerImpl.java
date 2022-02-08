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
package org.kie.kogito.persistence.postgresql.reporting.database;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.kie.kogito.persistence.postgresql.model.CacheEntityRepository;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.IndexesSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TableSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TriggerDeleteSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.TriggerInsertSqlBuilderImpl;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;

@ApplicationScoped
public class GenericPostgresDatabaseManagerImpl extends BasePostgresDatabaseManagerImpl {

    private CacheEntityRepository repository;

    protected GenericPostgresDatabaseManagerImpl() {
        //CDI proxy
    }

    @Inject
    public GenericPostgresDatabaseManagerImpl(final CacheEntityRepository repository,
            final IndexesSqlBuilderImpl indexesSqlBuilder,
            final TableSqlBuilderImpl tableSqlBuilder,
            final TriggerDeleteSqlBuilderImpl triggerDeleteSqlBuilder,
            final TriggerInsertSqlBuilderImpl triggerInsertSqlBuilder) {
        super(indexesSqlBuilder,
                tableSqlBuilder,
                triggerDeleteSqlBuilder,
                triggerInsertSqlBuilder);
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    protected EntityManager getEntityManager(final String sourceTableName) {
        return repository.getEntityManager();
    }

    @Override
    //Change visibility for Unit Tests
    public List<PathSegment> parsePathSegments(List<PostgresMapping> mappings) {
        return super.parsePathSegments(mappings);
    }
}

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
package org.kie.kogito.persistence.reporting.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.ApplyMappingSqlBuilder;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.Context;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.IndexesSqlBuilder;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TableSqlBuilder;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TriggerDeleteSqlBuilder;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TriggerInsertSqlBuilder;
import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.PartitionField;
import org.kie.kogito.persistence.reporting.model.paths.JoinPathSegment;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.kie.kogito.persistence.reporting.model.paths.TerminalPathSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class BaseDatabaseManagerImpl<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>, D extends MappingDefinition<T, F, P, J, M>, C extends Context<T, F, P, J, M>>
        implements DatabaseManager<T, F, P, J, M, D, C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseManagerImpl.class);

    private IndexesSqlBuilder<T, F, P, J, M, C> indexesSqlBuilder;
    private TableSqlBuilder<T, F, P, J, M, C> tableSqlBuilder;
    private TriggerDeleteSqlBuilder<T, F, P, J, M, C> triggerDeleteSqlBuilder;
    private TriggerInsertSqlBuilder<T, F, P, J, M, C> triggerInsertSqlBuilder;
    private ApplyMappingSqlBuilder<T, F, P, J, M, C> applyMappingSqlBuilder;

    protected BaseDatabaseManagerImpl() {
        //CDI proxy
    }

    protected BaseDatabaseManagerImpl(final IndexesSqlBuilder<T, F, P, J, M, C> indexesSqlBuilder,
            final TableSqlBuilder<T, F, P, J, M, C> tableSqlBuilder,
            final TriggerDeleteSqlBuilder<T, F, P, J, M, C> triggerDeleteSqlBuilder,
            final TriggerInsertSqlBuilder<T, F, P, J, M, C> triggerInsertSqlBuilder,
            final ApplyMappingSqlBuilder<T, F, P, J, M, C> applyMappingSqlBuilder) {
        this.indexesSqlBuilder = Objects.requireNonNull(indexesSqlBuilder);
        this.tableSqlBuilder = Objects.requireNonNull(tableSqlBuilder);
        this.triggerDeleteSqlBuilder = Objects.requireNonNull(triggerDeleteSqlBuilder);
        this.triggerInsertSqlBuilder = Objects.requireNonNull(triggerInsertSqlBuilder);
        this.applyMappingSqlBuilder = Objects.requireNonNull(applyMappingSqlBuilder);
    }

    protected abstract EntityManager getEntityManager(final String sourceTableName);

    protected abstract TerminalPathSegment<T, J, M> buildTerminalPathSegment(final String segment,
            final PathSegment parent,
            final M mapping);

    @Override
    public void createArtifacts(final D mappingDefinition) {
        try {
            final ObjectWriter objectWriter = CloudEventUtils.Mapper.mapper().writerWithDefaultPrettyPrinter();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Creating artifacts for:%n%s", objectWriter.writeValueAsString(mappingDefinition)));
            }

            LOGGER.info("Building Context...");
            final C context = createContext(mappingDefinition);

            LOGGER.info("Creating TABLE...");
            final String sourceTableName = mappingDefinition.getSourceTableName();
            getEntityManager(sourceTableName)
                    .createNativeQuery(tableSqlBuilder.createTableSql(context))
                    .executeUpdate();

            LOGGER.info("Creating INDEXES...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(indexesSqlBuilder.createTableIndexesSql(context))
                    .executeUpdate();

            LOGGER.info("Creating INSERT TRIGGER FUNCTION...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(triggerInsertSqlBuilder.createInsertTriggerFunctionSql(context))
                    .executeUpdate();

            LOGGER.info("Creating INSERT TRIGGER...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(triggerInsertSqlBuilder.createInsertTriggerSql(context))
                    .executeUpdate();

            LOGGER.info("Creating DELETE TRIGGER FUNCTION...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(triggerDeleteSqlBuilder.createDeleteTriggerFunctionSql(context))
                    .executeUpdate();

            LOGGER.info("Creating DELETE TRIGGER...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(triggerDeleteSqlBuilder.createDeleteTriggerSql(context))
                    .executeUpdate();

            LOGGER.info("Applying mappings to existing data...");
            getEntityManager(sourceTableName)
                    .createNativeQuery(applyMappingSqlBuilder.apply(context))
                    .executeUpdate();

        } catch (JsonProcessingException jpe) {
            LOGGER.error(jpe.getMessage());
        }
    }

    @Override
    public void destroyArtifacts(final D mappingDefinition) {
        LOGGER.info("Building Context...");
        final C context = createContext(mappingDefinition);

        LOGGER.info("Destroying TABLE...");
        final String sourceTableName = mappingDefinition.getSourceTableName();
        getEntityManager(sourceTableName)
                .createNativeQuery(tableSqlBuilder.dropTableSql(context))
                .executeUpdate();

        LOGGER.info("Destroying INDEXES...");
        getEntityManager(sourceTableName)
                .createNativeQuery(indexesSqlBuilder.dropTableIndexesSql(context))
                .executeUpdate();

        LOGGER.info("Destroying INSERT TRIGGER ...");
        getEntityManager(sourceTableName)
                .createNativeQuery(triggerInsertSqlBuilder.dropInsertTriggerSql(context))
                .executeUpdate();

        LOGGER.info("Destroying INSERT TRIGGER FUNCTION...");
        getEntityManager(sourceTableName)
                .createNativeQuery(triggerInsertSqlBuilder.dropInsertTriggerFunctionSql(context))
                .executeUpdate();

        LOGGER.info("Destroying DELETE TRIGGER ...");
        getEntityManager(sourceTableName)
                .createNativeQuery(triggerDeleteSqlBuilder.dropDeleteTriggerSql(context))
                .executeUpdate();

        LOGGER.info("Destroying DELETE TRIGGER FUNCTION...");
        getEntityManager(sourceTableName)
                .createNativeQuery(triggerDeleteSqlBuilder.dropDeleteTriggerFunctionSql(context))
                .executeUpdate();
    }

    protected List<PathSegment> parsePathSegments(final List<M> mappings) {
        final int[] groupCount = { 0 };
        final List<PathSegment> mappingPaths = new ArrayList<>();
        mappings.forEach(mapping -> {
            final String srcPath = mapping.getSourceJsonPath();
            final String[] pathParts = srcPath.split("\\.");
            List<PathSegment> paths = mappingPaths;
            PathSegment parent = null;
            for (int idx = 0; idx < pathParts.length; idx++) {
                PathSegment sibling;
                final String pathPart = pathParts[idx];
                final Optional<PathSegment> path = paths.stream().filter(p -> Objects.equals(p.getSegment(), pathPart)).findFirst();
                if (path.isEmpty()) {
                    if (idx == pathParts.length - 1) {
                        sibling = buildTerminalPathSegment(pathPart, parent, mapping);
                    } else if (pathPart.endsWith("[]")) {
                        sibling = new JoinPathSegment(pathPart, parent, "g" + groupCount[0]++);
                    } else {
                        sibling = new PathSegment(pathPart, parent);
                    }
                    paths.add(sibling);
                    parent = sibling;
                    paths = sibling.getChildren();
                } else {
                    parent = path.get();
                    paths = path.get().getChildren();
                }
            }
        });
        return mappingPaths;
    }

    protected abstract Map<String, String> getSourceTableFieldTypes(final String sourceTableName);
}

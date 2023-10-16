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
package org.kie.kogito.persistence.postgresql.reporting.database;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;
import javax.transaction.Transactional;

import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresApplyMappingSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresContext;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresIndexesSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTableSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerDeleteSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders.PostgresTriggerInsertSqlBuilder;
import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMappingDefinition;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.postgresql.reporting.model.paths.PostgresTerminalPathSegment;
import org.kie.kogito.persistence.reporting.database.BaseDatabaseManagerImpl;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.kie.kogito.persistence.reporting.model.paths.TerminalPathSegment;

import static org.kie.kogito.persistence.reporting.database.Validations.validateFieldMappings;
import static org.kie.kogito.persistence.reporting.database.Validations.validateMappingId;
import static org.kie.kogito.persistence.reporting.database.Validations.validateSourceTableIdentityFields;
import static org.kie.kogito.persistence.reporting.database.Validations.validateSourceTableJsonFieldName;
import static org.kie.kogito.persistence.reporting.database.Validations.validateSourceTableName;
import static org.kie.kogito.persistence.reporting.database.Validations.validateSourceTablePartitionFields;
import static org.kie.kogito.persistence.reporting.database.Validations.validateTargetTableName;

public abstract class BasePostgresDatabaseManagerImpl
        extends BaseDatabaseManagerImpl<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresMappingDefinition, PostgresContext> {

    private static final String COLUMN_INFO = "SELECT column_name, udt_name FROM information_schema.columns WHERE table_name = '%s';";

    protected BasePostgresDatabaseManagerImpl() {
        //CDI proxy
    }

    protected BasePostgresDatabaseManagerImpl(final PostgresIndexesSqlBuilder indexesSqlBuilder,
            final PostgresTableSqlBuilder tableSqlBuilder,
            final PostgresTriggerDeleteSqlBuilder triggerDeleteSqlBuilder,
            final PostgresTriggerInsertSqlBuilder triggerInsertSqlBuilder,
            final PostgresApplyMappingSqlBuilder applyMappingSqlBuilder) {
        super(indexesSqlBuilder,
                tableSqlBuilder,
                triggerDeleteSqlBuilder,
                triggerInsertSqlBuilder,
                applyMappingSqlBuilder);
    }

    @Override
    protected TerminalPathSegment<JsonType, PostgresJsonField, PostgresMapping> buildTerminalPathSegment(final String segment,
            final PathSegment parent,
            final PostgresMapping mapping) {
        return new PostgresTerminalPathSegment(segment, parent, mapping);
    }

    @Override
    public PostgresContext createContext(final PostgresMappingDefinition mappingDefinition) {
        final String mappingId = validateMappingId(mappingDefinition.getMappingId());
        final String sourceTableName = validateSourceTableName(mappingDefinition.getSourceTableName());
        final String sourceTableJsonFieldName = validateSourceTableJsonFieldName(mappingDefinition.getSourceTableJsonFieldName());
        final List<PostgresField> sourceTableIdentityFields = validateSourceTableIdentityFields(mappingDefinition.getSourceTableIdentityFields());
        final List<PostgresPartitionField> sourceTablePartitionFields = validateSourceTablePartitionFields(mappingDefinition.getSourceTablePartitionFields());
        final String targetTableName = validateTargetTableName(mappingDefinition.getTargetTableName());
        final List<PostgresMapping> mappings = validateFieldMappings(mappingDefinition.getFieldMappings());
        final List<PathSegment> pathSegments = parsePathSegments(mappings);
        final Map<String, String> sourceTableFieldTypes = getSourceTableFieldTypes(sourceTableName);

        return new PostgresContext(mappingId,
                sourceTableName,
                sourceTableJsonFieldName,
                sourceTableIdentityFields,
                sourceTablePartitionFields,
                targetTableName,
                mappings,
                pathSegments,
                sourceTableFieldTypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> getSourceTableFieldTypes(final String sourceTableName) {
        final EntityManager em = getEntityManager(sourceTableName);
        final Query query = em.createNativeQuery(String.format(COLUMN_INFO, sourceTableName), "ColumnInformationMapping");
        final List<ColumnInformationRow> results = query.getResultList();
        return results.stream().collect(Collectors.toMap(i -> i.name, i -> i.type));
    }

    @Override
    @Transactional
    public void createArtifacts(final PostgresMappingDefinition mappingDefinition) {
        super.createArtifacts(mappingDefinition);
    }

    @Override
    @Transactional
    public void destroyArtifacts(final PostgresMappingDefinition mappingDefinition) {
        super.destroyArtifacts(mappingDefinition);
    }

    @Entity
    @SqlResultSetMapping(
            name = "ColumnInformationMapping",
            entities = {
                    @EntityResult(
                            entityClass = ColumnInformationRow.class,
                            fields = { @FieldResult(name = "name", column = "column_name"),
                                    @FieldResult(name = "type", column = "udt_name") })
            })
    public static class ColumnInformationRow {

        @Id
        @Column(nullable = false)
        @SuppressWarnings("unused")
        private String name;

        @Column
        private String type;

    }
}

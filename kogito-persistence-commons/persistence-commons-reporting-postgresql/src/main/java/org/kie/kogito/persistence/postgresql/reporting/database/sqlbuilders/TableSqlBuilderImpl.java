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
package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TableSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TableSqlBuilderImpl implements TableSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableSqlBuilderImpl.class);

    private static final String CREATE_TABLE_TEMPLATE =
            "CREATE TABLE %s ( %n" +
                    "%s, %n" +
                    "%s %n" +
                    ");%n";

    private static final String DROP_TABLE_TEMPLATE = "DROP TABLE IF EXISTS %s;%n";

    @Override
    public String createTableSql(final PostgresContext context) {
        final String targetTableName = context.getTargetTableName();
        final List<PostgresMapping> getFieldMappings = context.getFieldMappings();
        final List<PostgresField> simpleMappings = new ArrayList<>();
        simpleMappings.addAll(context.getSourceTableIdentityFields());
        simpleMappings.addAll(context
                .getSourceTablePartitionFields()
                .stream()
                .map(pf -> new PostgresField(pf.getFieldName(), pf.getFieldType()))
                .collect(Collectors.toList()));

        final String sql = String.format(CREATE_TABLE_TEMPLATE,
                targetTableName,
                simpleMappings
                        .stream()
                        .map(TableSqlBuilderImpl::buildTargetIdentityFieldSql)
                        .collect(Collectors.joining(", " + String.format("%n"))),
                getFieldMappings
                        .stream()
                        .map(TableSqlBuilderImpl::buildTargetFieldSql)
                        .collect(Collectors.joining(", " + String.format("%n"))));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create TABLE SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String dropTableSql(final PostgresContext context) {
        final String targetTableName = context.getTargetTableName();

        final String sql = String.format(DROP_TABLE_TEMPLATE, targetTableName);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop TABLE SQL:%n%s", sql));
        }
        return sql;
    }

    private static String buildTargetIdentityFieldSql(final PostgresField sourceIdentifyField) {
        return String.format("  %s %s",
                sourceIdentifyField.getFieldName(),
                sourceIdentifyField.getFieldType().getPostgresType());
    }

    private static String buildTargetFieldSql(final PostgresMapping targetField) {
        return String.format("  %s %s",
                targetField.getTargetField().getFieldName(),
                targetField.getTargetField().getFieldType().getPostgresType());
    }
}

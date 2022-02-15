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
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TriggerDeleteSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PostgresTriggerDeleteSqlBuilder implements TriggerDeleteSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresTriggerDeleteSqlBuilder.class);

    private static final String CREATE_DELETE_TRIGGER_FUNCTION_TEMPLATE =
            "CREATE FUNCTION spDelete_%s() RETURNS trigger AS %n" +
                    "$$ %n" +
                    "BEGIN %n" +
                    "DELETE FROM %s %n" +
                    "  WHERE %n" +
                    "%s; %n" +
                    "RETURN OLD; %n" +
                    "END; %n" +
                    "$$ LANGUAGE PLPGSQL; %n";

    private static final String CREATE_DELETE_TRIGGER_TEMPLATE =
            "CREATE TRIGGER trgDelete_%s AFTER DELETE OR UPDATE ON %s %n" +
                    "FOR EACH ROW %n" +
                    "%s" +
                    "EXECUTE PROCEDURE spDelete_%s();%n";

    private static final String CREATE_DELETE_TRIGGER_WHEN_TEMPLATE =
            "WHEN %n" +
                    "  ( %n %s %n" +
                    "  ) %n";

    private static final String DROP_DELETE_TRIGGER_FUNCTION_TEMPLATE = "DROP FUNCTION IF EXISTS spDelete_%s; %n";

    private static final String DROP_DELETE_TRIGGER_TEMPLATE = "DROP TRIGGER IF EXISTS trgDelete_%s ON %s; %n";

    @Override
    public String createDeleteTriggerFunctionSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String targetTableName = context.getTargetTableName();
        final List<PostgresField> simpleMappings = new ArrayList<>();
        simpleMappings.addAll(context.getSourceTableIdentityFields());
        simpleMappings.addAll(context
                .getSourceTablePartitionFields()
                .stream()
                .map(pf -> new PostgresField(pf.getFieldName(), pf.getFieldType()))
                .collect(Collectors.toList()));

        final String sql = String.format(CREATE_DELETE_TRIGGER_FUNCTION_TEMPLATE,
                mappingId,
                targetTableName,
                simpleMappings
                        .stream()
                        .map(PostgresTriggerDeleteSqlBuilder::buildTargetIdentityFieldSql)
                        .collect(Collectors.joining(" AND " + String.format("%n"))));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create DELETE TRIGGER FUNCTION SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String createDeleteTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();
        final List<PostgresPartitionField> sourceTablePartitionFields = context.getSourceTablePartitionFields();

        final String sql = String.format(CREATE_DELETE_TRIGGER_TEMPLATE,
                mappingId,
                sourceTableName,
                buildTargetPartitionFieldsSql(sourceTablePartitionFields),
                mappingId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create DELETE TRIGGER SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String dropDeleteTriggerFunctionSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();

        final String sql = String.format(DROP_DELETE_TRIGGER_FUNCTION_TEMPLATE, mappingId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop DELETE TRIGGER FUNCTION SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String dropDeleteTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();

        final String sql = String.format(DROP_DELETE_TRIGGER_TEMPLATE, mappingId, sourceTableName);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop DELETE TRIGGER SQL:%n%s", sql));
        }
        return sql;
    }

    private static String buildTargetIdentityFieldSql(final PostgresField sourceIdentifyField) {
        return String.format("  %s = OLD.%s",
                sourceIdentifyField.getFieldName(),
                sourceIdentifyField.getFieldName());
    }

    private static String buildTargetPartitionFieldsSql(final List<PostgresPartitionField> sourcePartitionFields) {
        if (sourcePartitionFields.isEmpty()) {
            return "";
        }
        return String.format(CREATE_DELETE_TRIGGER_WHEN_TEMPLATE,
                sourcePartitionFields.stream()
                        .map(PostgresTriggerDeleteSqlBuilder::buildTargetPartitionFieldSql)
                        .collect(Collectors.joining(" AND " + String.format("%n"))));
    }

    private static String buildTargetPartitionFieldSql(final PostgresPartitionField sourcePartitionField) {
        return String.format("    OLD.%s = '%s' ",
                sourcePartitionField.getFieldName(),
                sourcePartitionField.getFieldValue());
    }
}

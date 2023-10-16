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
package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TriggerDeleteSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class PostgresTriggerDeleteSqlBuilder implements TriggerDeleteSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresTriggerDeleteSqlBuilder.class);

    private enum PseudoTable {
        OLD,
        NEW
    }

    private static final String CREATE_DELETE_TRIGGER_FUNCTION_TEMPLATE =
            "CREATE FUNCTION spDelete_%s() RETURNS trigger AS %n" +
                    "$$ %n" +
                    "BEGIN %n" +
                    "DELETE FROM %s %n" +
                    "  WHERE %n" +
                    "%s; %n" +
                    "RETURN %s; %n" +
                    "END; %n" +
                    "$$ LANGUAGE PLPGSQL; %n";

    private static final String CREATE_DELETE_TRIGGER_TEMPLATE_FOR_DELETES =
            "CREATE TRIGGER trgDelete_%s AFTER DELETE ON %s %n" +
                    "FOR EACH ROW %n" +
                    "%s" +
                    "EXECUTE PROCEDURE spDelete_%s();%n";

    private static final String CREATE_DELETE_TRIGGER_TEMPLATE_FOR_UPDATES =
            "CREATE TRIGGER trgDelete_%s BEFORE UPDATE ON %s %n" +
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
                .map(pf -> new PostgresField(pf.getFieldName()))
                .collect(Collectors.toList()));

        final StringBuilder sql = new StringBuilder();
        sql.append(createDeleteTriggerFunctionSql(mappingId,
                "DELETES",
                targetTableName,
                simpleMappings,
                PseudoTable.OLD));
        sql.append(createDeleteTriggerFunctionSql(mappingId,
                "UPDATES",
                targetTableName,
                simpleMappings,
                PseudoTable.NEW));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create DELETE TRIGGER FUNCTION SQL:%n%s", sql));
        }

        return sql.toString();
    }

    private String createDeleteTriggerFunctionSql(final String mappingId,
            final String suffix,
            final String targetTableName,
            final List<PostgresField> simpleMappings,
            final PseudoTable type) {
        return String.format(CREATE_DELETE_TRIGGER_FUNCTION_TEMPLATE,
                String.format("%s_%s", mappingId, suffix),
                targetTableName,
                simpleMappings
                        .stream()
                        .map(m -> buildTargetIdentityFieldSql(type, m))
                        .collect(Collectors.joining(" AND " + String.format("%n"))),
                type.name());
    }

    @Override
    public String createDeleteTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();
        final List<PostgresPartitionField> sourceTablePartitionFields = context.getSourceTablePartitionFields();

        final StringBuilder sql = new StringBuilder();
        sql.append(createDeleteTriggerSql(CREATE_DELETE_TRIGGER_TEMPLATE_FOR_DELETES,
                mappingId,
                "DELETES",
                sourceTableName,
                sourceTablePartitionFields,
                PseudoTable.OLD));
        sql.append(createDeleteTriggerSql(CREATE_DELETE_TRIGGER_TEMPLATE_FOR_UPDATES,
                mappingId,
                "UPDATES",
                sourceTableName,
                sourceTablePartitionFields,
                PseudoTable.NEW));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create DELETE TRIGGER SQL:%n%s", sql));
        }

        return sql.toString();
    }

    private String createDeleteTriggerSql(final String template,
            final String mappingId,
            final String suffix,
            final String sourceTableName,
            final List<PostgresPartitionField> sourceTablePartitionFields,
            final PseudoTable type) {
        return String.format(template,
                String.format("%s_%s", mappingId, suffix),
                sourceTableName,
                buildTargetPartitionFieldsSql(type,
                        sourceTablePartitionFields),
                String.format("%s_%s", mappingId, suffix));
    }

    @Override
    public String dropDeleteTriggerFunctionSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();

        final StringBuilder sql = new StringBuilder();
        sql.append(String.format(DROP_DELETE_TRIGGER_FUNCTION_TEMPLATE, mappingId + "_DELETES"));
        sql.append(String.format(DROP_DELETE_TRIGGER_FUNCTION_TEMPLATE, mappingId + "_UPDATES"));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop DELETE TRIGGER FUNCTION SQL:%n%s", sql));
        }

        return sql.toString();
    }

    @Override
    public String dropDeleteTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();

        final StringBuilder sql = new StringBuilder();
        sql.append(String.format(DROP_DELETE_TRIGGER_TEMPLATE, mappingId + "_DELETES", sourceTableName));
        sql.append(String.format(DROP_DELETE_TRIGGER_TEMPLATE, mappingId + "_UPDATES", sourceTableName));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop DELETE TRIGGER SQL:%n%s", sql));
        }

        return sql.toString();
    }

    private static String buildTargetIdentityFieldSql(final PseudoTable type,
            final PostgresField sourceIdentifyField) {
        return String.format("  %s = %s.%s",
                sourceIdentifyField.getFieldName(),
                type.name(),
                sourceIdentifyField.getFieldName());
    }

    private static String buildTargetPartitionFieldsSql(final PseudoTable type,
            final List<PostgresPartitionField> sourcePartitionFields) {
        if (sourcePartitionFields.isEmpty()) {
            return "";
        }
        return String.format(CREATE_DELETE_TRIGGER_WHEN_TEMPLATE,
                sourcePartitionFields.stream()
                        .map(p -> buildTargetPartitionFieldSql(type, p))
                        .collect(Collectors.joining(" AND " + String.format("%n"))));
    }

    private static String buildTargetPartitionFieldSql(final PseudoTable type,
            final PostgresPartitionField sourcePartitionField) {
        return String.format("    %s.%s = '%s' ",
                type.name(),
                sourcePartitionField.getFieldName(),
                sourcePartitionField.getFieldValue());
    }

}

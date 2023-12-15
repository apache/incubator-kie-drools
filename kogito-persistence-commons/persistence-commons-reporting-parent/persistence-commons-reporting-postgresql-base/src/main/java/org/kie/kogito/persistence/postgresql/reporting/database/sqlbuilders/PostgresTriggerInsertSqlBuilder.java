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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.postgresql.reporting.model.paths.PostgresTerminalPathSegment;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.TriggerInsertSqlBuilder;
import org.kie.kogito.persistence.reporting.model.paths.JoinPathSegment;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostgresTriggerInsertSqlBuilder implements TriggerInsertSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresTriggerInsertSqlBuilder.class);

    private static final String CREATE_INSERT_TRIGGER_FUNCTION_TEMPLATE =
            "CREATE FUNCTION spInsert_%s() RETURNS trigger AS %n" +
                    "$$ %n" +
                    "BEGIN %n" +
                    "INSERT INTO %s %n" +
                    "  SELECT %n" +
                    "%s, %n" +
                    "%s %n" +
                    "RETURN NEW; %n" +
                    "END; %n" +
                    "$$ LANGUAGE PLPGSQL; %n";

    private static final String CREATE_INSERT_TRIGGER_TEMPLATE =
            "CREATE TRIGGER trgInsert_%s AFTER INSERT OR UPDATE ON %s %n" +
                    "FOR EACH ROW %n" +
                    "%s" +
                    "EXECUTE PROCEDURE spInsert_%s();%n";

    private static final String CREATE_INSERT_TRIGGER_WHEN_TEMPLATE =
            "WHEN %n" +
                    "  ( %n %s %n" +
                    "  ) %n";

    private static final String DROP_INSERT_TRIGGER_FUNCTION_TEMPLATE = "DROP FUNCTION IF EXISTS spInsert_%s; %n";

    private static final String DROP_INSERT_TRIGGER_TEMPLATE = "DROP TRIGGER IF EXISTS trgInsert_%s ON %s; %n";

    private static final String TRIGGER_PATH_SEGMENT_TEMPLATE = "'%s'";

    private static final String INSERT_TRIGGER_FUNCTION_FIELD_TEMPLATE =
            "  (%s)\\:\\:%s as %s";

    private static final String INSERT_TRIGGER_FUNCTION_FROM_TEMPLATE =
            "  jsonb_array_elements(%n" +
                    "    case jsonb_typeof(%s->'%s') %n" +
                    "      when 'array' then %s->'%s' %n" +
                    "      else jsonb_build_array(%s->'%s')%n" +
                    "    end%n" +
                    "  ) %s";

    @Override
    public String createInsertTriggerFunctionSql(final PostgresContext context) {
        final StringBuilder fieldsSql = new StringBuilder();
        final String mappingId = context.getMappingId();
        final String sourceTableJsonFieldName = context.getSourceTableJsonFieldName();
        final String targetTableName = context.getTargetTableName();
        final List<PathSegment> mappingPaths = context.getMappingPaths();
        final List<PostgresField> simpleMappings = new ArrayList<>();
        simpleMappings.addAll(context.getSourceTableIdentityFields());
        simpleMappings.addAll(context
                .getSourceTablePartitionFields()
                .stream()
                .map(pf -> new PostgresField(pf.getFieldName()))
                .collect(Collectors.toList()));

        final List<PostgresTerminalPathSegment> fields = new ArrayList<>();
        walkMappingPathSegmentsForTerminals(mappingPaths, fields::add);
        if (!fields.isEmpty()) {
            fieldsSql.append(fields.stream()
                    .map(f -> buildTargetFieldMappingSql(f, sourceTableJsonFieldName))
                    .collect(Collectors.joining(", " + String.format("%n"))));
        }

        final List<JoinPathSegment> from = new ArrayList<>();
        for (PathSegment mappingPath : mappingPaths) {
            walkMappingPathSegmentsForJoins(mappingPath, from::add);
        }
        if (from.isEmpty()) {
            fieldsSql.append(";");
        } else {
            fieldsSql.append(String.format("%n  FROM %n%s;",
                    from
                            .stream()
                            .map(f -> buildJoinSql(f, sourceTableJsonFieldName))
                            .collect(Collectors.joining(", " + String.format("%n")))));
        }

        final StringBuilder sql = new StringBuilder();
        sql.append(String.format(CREATE_INSERT_TRIGGER_FUNCTION_TEMPLATE,
                mappingId,
                targetTableName,
                simpleMappings
                        .stream()
                        .map(PostgresTriggerInsertSqlBuilder::buildTargetIdentityFieldSql)
                        .collect(Collectors.joining(", " + String.format("%n"))),
                fieldsSql));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create INSERT TRIGGER FUNCTION SQL:%n%s", sql));
        }
        return sql.toString();
    }

    @Override
    public String createInsertTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();
        final List<PostgresPartitionField> sourceTablePartitionFields = context.getSourceTablePartitionFields();

        final String sql = String.format(CREATE_INSERT_TRIGGER_TEMPLATE,
                mappingId,
                sourceTableName,
                buildTargetPartitionFieldsSql(sourceTablePartitionFields),
                mappingId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create INSERT TRIGGER SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String dropInsertTriggerFunctionSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();

        final String sql = String.format(DROP_INSERT_TRIGGER_FUNCTION_TEMPLATE, mappingId);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop INSERT TRIGGER FUNCTION SQL:%n%s", sql));
        }
        return sql;
    }

    @Override
    public String dropInsertTriggerSql(final PostgresContext context) {
        final String mappingId = context.getMappingId();
        final String sourceTableName = context.getSourceTableName();

        final String sql = String.format(DROP_INSERT_TRIGGER_TEMPLATE, mappingId, sourceTableName);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop INSERT TRIGGER SQL:%n%s", sql));
        }
        return sql;
    }

    private static void walkMappingPathSegmentsForTerminals(final Collection<PathSegment> paths,
            final Consumer<PostgresTerminalPathSegment> consumer) {
        paths.forEach(path -> {
            if (path instanceof PostgresTerminalPathSegment) {
                consumer.accept((PostgresTerminalPathSegment) path);
            }
            walkMappingPathSegmentsForTerminals(path.getChildren(), consumer);
        });
    }

    private static void walkMappingPathSegmentsForJoins(final PathSegment path,
            final Consumer<JoinPathSegment> consumer) {
        if (path instanceof JoinPathSegment) {
            consumer.accept((JoinPathSegment) path);
        }
        path.getChildren().forEach(child -> walkMappingPathSegmentsForJoins(child, consumer));
    }

    private static String buildTargetFieldMappingSql(final PostgresTerminalPathSegment terminalSegment,
            final String sourceTableJsonFieldName) {
        final PostgresMapping targetField = terminalSegment.getMapping();
        final String segmentPathToJoin = getSegmentPathToJoin(terminalSegment, sourceTableJsonFieldName);
        return String.format(INSERT_TRIGGER_FUNCTION_FIELD_TEMPLATE,
                segmentPathToJoin,
                targetField.getTargetField().getFieldType().getPostgresType(),
                targetField.getTargetField().getFieldName());
    }

    private static String buildJoinSql(final JoinPathSegment join,
            final String sourceTableJsonFieldName) {
        final String parentGroupName = getParentGroupName(join, sourceTableJsonFieldName);
        final String sanitizedSegment = join.getSegment().replace("[]", "");
        return String.format(INSERT_TRIGGER_FUNCTION_FROM_TEMPLATE,
                parentGroupName,
                sanitizedSegment,
                parentGroupName,
                sanitizedSegment,
                parentGroupName,
                sanitizedSegment,
                join.getGroupName());
    }

    private static String getParentGroupName(final PathSegment segment,
            final String sourceTableJsonFieldName) {
        String parentGroupName = String.format("NEW.%s", sourceTableJsonFieldName);
        PathSegment parent = segment.getParent();
        while (Objects.nonNull(parent)) {
            if (parent instanceof JoinPathSegment) {
                parentGroupName = ((JoinPathSegment) parent).getGroupName();
                parent = null;
            } else {
                parent = parent.getParent();
            }
        }
        return parentGroupName;
    }

    private static String getSegmentPathToJoin(final PostgresTerminalPathSegment segment,
            final String sourceTableJsonFieldName) {
        final List<String> segmentsToJoin = new ArrayList<>();
        final String parentGroupName = getParentGroupName(segment, sourceTableJsonFieldName);
        PathSegment current = segment;
        while (Objects.nonNull(current)) {
            final String sanitizedSegment = current.getSegment().replace("[]", "");
            if (current instanceof JoinPathSegment) {
                current = null;
            } else {
                segmentsToJoin.add(0, String.format(TRIGGER_PATH_SEGMENT_TEMPLATE, sanitizedSegment));
                current = current.getParent();
            }
        }
        segmentsToJoin.add(0, parentGroupName);
        if (Objects.equals(segment.getMapping().getTargetField().getFieldType(), JsonType.STRING)) {
            final StringBuilder sb = new StringBuilder();
            final int segmentsToJoinCount = segmentsToJoin.size() - 1;
            for (int idx = 0; idx < segmentsToJoinCount; idx++) {
                sb.append(segmentsToJoin.get(idx)).append(idx < segmentsToJoinCount - 1 ? "->" : "->>");
            }
            sb.append(segmentsToJoin.get(segmentsToJoinCount));
            return sb.toString();
        }
        return String.join("->", segmentsToJoin);
    }

    private static String buildTargetIdentityFieldSql(final PostgresField sourceIdentifyField) {
        return String.format("  NEW.%s",
                sourceIdentifyField.getFieldName());
    }

    private static String buildTargetPartitionFieldsSql(final List<PostgresPartitionField> sourcePartitionFields) {
        if (sourcePartitionFields.isEmpty()) {
            return "";
        }
        return String.format(CREATE_INSERT_TRIGGER_WHEN_TEMPLATE,
                sourcePartitionFields.stream()
                        .map(PostgresTriggerInsertSqlBuilder::buildTargetPartitionFieldSql)
                        .collect(Collectors.joining(" AND " + String.format("%n"))));
    }

    private static String buildTargetPartitionFieldSql(final PostgresPartitionField sourcePartitionField) {
        return String.format("    NEW.%s = '%s' ",
                sourcePartitionField.getFieldName(),
                sourcePartitionField.getFieldValue());
    }
}

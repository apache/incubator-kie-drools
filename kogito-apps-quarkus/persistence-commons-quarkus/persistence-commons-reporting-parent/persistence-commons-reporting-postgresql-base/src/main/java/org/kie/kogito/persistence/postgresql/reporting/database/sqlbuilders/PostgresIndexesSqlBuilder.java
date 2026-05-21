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
import java.util.function.Consumer;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.IndexesSqlBuilder;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostgresIndexesSqlBuilder implements IndexesSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresIndexesSqlBuilder.class);

    private static final String CREATE_INDEX_TEMPLATE =
            "CREATE INDEX idx_%s ON %s USING gin ((%s->%s)); %n";

    private static final String INDEX_PATH_SEGMENT_TEMPLATE = "'%s'";

    private static final String DROP_INDEX_TEMPLATE = "DROP INDEX IF EXISTS idx_%s; %n";

    @Override
    public String createTableIndexesSql(final PostgresContext context) {
        final int[] idxCount = { 0 };
        final String sourceTableName = context.getSourceTableName();
        final String sourceTableJsonFieldName = context.getSourceTableJsonFieldName();
        final String targetTableName = context.getTargetTableName();
        final List<PathSegment> mappingPaths = context.getMappingPaths();

        final StringBuilder sql = new StringBuilder();
        for (PathSegment mappingPath : mappingPaths) {
            addIndexSegment(mappingPath,
                    new ArrayList<>(),
                    segments -> sql.append(String.format(CREATE_INDEX_TEMPLATE,
                            targetTableName + "_" + idxCount[0]++,
                            sourceTableName,
                            sourceTableJsonFieldName,
                            String.join("->",
                                    segments))));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Create TABLE INDEXES SQL:%n%s", sql));
        }
        return sql.toString();
    }

    @Override
    public String dropTableIndexesSql(final PostgresContext context) {
        final int[] idxCount = { 0 };
        final String targetTableName = context.getTargetTableName();
        final List<PathSegment> mappingPaths = context.getMappingPaths();

        final StringBuilder sql = new StringBuilder();
        for (PathSegment mappingPath : mappingPaths) {
            addIndexSegment(mappingPath,
                    new ArrayList<>(),
                    segments -> sql.append(String.format(DROP_INDEX_TEMPLATE,
                            targetTableName + "_" + idxCount[0]++)));
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Drop TABLE INDEXES SQL:%n%s", sql));
        }
        return sql.toString();
    }

    private static void addIndexSegment(final PathSegment path,
            final List<String> segments,
            final Consumer<List<String>> consumer) {
        final String sanitizedSegment = path.getSegment().replace("[]", "");
        segments.add(String.format(INDEX_PATH_SEGMENT_TEMPLATE, sanitizedSegment));
        consumer.accept(segments);

        path.getChildren().forEach(child -> addIndexSegment(child,
                new ArrayList<>(segments),
                consumer));
    }
}

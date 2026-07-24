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

import java.util.List;
import java.util.stream.Collectors;

import org.kie.kogito.persistence.postgresql.reporting.model.JsonType;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresJsonField;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresMapping;
import org.kie.kogito.persistence.postgresql.reporting.model.PostgresPartitionField;
import org.kie.kogito.persistence.reporting.database.sqlbuilders.ApplyMappingSqlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostgresApplyMappingSqlBuilder implements ApplyMappingSqlBuilder<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping, PostgresContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresApplyMappingSqlBuilder.class);

    private static final String UPDATE_TABLE_TEMPLATE = "UPDATE %s %n" +
            "SET %s %n" +
            "WHERE %n" +
            "%s;";

    @Override
    public String apply(final PostgresContext context) {
        final String sourceTableName = context.getSourceTableName();
        final List<PostgresField> identityFields = context.getSourceTableIdentityFields();
        final List<PostgresPartitionField> partitionFields = context.getSourceTablePartitionFields();

        final String sql = String.format(UPDATE_TABLE_TEMPLATE,
                sourceTableName,
                identityFields
                        .stream()
                        .map(PostgresApplyMappingSqlBuilder::buildIdentityFieldSql)
                        .collect(Collectors.joining(", " + String.format("%n"))),
                partitionFields
                        .stream()
                        .map(PostgresApplyMappingSqlBuilder::buildPartitionFieldSql)
                        .collect(Collectors.joining(" AND " + String.format("%n"))));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("Apply Mapping SQL:%n%s", sql));
        }

        return sql;
    }

    private static String buildIdentityFieldSql(final PostgresField identifyField) {
        return String.format("%s = %s",
                identifyField.getFieldName(),
                identifyField.getFieldName());
    }

    private static String buildPartitionFieldSql(final PostgresPartitionField partitionField) {
        return String.format("%s = '%s'",
                partitionField.getFieldName(),
                partitionField.getFieldValue());
    }
}

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
package org.kie.kogito.persistence.postgresql.reporting.model;

import java.util.List;

import org.kie.kogito.persistence.reporting.model.BaseMappingDefinition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostgresMappingDefinition extends BaseMappingDefinition<JsonType, PostgresField, PostgresPartitionField, PostgresJsonField, PostgresMapping> {

    PostgresMappingDefinition() {
    }

    public PostgresMappingDefinition(final String mappingId,
            final String sourceTableName,
            final String sourceTableJsonFieldName,
            final List<PostgresField> sourceTableIdentityFields,
            final List<PostgresPartitionField> sourceTablePartitionFields,
            final String targetTableName,
            final List<PostgresMapping> fieldMappings) {
        super(mappingId,
                sourceTableName,
                sourceTableJsonFieldName,
                sourceTableIdentityFields,
                sourceTablePartitionFields,
                targetTableName,
                fieldMappings);
    }
}

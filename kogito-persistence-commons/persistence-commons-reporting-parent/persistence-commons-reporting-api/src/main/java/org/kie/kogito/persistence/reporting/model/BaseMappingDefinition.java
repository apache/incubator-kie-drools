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
package org.kie.kogito.persistence.reporting.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseMappingDefinition<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>> implements MappingDefinition<T, F, P, J, M> {

    public static final String MAPPING_ID_FIELD = "mappingId";
    public static final String SOURCE_TABLE_NAME_FIELD = "sourceTableName";
    public static final String SOURCE_TABLE_JSON_FIELD_NAME_FIELD = "sourceTableJsonFieldName";
    public static final String SOURCE_TABLE_IDENTITY_FIELDS_FIELD = "sourceTableIdentityFields";
    public static final String SOURCE_TABLE_PARTITION_FIELDS_FIELD = "sourceTablePartitionFields";
    public static final String TARGET_TABLE_NAME_FIELD = "targetTableName";
    public static final String FIELD_MAPPINGS_FIELD = "fieldMappings";

    @JsonProperty(MAPPING_ID_FIELD)
    private String mappingId;

    @JsonProperty(SOURCE_TABLE_NAME_FIELD)
    private String sourceTableName;

    @JsonProperty(SOURCE_TABLE_JSON_FIELD_NAME_FIELD)
    private String sourceTableJsonFieldName;

    @JsonProperty(SOURCE_TABLE_IDENTITY_FIELDS_FIELD)
    private List<F> sourceTableIdentityFields;

    @JsonProperty(SOURCE_TABLE_PARTITION_FIELDS_FIELD)
    private List<P> sourceTablePartitionFields;

    @JsonProperty(TARGET_TABLE_NAME_FIELD)
    private String targetTableName;

    @JsonProperty(FIELD_MAPPINGS_FIELD)
    private List<M> fieldMappings;

    protected BaseMappingDefinition() {
    }

    protected BaseMappingDefinition(final String mappingId,
            final String sourceTableName,
            final String sourceTableJsonFieldName,
            final List<F> sourceTableIdentityFields,
            final List<P> sourceTablePartitionFields,
            final String targetTableName,
            final List<M> fieldMappings) {
        this.mappingId = Objects.requireNonNull(mappingId);
        this.sourceTableName = Objects.requireNonNull(sourceTableName);
        this.sourceTableJsonFieldName = Objects.requireNonNull(sourceTableJsonFieldName);
        this.sourceTableIdentityFields = Objects.requireNonNull(sourceTableIdentityFields);
        this.sourceTablePartitionFields = Objects.requireNonNull(sourceTablePartitionFields);
        this.targetTableName = Objects.requireNonNull(targetTableName);
        this.fieldMappings = Objects.requireNonNull(fieldMappings);
    }

    @Override
    public String getMappingId() {
        return mappingId;
    }

    @Override
    public String getSourceTableName() {
        return sourceTableName;
    }

    @Override
    public String getSourceTableJsonFieldName() {
        return sourceTableJsonFieldName;
    }

    @Override
    public List<F> getSourceTableIdentityFields() {
        return sourceTableIdentityFields;
    }

    @Override
    public List<P> getSourceTablePartitionFields() {
        return sourceTablePartitionFields;
    }

    @Override
    public String getTargetTableName() {
        return targetTableName;
    }

    @Override
    public List<M> getFieldMappings() {
        return fieldMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseMappingDefinition)) {
            return false;
        }
        BaseMappingDefinition<?, ?, ?, ?, ?> that = (BaseMappingDefinition<?, ?, ?, ?, ?>) o;
        return getMappingId().equals(that.getMappingId())
                && getSourceTableName().equals(that.getSourceTableName())
                && getSourceTableJsonFieldName().equals(that.getSourceTableJsonFieldName())
                && getSourceTableIdentityFields().equals(that.getSourceTableIdentityFields())
                && Objects.equals(getSourceTablePartitionFields(), that.getSourceTablePartitionFields())
                && getTargetTableName().equals(that.getTargetTableName())
                && getFieldMappings().equals(that.getFieldMappings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMappingId(),
                getSourceTableName(),
                getSourceTableJsonFieldName(),
                getSourceTableIdentityFields(),
                getSourceTablePartitionFields(),
                getTargetTableName(),
                getFieldMappings());
    }
}

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

import java.util.List;
import java.util.Objects;

import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.PartitionField;

public class Validations {

    private Validations() {
        //Static method utility class
    }

    public static String validateMappingId(final String mappingId) {
        if (Objects.isNull(mappingId)) {
            throw new IllegalArgumentException("mappingId cannot be null.");
        }
        if (mappingId.isBlank()) {
            throw new IllegalArgumentException("mappingId cannot be blank.");
        }
        return mappingId;
    }

    public static String validateSourceTableName(final String sourceTableName) {
        if (Objects.isNull(sourceTableName)) {
            throw new IllegalArgumentException("sourceTableName cannot be null.");
        }
        if (sourceTableName.isBlank()) {
            throw new IllegalArgumentException("sourceTableName cannot be blank.");
        }
        return sourceTableName;
    }

    public static String validateSourceTableJsonFieldName(final String sourceTableJsonFieldName) {
        if (Objects.isNull(sourceTableJsonFieldName)) {
            throw new IllegalArgumentException("sourceTableJsonFieldName cannot be null.");
        }
        if (sourceTableJsonFieldName.isBlank()) {
            throw new IllegalArgumentException("sourceTableJsonFieldName cannot be blank.");
        }
        return sourceTableJsonFieldName;
    }

    public static <F extends Field> List<F> validateSourceTableIdentityFields(final List<F> sourceTableIdentityFields) {
        if (Objects.isNull(sourceTableIdentityFields)) {
            throw new IllegalArgumentException("sourceTableIdentityFields cannot be null.");
        }
        if (sourceTableIdentityFields.isEmpty()) {
            throw new IllegalArgumentException("At least one Source Table Identity Field must be defined.");
        }
        sourceTableIdentityFields.forEach(sourceTableIdentityField -> {
            if (Objects.isNull(sourceTableIdentityField.getFieldName())) {
                throw new IllegalArgumentException("sourceTableIdentityField.fieldName cannot be null.");
            }
            if (sourceTableIdentityField.getFieldName().isBlank()) {
                throw new IllegalArgumentException("sourceTableIdentityField.fieldName cannot be blank.");
            }
        });
        return sourceTableIdentityFields;
    }

    public static <P extends PartitionField> List<P> validateSourceTablePartitionFields(final List<P> sourceTablePartitionFields) {
        if (Objects.isNull(sourceTablePartitionFields)) {
            throw new IllegalArgumentException("sourceTablePartitionFields cannot be null.");
        }
        sourceTablePartitionFields.forEach(sourceTablePartitionField -> {
            if (Objects.isNull(sourceTablePartitionField.getFieldName())) {
                throw new IllegalArgumentException("sourceTablePartitionField.fieldName cannot be null.");
            }
            if (sourceTablePartitionField.getFieldName().isBlank()) {
                throw new IllegalArgumentException("sourceTablePartitionField.fieldName cannot be blank.");
            }
            if (Objects.isNull(sourceTablePartitionField.getFieldValue())) {
                throw new IllegalArgumentException("sourceTablePartitionField.fieldValue cannot be null.");
            }
            if (sourceTablePartitionField.getFieldValue().isBlank()) {
                throw new IllegalArgumentException("sourceTablePartitionField.fieldValue cannot be blank.");
            }
        });
        return sourceTablePartitionFields;
    }

    public static String validateTargetTableName(final String targetTableName) {
        if (Objects.isNull(targetTableName)) {
            throw new IllegalArgumentException("targetTableName cannot be null.");
        }
        if (targetTableName.isBlank()) {
            throw new IllegalArgumentException("targetTableName cannot be blank.");
        }
        return targetTableName;
    }

    public static <T, J extends JsonField<T>, M extends Mapping<T, J>> List<M> validateFieldMappings(final List<M> fieldMappings) {
        if (Objects.isNull(fieldMappings)) {
            throw new IllegalArgumentException("fieldMappings cannot be null.");
        }
        if (fieldMappings.isEmpty()) {
            throw new IllegalArgumentException("At least one Field Mapping must be defined.");
        }
        fieldMappings.forEach(fieldMapping -> {
            if (fieldMapping.getSourceJsonPath().isBlank()) {
                throw new IllegalArgumentException("Mapping.sourceJsonPath cannot be blank.");
            }
            if (Objects.isNull(fieldMapping.getTargetField())) {
                throw new IllegalArgumentException("Mapping.targetField cannot be blank.");
            }
            final JsonField<T> field = fieldMapping.getTargetField();
            if (Objects.isNull(field.getFieldName())) {
                throw new IllegalArgumentException("Mapping.targetField.fieldName cannot be null.");
            }
            if (field.getFieldName().isBlank()) {
                throw new IllegalArgumentException("Mapping.targetField.fieldName cannot be blank.");
            }
            if (Objects.isNull(field.getFieldName())) {
                throw new IllegalArgumentException("Mapping.targetField.fieldName cannot be null.");
            }
            if (Objects.isNull(field.getFieldType())) {
                throw new IllegalArgumentException("Mapping.targetField.fieldType cannot be blank.");
            }
        });
        return fieldMappings;
    }
}

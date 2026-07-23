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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseJsonField<T> implements JsonField<T> {

    public static final String FIELD_NAME_FIELD = "fieldName";
    public static final String FIELD_TYPE_FIELD = "fieldType";

    @JsonProperty(FIELD_NAME_FIELD)
    String fieldName;

    @JsonProperty(FIELD_TYPE_FIELD)
    T fieldType;

    protected BaseJsonField() {
    }

    protected BaseJsonField(final String fieldName,
            final T fieldType) {
        this.fieldName = Objects.requireNonNull(fieldName);
        this.fieldType = Objects.requireNonNull(fieldType);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public T getFieldType() {
        return fieldType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseJsonField<?> mapping = (BaseJsonField<?>) o;
        return fieldName.equals(mapping.fieldName)
                && fieldType.equals(mapping.fieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType);
    }
}

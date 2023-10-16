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
public abstract class BasePartitionField extends BaseField implements PartitionField {

    public static final String FIELD_VALUE_FIELD = "fieldValue";

    @JsonProperty(FIELD_VALUE_FIELD)
    String fieldValue;

    protected BasePartitionField() {
    }

    protected BasePartitionField(final String fieldName,
            final String fieldValue) {
        super(fieldName);
        this.fieldValue = Objects.requireNonNull(fieldValue);
    }

    @Override
    public String getFieldValue() {
        return fieldValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BasePartitionField)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BasePartitionField that = (BasePartitionField) o;
        return getFieldValue().equals(that.getFieldValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFieldValue());
    }
}

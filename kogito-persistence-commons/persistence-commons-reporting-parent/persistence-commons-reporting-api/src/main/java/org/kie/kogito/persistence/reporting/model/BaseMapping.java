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
public abstract class BaseMapping<T, J extends JsonField<T>> implements Mapping<T, J> {

    public static final String SOURCE_JSON_PATH_FIELD = "sourceJsonPath";
    public static final String TARGET_FIELD_FIELD = "targetField";

    @JsonProperty(SOURCE_JSON_PATH_FIELD)
    private String sourceJsonPath;

    @JsonProperty(TARGET_FIELD_FIELD)
    private J targetField;

    protected BaseMapping() {
    }

    protected BaseMapping(final String sourceJsonPath,
            final J targetField) {
        this.sourceJsonPath = Objects.requireNonNull(sourceJsonPath);
        this.targetField = Objects.requireNonNull(targetField);
    }

    @Override
    public String getSourceJsonPath() {
        return sourceJsonPath;
    }

    @Override
    public J getTargetField() {
        return targetField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseMapping)) {
            return false;
        }
        BaseMapping<?, ?> that = (BaseMapping<?, ?>) o;
        return getSourceJsonPath().equals(that.getSourceJsonPath())
                && getTargetField().equals(that.getTargetField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSourceJsonPath(),
                getTargetField());
    }
}

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

import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseMappingDefinitions<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>, D extends MappingDefinition<T, F, P, J, M>>
        implements MappingDefinitions<T, F, P, J, M, D> {

    public static final String MAPPING_DEFINITIONS_FIELD = "mappingDefinitions";

    @JsonProperty(MAPPING_DEFINITIONS_FIELD)
    private Collection<D> mappingDefinitions;

    protected BaseMappingDefinitions() {
    }

    protected BaseMappingDefinitions(final Collection<D> mappingDefinitions) {
        this.mappingDefinitions = Objects.requireNonNull(mappingDefinitions);
    }

    @Override
    public Collection<D> getMappingDefinitions() {
        return mappingDefinitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MappingDefinitions<?, ?, ?, ?, ?, ?> that = (MappingDefinitions<?, ?, ?, ?, ?, ?>) o;
        return getMappingDefinitions().equals(that.getMappingDefinitions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMappingDefinitions());
    }
}

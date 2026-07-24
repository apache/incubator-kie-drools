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
package org.kie.kogito.persistence.reporting.database.sqlbuilders;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.persistence.reporting.model.BaseMappingDefinition;
import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.PartitionField;
import org.kie.kogito.persistence.reporting.model.paths.PathSegment;

public abstract class BaseContext<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>> extends BaseMappingDefinition<T, F, P, J, M>
        implements Context<T, F, P, J, M> {

    private final List<PathSegment> mappingPaths;
    private final Map<String, String> sourceTableFieldTypes;

    protected BaseContext(final String mappingId,
            final String sourceTableName,
            final String sourceTableJsonFieldName,
            final List<F> sourceTableIdentityFields,
            final List<P> sourceTablePartitionFields,
            final String targetTableName,
            final List<M> mappings,
            final List<PathSegment> mappingPaths,
            final Map<String, String> sourceTableFieldTypes) {
        super(mappingId,
                sourceTableName,
                sourceTableJsonFieldName,
                sourceTableIdentityFields,
                sourceTablePartitionFields,
                targetTableName,
                mappings);
        this.mappingPaths = Objects.requireNonNull(mappingPaths);
        this.sourceTableFieldTypes = Objects.requireNonNull(sourceTableFieldTypes);
    }

    @Override
    public List<PathSegment> getMappingPaths() {
        return mappingPaths;
    }

    @Override
    public Map<String, String> getSourceTableFieldTypes() {
        return sourceTableFieldTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseContext)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BaseContext<?, ?, ?, ?, ?> that = (BaseContext<?, ?, ?, ?, ?>) o;
        return getMappingPaths().equals(that.getMappingPaths())
                && getSourceTableFieldTypes().equals(that.getSourceTableFieldTypes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                getMappingPaths(),
                getSourceTableFieldTypes());
    }
}

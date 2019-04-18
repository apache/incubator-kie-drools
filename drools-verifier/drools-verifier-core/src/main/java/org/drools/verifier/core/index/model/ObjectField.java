/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.index.model;

import java.util.Objects;
import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.meta.ConditionParentType;

public class ObjectField
        extends FieldBase
        implements ConditionParentType {

    private final Optional<FieldRange> range;

    public ObjectField(final String factType,
                       final String fieldType,
                       final String name,
                       final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
        range = Optional.empty();
    }

    public ObjectField(final String factType,
                       final String fieldType,
                       final String name,
                       final FieldRange fieldRange,
                       final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
        this.range = Optional.of(fieldRange);
    }

    public Optional<FieldRange> getRange() {
        return range;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ObjectField that = (ObjectField) o;
        return range.equals(that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), range);
    }
}

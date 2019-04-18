/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.validation.dtanalysis.verifier;

import java.util.Objects;
import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.FieldRange;
import org.drools.verifier.core.index.model.meta.ConditionParentBase;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;

public class StructureField
        implements ConditionParentType {

    private static final KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("name")
            .updatable()
            .build();

    private final UUIDKey uuidKey;
    private final String name;
    private final String fieldType;

    public StructureField(final String name,
                          final String fieldType,
                          final AnalyzerConfiguration configuration) {
        this.name = PortablePreconditions.checkNotNull("name",
                                                       name);
        this.fieldType = PortablePreconditions.checkNotNull("fieldType",
                                                            fieldType);
        this.uuidKey = configuration.getUUID(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFieldType() {
        return fieldType;
    }

    @Override
    public Optional<FieldRange> getRange() {
        return Optional.empty();
    }

    @Override
    public int compareTo(final ConditionParentBase o) {
        if (o instanceof StructureField) {
            final StructureField other = (StructureField) o;
            if (Objects.equals(fieldType, other.fieldType)
                    && Objects.equals(name, other.name)) {
                return 0;
            } else if (fieldType.equals(other.fieldType)) {
                return name.compareTo(other.name);
            } else {
                return fieldType.compareTo(other.fieldType);
            }
        } else {
            return -1;
        }
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(NAME_KEY_DEFINITION,
                        name)
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StructureField that = (StructureField) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(fieldType, that.fieldType);
    }

    @Override
    public int hashCode() {
        int result = ~~fieldType.hashCode();
        result = 31 * result + ~~name.hashCode();
        return ~~result;
    }

    @Override
    public String toString() {
        return "StructureField{" +
                "name='" + name + '\'' +
                '}';
    }
}

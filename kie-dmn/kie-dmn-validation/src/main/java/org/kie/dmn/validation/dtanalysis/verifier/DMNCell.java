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

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Actions;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.FieldBase;
import org.drools.verifier.core.index.model.meta.ConditionParent;
import org.drools.verifier.core.index.model.meta.ConditionParentBase;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;

public class DMNCell
        implements ConditionParent {

    private static final KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("name")
            .updatable()
            .build();

    private final Conditions conditions;
    private final Actions actions;
    private final UUIDKey uuidKey;
    private String name;
    private ConditionParentType conditionParentType;

    public DMNCell(final String name,
                   final ConditionParentType conditionParentType,
                   final AnalyzerConfiguration configuration) {
        this.name = PortablePreconditions.checkNotNull("name",
                                                       name);
        this.conditionParentType = PortablePreconditions.checkNotNull("conditionParentType",
                                                                      conditionParentType);
        this.conditions = new Conditions(configuration.getConditionKeyDefinitions());
        this.actions = new Actions(configuration.getActionKeyDefinitions());
        this.uuidKey = configuration.getUUID(this);
    }

    @Override
    public Conditions getConditions() {
        return conditions;
    }

    @Override
    public Actions getActions() {
        return actions;
    }

    @Override
    public String getFieldType() {
        return name;
    }

    @Override
    public ConditionParentType getConditionParentType() {
        return conditionParentType;
    }

    @Override
    public int compareTo(ConditionParentBase conditionParentBase) {
        if (conditionParentBase instanceof FieldBase) {
            final DMNCell dmnCell = (DMNCell) conditionParentBase;
            return name.compareTo(dmnCell.name);
        } else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DMNCell dmnCell = (DMNCell) o;
        return Objects.equals(name, dmnCell.name) &&
                Objects.equals(conditionParentType, dmnCell.conditionParentType);
    }

    @Override
    public int hashCode() {
        int result = ~~name.hashCode();
        result = 31 * result + ~~conditionParentType.hashCode();
        return ~~result;
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
    public String toString() {
        return "DMNCell{" +
                "name='" + name + '\'' +
                ", conditionParentType=" + conditionParentType +
                '}';
    }

    @Override
    public String toHumanReadableString() {
        return name;
    }
}

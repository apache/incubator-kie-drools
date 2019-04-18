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

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.meta.ConditionParent;
import org.drools.verifier.core.index.model.meta.ConditionParentType;
import org.drools.verifier.core.util.PortablePreconditions;

/**
 * Actual field of a Pattern instance.
 */
public class Field
        extends FieldBase
        implements ConditionParent {

    private final ConditionParentType conditionParentType;
    private final Conditions conditions;
    private final Actions actions;

    public Field(final ConditionParentType conditionParentType,
                 final String factType,
                 final String fieldType,
                 final String name,
                 final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
        this.conditions = new Conditions(configuration.getConditionKeyDefinitions());
        this.actions = new Actions(configuration.getActionKeyDefinitions());
        this.conditionParentType = PortablePreconditions.checkNotNull("conditionParentType",
                                                                      conditionParentType);
    }

    @Override
    public ConditionParentType getConditionParentType() {
        return conditionParentType;
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
    public String toHumanReadableString() {
        return new StringBuilder().append(getFactType()).append(".").append(getName()).toString();
    }
}

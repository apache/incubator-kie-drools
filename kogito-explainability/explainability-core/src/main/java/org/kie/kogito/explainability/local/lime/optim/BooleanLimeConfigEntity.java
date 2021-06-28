/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.lime.optim;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class BooleanLimeConfigEntity extends LimeConfigEntity {

    public BooleanLimeConfigEntity() {
        super();
    }

    public BooleanLimeConfigEntity(String name, Boolean proposedValue) {
        super(name, proposedValue);
    }

    @ValueRangeProvider(id = "booleanRange")
    public ValueRange<Boolean> getValueRange() {
        return ValueRangeFactory.createBooleanValueRange();
    }

    @PlanningVariable(valueRangeProviderRefs = { "booleanRange" })
    public Boolean getProposedValue() {
        return (Boolean) proposedValue;
    }

    public void setProposedValue(Boolean proposedValue) {
        this.proposedValue = proposedValue;
    }

    @Override
    double asDouble() {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean asBoolean() {
        return (boolean) proposedValue;
    }
}

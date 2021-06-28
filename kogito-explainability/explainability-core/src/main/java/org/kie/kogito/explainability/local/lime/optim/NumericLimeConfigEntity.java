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
public class NumericLimeConfigEntity extends LimeConfigEntity {

    private double rangeMinimum;
    private double rangeMaximum;

    public NumericLimeConfigEntity() {
        super();
    }

    @Override
    double asDouble() {
        return (double) proposedValue;
    }

    @Override
    boolean asBoolean() {
        throw new UnsupportedOperationException();
    }

    public NumericLimeConfigEntity(String name, double proposedValue, double rangeMinimum, double rangeMaximum) {
        super(name, proposedValue);
        this.rangeMinimum = rangeMinimum;
        this.rangeMaximum = rangeMaximum;
    }

    @ValueRangeProvider(id = "doubleRange")
    public ValueRange<Double> getValueRange() {
        return ValueRangeFactory.createDoubleValueRange(rangeMinimum, rangeMaximum);
    }

    @PlanningVariable(valueRangeProviderRefs = { "doubleRange" })
    public Double getProposedValue() {
        return (double) proposedValue;
    }

    public void setProposedValue(Double proposedValue) {
        this.proposedValue = proposedValue;
    }

}

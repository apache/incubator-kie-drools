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
package org.kie.kogito.explainability.local.counterfactual.entities;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Mapping between a boolean feature an OptaPlanner {@link PlanningEntity}
 */
@PlanningEntity
public class BooleanEntity implements CounterfactualEntity {
    @PlanningVariable(valueRangeProviderRefs = {"booleanRange"})
    private Boolean proposedValue;

    private boolean constrained;
    private Boolean originalValue;
    private String featureName;

    public BooleanEntity() {
    }

    private BooleanEntity(Boolean originalValue, String featureName, boolean constrained) {
        this.proposedValue = originalValue;
        this.originalValue = originalValue;
        this.featureName = featureName;
        this.constrained = constrained;
    }

    /**
     * Creates a {@link BooleanEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is contrained or not.
     *
     * @param originalFeature Original input {@link Feature}
     * @param constrained     Whether this entity's value should be fixed or not
     */
    public static BooleanEntity from(Feature originalFeature, boolean constrained) {
        return new BooleanEntity((Boolean) originalFeature.getValue().getUnderlyingObject(), originalFeature.getName(), constrained);
    }

    /**
     * Creates an unconstrained {@link BooleanEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param originalFeature feature Original input {@link Feature}
     */
    public static BooleanEntity from(Feature originalFeature) {
        return BooleanEntity.from(originalFeature, false);
    }

    /**
     * Calculates the distance between the current planning value and the reference value
     * for this feature.
     *
     * @return Numerical distance
     */
    public double distance() {
        return proposedValue.equals(originalValue) ? 0.0 : 1.0;
    }

    /**
     * Returns the {@link BooleanEntity} as a {@link Feature}
     *
     * @return {@link Feature}
     */
    @Override
    public Feature asFeature() {
        return FeatureFactory.newBooleanFeature(this.featureName, this.proposedValue);
    }

    @Override
    public boolean isConstrained() {
        return constrained;
    }

    /**
     * Returns whether the {@link BooleanEntity} new value is different from the reference
     * {@link Feature} value.
     *
     * @return boolean
     */
    @Override
    public boolean isChanged() {
        return !this.originalValue.equals(this.proposedValue);
    }

    @ValueRangeProvider(id = "booleanRange")
    public ValueRange getValueRange() {
        return ValueRangeFactory.createBooleanValueRange();
    }

    @Override
    public String toString() {
        return "BooleanEntity{" + "value=" + proposedValue + ", id='" + featureName + '\'' + '}';
    }
}

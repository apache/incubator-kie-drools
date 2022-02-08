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
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Mapping between an integer feature an OptaPlanner {@link PlanningEntity}
 */

@PlanningEntity
public class IntegerEntity extends AbstractNumericEntity<Integer> {

    public IntegerEntity() {
        super();
    }

    private IntegerEntity(Integer originalValue, String featureName, int minimum, int maximum,
            FeatureDistribution featureDistribution, boolean constrained) {
        super(originalValue, featureName, minimum, maximum, featureDistribution, constrained);
    }

    /**
     * Creates a {@link IntegerEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param constrained Whether this entity's value should be fixed or not
     */
    public static IntegerEntity from(Feature originalFeature, int minimum, int maximum, boolean constrained) {
        return from(originalFeature, minimum, maximum, null, constrained);
    }

    public static IntegerEntity from(Feature originalFeature, int minimum, int maximum, FeatureDistribution featureDistribution,
            boolean constrained) {
        return new IntegerEntity((int) originalFeature.getValue().asNumber(), originalFeature.getName(), minimum, maximum,
                featureDistribution, constrained);
    }

    /**
     * Creates an unconstrained {@link IntegerEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param feature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     */
    public static IntegerEntity from(Feature feature, int minimum, int maximum) {
        return IntegerEntity.from(feature, minimum, maximum, null, false);
    }

    /**
     * Creates an unconstrained {@link IntegerEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param feature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     */
    public static IntegerEntity from(Feature feature, int minimum, int maximum, FeatureDistribution featureDistribution) {
        return IntegerEntity.from(feature, minimum, maximum, featureDistribution, false);
    }

    @ValueRangeProvider(id = "intRange")
    public ValueRange<Integer> getValueRange() {
        return ValueRangeFactory.createIntValueRange(rangeMinimum, rangeMaximum);
    }

    @PlanningVariable(valueRangeProviderRefs = { "intRange" })
    public Integer getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(Integer proposedValue) {
        this.proposedValue = proposedValue;
    }
}

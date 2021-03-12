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
 * Mapping between a Double feature an OptaPlanner {@link PlanningEntity}
 */
@PlanningEntity
public class DoubleEntity extends AbstractNumericEntity<Double> {

    public DoubleEntity() {
        super();
    }

    private DoubleEntity(Double originalValue, String featureName, double minimum, double maximum, FeatureDistribution featureDistribution, boolean constrained) {
        super(originalValue, featureName, minimum, maximum, featureDistribution, constrained);
    }

    /**
     * Creates a {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param constrained Whether this entity's value should be fixed or not
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, boolean constrained) {
        return from(originalFeature, minimum, maximum, null, constrained);
    }

    /**
     * Creates a {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     * @param constrained Whether this entity's value should be fixed or not
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, FeatureDistribution featureDistribution, boolean constrained) {
        return new DoubleEntity(originalFeature.getValue().asNumber(), originalFeature.getName(), minimum, maximum, featureDistribution, constrained);
    }

    /**
     * Creates an unconstrained {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum) {
        return DoubleEntity.from(originalFeature, minimum, maximum, null, false);
    }

    /**
     * Creates an unconstrained {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature}.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, FeatureDistribution featureDistribution) {
        return DoubleEntity.from(originalFeature, minimum, maximum, featureDistribution, false);
    }

    @ValueRangeProvider(id = "doubleRange")
    public ValueRange<Double> getValueRange() {
        return ValueRangeFactory.createDoubleValueRange(rangeMinimum, rangeMaximum);
    }

    @PlanningVariable(valueRangeProviderRefs = { "doubleRange" })
    public Double getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(Double proposedValue) {
        this.proposedValue = proposedValue;
    }
}
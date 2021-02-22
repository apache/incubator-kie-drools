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
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Mapping between a Double feature an OptaPlanner {@link PlanningEntity}
 */
@PlanningEntity
public class DoubleEntity implements CounterfactualEntity {
    @PlanningVariable(valueRangeProviderRefs = {"doubleRange"})
    public Double proposedValue;

    double doubleRangeMinimum;
    double doubleRangeMaximum;
    private Double stdDev = null;

    private Double originalValue;

    private boolean constrained;
    private String featureName;


    public DoubleEntity() {
    }

    private DoubleEntity(Double originalValue, String featureName, double minimum, double maximum, boolean constrained) {
        this(originalValue, featureName, minimum, maximum, null, constrained);
    }

    private DoubleEntity(Double originalValue, String featureName, double minimum, double maximum, FeatureDistribution featureDistribution, boolean constrained) {
        this.proposedValue = originalValue;
        this.originalValue = originalValue;
        this.featureName = featureName;
        this.doubleRangeMinimum = minimum;
        this.doubleRangeMaximum = maximum;
        this.constrained = constrained;
        if (featureDistribution != null) {
            final double[] samples = featureDistribution.getAllSamples().stream().mapToDouble(Value::asNumber).toArray();
            final double mean = DataUtils.getMean(samples);
            this.stdDev = DataUtils.getStdDev(samples, mean);
        }
    }

    /**
     * Creates a {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum         The start of the domain search space
     * @param maximum         The end of the domain search space
     * @param constrained     Whether this entity's value should be fixed or not
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, boolean constrained) {
        return from(originalFeature, minimum, maximum, null, constrained);
    }

    /**
     * Creates a {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature     Original input {@link Feature}
     * @param minimum             The start of the domain search space
     * @param maximum             The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     * @param constrained         Whether this entity's value should be fixed or not
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, FeatureDistribution featureDistribution, boolean constrained) {
        return new DoubleEntity(originalFeature.getValue().asNumber(), originalFeature.getName(), minimum, maximum, featureDistribution, constrained);
    }


    /**
     * Creates an unconstrained {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param minimum         The start of the domain search space
     * @param maximum         The end of the domain search space
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum) {
        return DoubleEntity.from(originalFeature, minimum, maximum, null, false);
    }

    /**
     * Creates an unconstrained {@link DoubleEntity}, taking the original input value from the
     * provided {@link Feature}.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature     feature Original input {@link Feature}
     * @param minimum             The start of the domain search space
     * @param maximum             The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     */
    public static DoubleEntity from(Feature originalFeature, double minimum, double maximum, FeatureDistribution featureDistribution) {
        return DoubleEntity.from(originalFeature, minimum, maximum, featureDistribution, false);
    }


    @ValueRangeProvider(id = "doubleRange")
    public ValueRange getValueRange() {
        return ValueRangeFactory.createDoubleValueRange(doubleRangeMinimum, doubleRangeMaximum);
    }

    @Override
    public String toString() {
        return "DoubleFeature{"
                + "value="
                + proposedValue
                + ", doubleRangeMinimum="
                + doubleRangeMinimum
                + ", doubleRangeMaximum="
                + doubleRangeMaximum
                + ", id='"
                + featureName
                + '\''
                + '}';
    }

    /**
     * Calculates the distance between the current planning value and the reference value
     * for this feature.
     * If the feature distribution is specified, this will return a scaled distance, otherwise
     * it returns an unscaled distance.
     *
     * @return Numerical distance
     */
    @Override
    public double distance() {
        double distance = Math.abs(this.proposedValue - originalValue);
        if (this.stdDev != null) {
            return distance / (this.stdDev * this.stdDev);
        } else {
            return distance;
        }
    }

    /**
     * Returns the {@link BooleanEntity} as a {@link Feature}
     *
     * @return {@link Feature}
     */
    @Override
    public Feature asFeature() {
        return FeatureFactory.newNumericalFeature(featureName, this.proposedValue);
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
        return !originalValue.equals(this.proposedValue);
    }
}
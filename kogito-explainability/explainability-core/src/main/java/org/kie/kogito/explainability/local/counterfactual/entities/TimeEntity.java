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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * Mapping between a Duration feature an OptaPlanner {@link PlanningEntity}
 */
@PlanningEntity
public class TimeEntity extends AbstractAlgebraicEntity<LocalTime> {

    public TimeEntity() {
        super();
    }

    private TimeEntity(LocalTime originalValue, String featureName, LocalTime minimum, LocalTime maximum,
            FeatureDistribution featureDistribution, boolean constrained) {
        super(originalValue, featureName, minimum, maximum, constrained);
        this.range = (double) (minimum.until(maximum, ChronoUnit.SECONDS));
    }

    /**
     * Creates a {@link TimeEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param constrained Whether this entity's value should be fixed or not
     */
    public static TimeEntity from(Feature originalFeature, LocalTime minimum, LocalTime maximum, boolean constrained) {
        return from(originalFeature, minimum, maximum, null, constrained);
    }

    /**
     * Creates a {@link TimeEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     * @param constrained Whether this entity's value should be fixed or not
     */
    public static TimeEntity from(Feature originalFeature, LocalTime minimum, LocalTime maximum,
            FeatureDistribution featureDistribution, boolean constrained) {
        return new TimeEntity((LocalTime) originalFeature.getValue().getUnderlyingObject(), originalFeature.getName(), minimum,
                maximum, featureDistribution, constrained);
    }

    /**
     * Creates an unconstrained {@link TimeEntity}, taking the original input value from the
     * provided {@link Feature}.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     */
    public static TimeEntity from(Feature originalFeature, LocalTime minimum, LocalTime maximum) {
        return TimeEntity.from(originalFeature, minimum, maximum, null, false);
    }

    /**
     * Creates an unconstrained {@link TimeEntity}, taking the original input value from the
     * provided {@link Feature}.
     * If the feature distribution is available, it will be used to scale the feature distances.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param minimum The start of the domain search space
     * @param maximum The end of the domain search space
     * @param featureDistribution The feature's distribution (as {@link FeatureDistribution}), if available
     */
    public static TimeEntity from(Feature originalFeature, LocalTime minimum, LocalTime maximum,
            FeatureDistribution featureDistribution) {
        return TimeEntity.from(originalFeature, minimum, maximum, featureDistribution, false);
    }

    @ValueRangeProvider(id = "timeRange")
    public ValueRange<Double> getValueRange() {
        final double minimum = LocalTime.MIN.until(rangeMinimum, ChronoUnit.SECONDS);
        final double maximum = LocalTime.MIN.until(rangeMaximum, ChronoUnit.SECONDS);
        return ValueRangeFactory.createDoubleValueRange(minimum, maximum);
    }

    @PlanningVariable(valueRangeProviderRefs = { "timeRange" })
    public LocalTime getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(LocalTime proposedValue) {
        this.proposedValue = proposedValue;
    }

    @Override
    public double distance() {
        return Math.abs(this.proposedValue.until(this.originalValue, ChronoUnit.SECONDS));

    }

    @Override
    public Feature asFeature() {
        return FeatureFactory.newTimeFeature(featureName, this.proposedValue);
    }

    @Override
    public double similarity() {
        return 1.0 - Math.abs(this.proposedValue.until(this.originalValue, ChronoUnit.SECONDS)) / this.range;
    }
}
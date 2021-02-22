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
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.Set;

/**
 * Mapping between a categorical feature an OptaPlanner {@link PlanningEntity}
 */
@PlanningEntity
public class CategoricalEntity implements CounterfactualEntity {
    @PlanningVariable(valueRangeProviderRefs = {"categoricalRange"})
    public String proposedValue;

    private String originalValue;

    private boolean constrained;
    private String featureName;

    private Set<String> allowedCategories;

    public CategoricalEntity() {
    }

    private CategoricalEntity(String originalValue, String featureName, Set<String> allowedCategories, boolean constrained) {
        this.proposedValue = originalValue;
        this.originalValue = originalValue;
        this.featureName = featureName;
        this.allowedCategories = allowedCategories;
        this.constrained = constrained;
    }

    /**
     * Creates a {@link CategoricalEntity}, taking the original input value from the
     * provided {@link Feature} and specifying whether the entity is constrained or not.
     * A set of allowed category values must be passed.
     *
     * @param originalFeature Original input {@link Feature}
     * @param categories      Set of allowed category values
     * @param constrained     Whether this entity's value should be fixed or not
     */
    public static CategoricalEntity from(Feature originalFeature, Set<String> categories, boolean constrained) {
        return new CategoricalEntity(originalFeature.getValue().asString(), originalFeature.getName(), categories, constrained);
    }

    /**
     * Creates an unconstrained {@link CategoricalEntity}, taking the original input value from the
     * provided {@link Feature}.
     * A set of allowed category values must be passed.
     *
     * @param originalFeature feature Original input {@link Feature}
     * @param categories      Set of allowed category values
     */
    public static CategoricalEntity from(Feature originalFeature, Set<String> categories) {
        return CategoricalEntity.from(originalFeature, categories, false);
    }


    @ValueRangeProvider(id = "categoricalRange")
    public Set<String> getValueRange() {
        return allowedCategories;
    }

    @Override
    public String toString() {
        return "CategoricalFeature{"
                + "value="
                + proposedValue
                + ", id='"
                + featureName
                + '\''
                + '}';
    }

    /**
     * Calculates the distance between the current planning value and the reference value
     * for this feature.
     *
     * @return Numerical distance
     */
    @Override
    public double distance() {
        return proposedValue.equals(originalValue) ? 0.0 : 1.0;
    }

    /**
     * Returns the {@link CategoricalEntity} as a {@link Feature}
     *
     * @return {@link Feature}
     */
    @Override
    public Feature asFeature() {
        return FeatureFactory.newCategoricalFeature(featureName, this.proposedValue);
    }

    @Override
    public boolean isConstrained() {
        return constrained;
    }

    /**
     * Returns whether the {@link CategoricalEntity} new value is different from the reference
     * {@link Feature} value.
     *
     * @return boolean
     */
    @Override
    public boolean isChanged() {
        return !originalValue.equals(this.proposedValue);
    }
}

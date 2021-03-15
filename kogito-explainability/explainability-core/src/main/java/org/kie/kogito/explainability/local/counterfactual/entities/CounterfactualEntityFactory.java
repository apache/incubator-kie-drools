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
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.FeatureDomain;

public class CounterfactualEntityFactory {

    private CounterfactualEntityFactory() {
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain) {
        return CounterfactualEntityFactory.from(feature, isConstrained, featureDomain, null);
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain, FeatureDistribution featureDistribution) {
        CounterfactualEntity entity = null;
        if (feature.getType() == Type.NUMBER) {
            if (feature.getValue().getUnderlyingObject() instanceof Double) {
                entity = DoubleEntity.from(feature, featureDomain.getLowerBound(), featureDomain.getUpperBound(), featureDistribution, isConstrained);
            } else if (feature.getValue().getUnderlyingObject() instanceof Integer) {
                entity = IntegerEntity.from(feature, featureDomain.getLowerBound().intValue(), featureDomain.getUpperBound().intValue(), featureDistribution, isConstrained);
            }
        } else if (feature.getType() == Type.BOOLEAN) {
            entity = BooleanEntity.from(feature, isConstrained);
        } else if (feature.getType() == Type.CATEGORICAL) {
            entity = CategoricalEntity.from(feature, featureDomain.getCategories(), isConstrained);
        } else {
            throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
        }
        return entity;
    }
}
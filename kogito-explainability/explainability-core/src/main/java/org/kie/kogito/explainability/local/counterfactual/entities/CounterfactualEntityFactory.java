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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.FeatureDomain;

public class CounterfactualEntityFactory {

    private CounterfactualEntityFactory() {
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain) {
        return CounterfactualEntityFactory.from(feature, isConstrained, featureDomain, null);
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain,
            FeatureDistribution featureDistribution) {
        CounterfactualEntity entity = null;
        if (feature.getType() == Type.NUMBER) {
            if (feature.getValue().getUnderlyingObject() instanceof Double) {
                if (isConstrained) {
                    entity = FixedDoubleEntity.from(feature);
                } else {
                    entity = DoubleEntity.from(feature, featureDomain.getLowerBound(), featureDomain.getUpperBound(),
                            featureDistribution, isConstrained);
                }
            } else if (feature.getValue().getUnderlyingObject() instanceof Integer) {
                if (isConstrained) {
                    entity = FixedIntegerEntity.from(feature);
                } else {
                    entity = IntegerEntity.from(feature, featureDomain.getLowerBound().intValue(),
                            featureDomain.getUpperBound().intValue(), featureDistribution, isConstrained);
                }
            }
        } else if (feature.getType() == Type.BOOLEAN) {
            if (isConstrained) {
                entity = FixedBooleanEntity.from(feature);
            } else {
                entity = BooleanEntity.from(feature, isConstrained);
            }

        } else if (feature.getType() == Type.CATEGORICAL) {
            if (isConstrained) {
                entity = FixedCategoricalEntity.from(feature);
            } else {
                entity = CategoricalEntity.from(feature, featureDomain.getCategories(), isConstrained);
            }
        } else {
            throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
        }
        return entity;
    }

    public static List<CounterfactualEntity> createEntities(PredictionInput predictionInput,
            PredictionFeatureDomain featureDomain, List<Boolean> constraints, DataDistribution dataDistribution) {
        final List<FeatureDomain> domains = featureDomain.getFeatureDomains();
        return IntStream.range(0, predictionInput.getFeatures().size())
                .mapToObj(featureIndex -> {
                    final Feature feature = predictionInput.getFeatures().get(featureIndex);
                    final Boolean isConstrained = constraints.get(featureIndex);
                    final FeatureDomain domain = domains.get(featureIndex);
                    final FeatureDistribution featureDistribution = Optional
                            .ofNullable(dataDistribution)
                            .map(dd -> dd.asFeatureDistributions().get(featureIndex))
                            .orElse(null);
                    return CounterfactualEntityFactory
                            .from(feature, isConstrained, domain, featureDistribution);
                }).collect(Collectors.toList());
    }
}
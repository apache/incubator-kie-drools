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

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBinaryEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedBooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCompositeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedCurrencyEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedDurationEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedIntegerEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedObjectEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTextEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedTimeEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedURIEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.fixed.FixedVectorEntity;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.PredictionFeatureDomain;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.BinaryFeatureDomain;
import org.kie.kogito.explainability.model.domain.CategoricalFeatureDomain;
import org.kie.kogito.explainability.model.domain.CurrencyFeatureDomain;
import org.kie.kogito.explainability.model.domain.DurationFeatureDomain;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.ObjectFeatureDomain;
import org.kie.kogito.explainability.model.domain.URIFeatureDomain;

public class CounterfactualEntityFactory {

    private CounterfactualEntityFactory() {
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain) {
        return CounterfactualEntityFactory.from(feature, isConstrained, featureDomain, null);
    }

    public static CounterfactualEntity from(Feature feature, Boolean isConstrained, FeatureDomain featureDomain,
            FeatureDistribution featureDistribution) {
        CounterfactualEntity entity = null;
        validateFeature(feature);
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

        } else if (feature.getType() == Type.TEXT) {
            if (isConstrained) {
                entity = FixedTextEntity.from(feature);
            } else {
                throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
            }

        } else if (feature.getType() == Type.BINARY) {
            if (isConstrained) {
                entity = FixedBinaryEntity.from(feature);
            } else {
                entity = BinaryEntity.from(feature, ((BinaryFeatureDomain) featureDomain).getCategories(), isConstrained);
            }

        } else if (feature.getType() == Type.URI) {
            if (isConstrained) {
                entity = FixedURIEntity.from(feature);
            } else {
                entity = URIEntity.from(feature, ((URIFeatureDomain) featureDomain).getCategories(), isConstrained);
            }

        } else if (feature.getType() == Type.TIME) {
            if (isConstrained) {
                entity = FixedTimeEntity.from(feature);
            } else {
                final LocalTime lowerBound = LocalTime.MIN.plusSeconds(featureDomain.getLowerBound().longValue());
                final LocalTime upperBound = LocalTime.MIN.plusSeconds(featureDomain.getUpperBound().longValue());
                entity = TimeEntity.from(feature, lowerBound, upperBound, isConstrained);
            }

        } else if (feature.getType() == Type.DURATION) {
            if (isConstrained) {
                entity = FixedDurationEntity.from(feature);
            } else {
                DurationFeatureDomain domain = (DurationFeatureDomain) featureDomain;
                entity = DurationEntity.from(feature, Duration.of(domain.getLowerBound().longValue(), domain.getUnit()),
                        Duration.of(domain.getUpperBound().longValue(), domain.getUnit()),
                        featureDistribution, isConstrained);
            }

        } else if (feature.getType() == Type.VECTOR) {
            if (isConstrained) {
                entity = FixedVectorEntity.from(feature);
            } else {
                throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
            }

        } else if (feature.getType() == Type.COMPOSITE) {
            if (isConstrained) {
                entity = FixedCompositeEntity.from(feature);
            } else {
                throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
            }

        } else if (feature.getType() == Type.CURRENCY) {
            if (isConstrained) {
                entity = FixedCurrencyEntity.from(feature);
            } else {
                entity = CurrencyEntity.from(feature, ((CurrencyFeatureDomain) featureDomain).getCategories(), isConstrained);
            }

        } else if (feature.getType() == Type.CATEGORICAL) {
            if (isConstrained) {
                entity = FixedCategoricalEntity.from(feature);
            } else {
                entity = CategoricalEntity.from(feature, ((CategoricalFeatureDomain) featureDomain).getCategories(),
                        isConstrained);
            }
        } else if (feature.getType() == Type.UNDEFINED) {
            if (isConstrained) {
                entity = FixedObjectEntity.from(feature);
            } else {
                entity = ObjectEntity.from(feature, ((ObjectFeatureDomain) featureDomain).getCategories(), isConstrained);
            }
        } else {
            throw new IllegalArgumentException("Unsupported feature type: " + feature.getType());
        }
        return entity;
    }

    /**
     * Validation of features for counterfactual entity construction
     *
     * @param feature {@link Feature} to be validated
     */
    public static void validateFeature(Feature feature) {
        final Type type = feature.getType();
        final Object object = feature.getValue().getUnderlyingObject();
        if (type == Type.NUMBER) {
            if (object == null) {
                throw new IllegalArgumentException("Null numeric features are not supported in counterfactuals");
            }
        }
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
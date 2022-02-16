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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.NumericFeatureDistribution;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.explainability.utils.CompositeFeatureUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositeEntityTest {

    private static Feature generateCompositeFeature() {
        Map<String, Object> map = new HashMap<>();
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 10.0));
        features.add(FeatureFactory.newNumericalFeature("f2", 11.2));
        features.add(FeatureFactory.newNumericalFeature("f3", 5));
        features.add(FeatureFactory.newBooleanFeature("f4", true));
        features.add(FeatureFactory.newBooleanFeature("f5", false));
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("nf-1", FeatureFactory.newNumericalFeature("nff-1", 101.1));
        nestedMap.put("nf-2", FeatureFactory.newNumericalFeature("nff-2", 15.0));
        features.add(FeatureFactory.newCompositeFeature("f7", nestedMap));
        for (Feature f : features) {
            map.put(f.getName(), f.getValue().getUnderlyingObject());
        }
        Feature feature = FeatureFactory.newCompositeFeature("some-name", map);
        return feature;
    }

    @Test
    void testBasicSerDe() {
        final List<Feature> features = new ArrayList<>();
        final Feature compositeFeature = generateCompositeFeature();
        features.add(compositeFeature);

        final List<Feature> flattened = CompositeFeatureUtils.flattenFeatures(features);

        final List<Feature> delinearised = CompositeFeatureUtils.unflattenFeatures(flattened, features);

        assertEquals(features, delinearised);
    }

    @Test
    void distanceUnscaled() {
        final FeatureDomain featureDomain = NumericalFeatureDomain.create(0.0, 40.0);
        final Feature doubleFeature = FeatureFactory.newNumericalFeature("feature-double", 20.0, featureDomain);
        DoubleEntity entity = (DoubleEntity) CounterfactualEntityFactory.from(doubleFeature);
        entity.proposedValue = 30.0;
        assertEquals(10.0, entity.distance());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void distanceScaled(int seed) {
        Random random = new Random();
        random.setSeed(seed);

        final Feature doubleFeature =
                FeatureFactory.newNumericalFeature("feature-double", 20.0, NumericalFeatureDomain.create(0.0, 40.0));
        final FeatureDistribution featureDistribution =
                new NumericFeatureDistribution(doubleFeature, random.doubles(5000, 10.0, 40.0).toArray());
        DoubleEntity entity = (DoubleEntity) CounterfactualEntityFactory.from(doubleFeature, featureDistribution);
        entity.proposedValue = 30.0;
        final double distance = entity.distance();
        assertTrue(distance > 0.1 && distance < 0.2);
    }
}
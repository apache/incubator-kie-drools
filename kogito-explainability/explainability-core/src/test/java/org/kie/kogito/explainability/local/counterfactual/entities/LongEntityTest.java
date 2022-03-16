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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongEntityTest {

    @Test
    void distanceUnscaled() {
        final FeatureDomain featureDomain = NumericalFeatureDomain.create(0, 100);
        final Feature feature = FeatureFactory.newNumericalFeature("feature-long", 20L, featureDomain);
        LongEntity entity = (LongEntity) CounterfactualEntityFactory.from(feature);
        entity.proposedValue = 40L;
        assertEquals(20.0, entity.distance());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void distanceScaled(int seed) {
        Random random = new Random();
        random.setSeed(seed);

        final FeatureDomain featureDomain = NumericalFeatureDomain.create(0, 100);
        final Feature feature = FeatureFactory.newNumericalFeature("feature-long", 20L, featureDomain);
        final FeatureDistribution featureDistribution = new NumericFeatureDistribution(feature, random.longs(5000, 10, 40).mapToDouble(x -> x).toArray());

        LongEntity entity = (LongEntity) CounterfactualEntityFactory.from(feature, featureDistribution);
        entity.proposedValue = 40L;
        final double distance = entity.distance();
        assertTrue(distance > 0.2 && distance < 0.3);
    }
}

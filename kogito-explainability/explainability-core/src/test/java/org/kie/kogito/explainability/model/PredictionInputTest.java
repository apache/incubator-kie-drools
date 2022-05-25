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
package org.kie.kogito.explainability.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.kie.kogito.explainability.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PredictionInputTest {

    private final PerturbationContext context = new PerturbationContext(new Random(), 1);

    @ParameterizedTest
    @EnumSource
    void testEqualitySingleFeature(Type type) {
        Value v = type.randomValue(context);
        Feature mockedFeature = TestUtils.getMockedFeature(type, v);
        PredictionInput input1 = new PredictionInput(List.of(mockedFeature));
        PredictionInput input2 = new PredictionInput(List.of(mockedFeature));
        assertThat(input1).isEqualTo(input2).isEqualTo(input1);
    }

    @ParameterizedTest
    @EnumSource
    void testEqualityMultipleFeatures(Type type) {
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Value v = type.randomValue(context);
            Feature feature = TestUtils.getMockedFeature(type, v);
            features.add(feature);
        }
        PredictionInput input1 = new PredictionInput(features);
        PredictionInput input2 = new PredictionInput(features);
        assertThat(input1).isEqualTo(input2).isEqualTo(input1);
    }

    @Test
    void testInequality() {
        PredictionInput input1 = new PredictionInput(Collections.emptyList());
        Feature feature = mock(Feature.class);
        PredictionInput input2 = new PredictionInput(List.of(feature));
        assertThat(input1).isNotEqualTo(input2).isNotEqualTo(null);
    }

    @Test
    void testGetByNameEmpty() {
        PredictionInput input1 = new PredictionInput(Collections.emptyList());
        assertThat(input1.getFeatureByName("foo")).isEmpty();
    }

    @Test
    void testGetByNameMissing() {
        List<Feature> features = new ArrayList<>();
        features.add(TestUtils.getMockedNumericFeature(10));
        PredictionInput input1 = new PredictionInput(features);
        assertThat(input1.getFeatureByName("foo")).isNotPresent();
    }

    @Test
    void testGetByNameFound() {
        List<Feature> features = new ArrayList<>();
        features.add(TestUtils.getMockedNumericFeature(10));
        PredictionInput input1 = new PredictionInput(features);
        assertThat(input1.getFeatureByName("f-num")).isPresent();
    }
}

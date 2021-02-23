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
package org.kie.kogito.explainability.local.lime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.Feature;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IndependentSparseFeatureBalanceFilterTest {

    @Test
    void testNoFilterWithNonMatchingSizes() {
        IndependentSparseFeatureBalanceFilter filter = new IndependentSparseFeatureBalanceFilter();
        int size = 3;
        double[] coefficients = new double[size];
        Arrays.fill(coefficients, 1);
        double[] copy = Arrays.copyOf(coefficients, 3);
        List<Feature> features = new ArrayList<>(1);
        features.add(TestUtils.getMockedNumericFeature());
        List<Pair<double[], Double>> trainingSet = new ArrayList<>();
        double[] sampleWeights = new double[size];
        TestUtils.fillBalancedDataForFiltering(size, trainingSet, sampleWeights);
        filter.apply(coefficients, features, trainingSet);
        assertThat(coefficients).isEqualTo(copy);
    }

    @Test
    void testFilter() {
        IndependentSparseFeatureBalanceFilter filter = new IndependentSparseFeatureBalanceFilter();
        int size = 2;
        double[] coefficients = new double[size];
        Arrays.fill(coefficients, 1);
        double[] copy = Arrays.copyOf(coefficients, size);
        List<Feature> features = new ArrayList<>(1);
        features.add(TestUtils.getMockedNumericFeature());
        features.add(TestUtils.getMockedNumericFeature());
        List<Pair<double[], Double>> trainingSet = new ArrayList<>();
        double[] sampleWeights = new double[size];
        TestUtils.fillBalancedDataForFiltering(size, trainingSet, sampleWeights);
        filter.apply(coefficients, features, trainingSet);
        assertThat(coefficients).isNotEqualTo(copy);
    }

}
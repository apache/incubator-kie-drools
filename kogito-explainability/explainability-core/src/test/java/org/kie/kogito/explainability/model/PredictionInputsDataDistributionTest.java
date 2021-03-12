/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictionInputsDataDistributionTest {

    @Test
    void testSample() {
        List<PredictionInput> inputs = new ArrayList<>(3);
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("foo"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("bar"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("asd"))));
        PredictionInputsDataDistribution predictionInputsDataDistribution = new PredictionInputsDataDistribution(inputs);
        PredictionInput sample = predictionInputsDataDistribution.sample();
        assertNotNull(sample);
        assertTrue(inputs.contains(sample));
    }

    @Test
    void testSamples() {
        List<PredictionInput> inputs = new ArrayList<>(3);
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("foo"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("bar"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("asd"))));
        PredictionInputsDataDistribution predictionInputsDataDistribution = new PredictionInputsDataDistribution(inputs);
        assertEquals(3, predictionInputsDataDistribution.getAllSamples().size());
        List<PredictionInput> samples = predictionInputsDataDistribution.sample(2);
        assertNotNull(samples);
        assertEquals(2, samples.size());
        for (PredictionInput sample : samples) {
            assertTrue(inputs.contains(sample));
        }
    }

    @Test
    void testLargerSamples() {
        List<PredictionInput> inputs = new ArrayList<>(3);
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("foo"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("bar"))));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("asd"))));
        PredictionInputsDataDistribution predictionInputsDataDistribution = new PredictionInputsDataDistribution(inputs);
        List<PredictionInput> samples = predictionInputsDataDistribution.sample(12);
        assertNotNull(samples);
        assertEquals(12, samples.size());
        for (PredictionInput sample : samples) {
            assertTrue(inputs.contains(sample));
        }
    }

    @Test
    void testAsFeatureDistributions() {
        List<PredictionInput> inputs = new ArrayList<>(3);
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("foo"), TestUtils.getMockedNumericFeature())));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("bar"), TestUtils.getMockedNumericFeature())));
        inputs.add(new PredictionInput(List.of(TestUtils.getMockedTextFeature("asd"), TestUtils.getMockedNumericFeature())));
        PredictionInputsDataDistribution predictionInputsDataDistribution = new PredictionInputsDataDistribution(inputs);
        List<FeatureDistribution> featureDistributions = predictionInputsDataDistribution.asFeatureDistributions();
        assertNotNull(featureDistributions);
        assertEquals(2, featureDistributions.size());
        for (FeatureDistribution featureDistribution : featureDistributions) {
            assertNotNull(featureDistribution.getFeature());
            List<Value> allSamples = featureDistribution.getAllSamples();
            assertNotNull(allSamples);
            assertEquals(3, allSamples.size());
        }
    }
}
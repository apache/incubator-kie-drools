/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatasetTest {

    @Test
    void testEmpty() {
        List<Prediction> predictions = new ArrayList<>();
        Dataset dataset = new Dataset(predictions);
        assertThat(dataset.getData()).isEmpty();
        assertThat(dataset.getInputs()).isEmpty();
        assertThat(dataset.getOutputs()).isEmpty();
    }

    @Test
    void testNotEmpty() {
        List<Prediction> predictions = new ArrayList<>();
        predictions.add(new SimplePrediction(new PredictionInput(List.of(TestUtils.getMockedNumericFeature())),
                new PredictionOutput(List.of(new Output("name", Type.UNDEFINED)))));
        Dataset dataset = new Dataset(predictions);
        assertThat(dataset.getData()).isNotEmpty();
        assertThat(dataset.getInputs()).isNotEmpty();
        assertThat(dataset.getOutputs()).isNotEmpty();
    }

    @Test
    void testInputFilter() {
        List<Prediction> predictions = new ArrayList<>();
        predictions.add(new SimplePrediction(new PredictionInput(List.of(TestUtils.getMockedNumericFeature())),
                new PredictionOutput(List.of(new Output("name", Type.UNDEFINED)))));
        Dataset filteredDataset1 = new Dataset(predictions).filterByInput(pi -> pi.getFeatures().size() == 1);
        assertThat(filteredDataset1.getData()).isNotEmpty();
        assertThat(filteredDataset1.getInputs()).isNotEmpty();
        assertThat(filteredDataset1.getOutputs()).isNotEmpty();

        Dataset filteredDataset2 = new Dataset(predictions).filterByInput(pi -> pi.getFeatures().size() == 2);
        assertThat(filteredDataset2.getData()).isEmpty();
        assertThat(filteredDataset2.getInputs()).isEmpty();
        assertThat(filteredDataset2.getOutputs()).isEmpty();
    }

    @Test
    void testOutFilter() {
        List<Prediction> predictions = new ArrayList<>();
        predictions.add(new SimplePrediction(new PredictionInput(List.of(TestUtils.getMockedNumericFeature())),
                new PredictionOutput(List.of(new Output("name", Type.UNDEFINED)))));
        Dataset filteredDataset1 = new Dataset(predictions).filterByOutput(po -> po.getOutputs().size() == 1);
        assertThat(filteredDataset1.getData()).isNotEmpty();
        assertThat(filteredDataset1.getInputs()).isNotEmpty();
        assertThat(filteredDataset1.getOutputs()).isNotEmpty();

        Dataset filteredDataset2 = new Dataset(predictions).filterByOutput(po -> po.getOutputs().size() == 2);
        assertThat(filteredDataset2.getData()).isEmpty();
        assertThat(filteredDataset2.getInputs()).isEmpty();
        assertThat(filteredDataset2.getOutputs()).isEmpty();
    }

    private Dataset createDatasetFeatureFiltering(Random random) {
        List<Prediction> predictions = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            final List<Feature> features = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                features.add(FeatureFactory.newNumericalFeature("f-" + j, random.nextDouble() * 100.0));
            }

            for (int j = 4; j < 8; j++) {
                features.add(FeatureFactory.newNumericalFeature("f-" + j, 100.0 + random.nextDouble() * 100.0));
            }

            features.add(FeatureFactory.newBooleanFeature("f-8", true));
            features.add(FeatureFactory.newTextFeature("f-9", UUID.randomUUID().toString()));

            PredictionOutput output = new PredictionOutput(List.of(new Output("output", Type.BOOLEAN, new Value(false), 1.0)));
            predictions.add(new SimplePrediction(new PredictionInput(features), output));
        }
        return new Dataset(predictions);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testFilterByFeatureName(int seed) {
        Random random = new Random(seed);
        final Dataset dataset = createDatasetFeatureFiltering(random);

        // Total dataset entries size
        assertEquals(1000, dataset.getData().size());
        // Number of features per entry for random entry
        final int index = random.nextInt(dataset.getData().size());
        assertEquals(10, dataset.getData().get(index).getInput().getFeatures().size());

        // Filter by name, remove all "f-3" features
        Predicate<Feature> featureName = (Feature f) -> f.getName().equals("f-3");
        final Dataset filteredDataset = dataset.filterByFeature(featureName.negate());

        assertEquals(1000, filteredDataset.getData().size());
        assertEquals(9, filteredDataset.getData().get(index).getInput().getFeatures().size());

        final List<String> names = filteredDataset
                .getData()
                .get(index)
                .getInput()
                .getFeatures()
                .stream()
                .map(Feature::getName)
                .collect(Collectors.toList());

        assertFalse(names.contains("f-3"));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testFilterByFeatureType(int seed) {
        Random random = new Random(seed);
        final Dataset dataset = createDatasetFeatureFiltering(random);

        // Total dataset entries size
        assertEquals(1000, dataset.getData().size());
        // Number of features per entry for random entry
        final int index = random.nextInt(dataset.getData().size());
        assertEquals(10, dataset.getData().get(index).getInput().getFeatures().size());

        // Filter by name, remove all numerical features
        Predicate<Feature> featureType = (Feature f) -> f.getType().equals(Type.NUMBER);
        final Dataset filteredDataset = dataset.filterByFeature(featureType.negate());

        assertEquals(1000, filteredDataset.getData().size());
        assertEquals(2, filteredDataset.getData().get(index).getInput().getFeatures().size());

        final List<Type> types = filteredDataset
                .getData()
                .get(index)
                .getInput()
                .getFeatures()
                .stream()
                .map(Feature::getType)
                .collect(Collectors.toList());

        assertFalse(types.contains(Type.NUMBER));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void testFilterByFeatureValue(int seed) {
        Random random = new Random(seed);
        final Dataset dataset = createDatasetFeatureFiltering(random);

        // Total dataset entries size
        assertEquals(1000, dataset.getData().size());
        // Number of features per entry for random entry
        final int index = random.nextInt(dataset.getData().size());
        assertEquals(10, dataset.getData().get(index).getInput().getFeatures().size());

        // Filter by name, remove all numerical features
        Predicate<Feature> featureValue = (Feature f) -> f.getValue().asNumber() > 100.0;
        final Dataset filteredDataset = dataset.filterByFeature(featureValue);

        assertEquals(1000, filteredDataset.getData().size());
        assertEquals(4, filteredDataset.getData().get(index).getInput().getFeatures().size());

        final List<Double> values = filteredDataset
                .getData()
                .get(index)
                .getInput()
                .getFeatures()
                .stream()
                .map(Feature::getValue)
                .map(Value::asNumber)
                .collect(Collectors.toList());

        assertTrue(values.stream().allMatch(x -> x > 100.0));

        final List<Type> types = filteredDataset
                .getData()
                .get(index)
                .getInput()
                .getFeatures()
                .stream()
                .map(Feature::getType)
                .collect(Collectors.toList());

        assertTrue(types.stream().allMatch(x -> x.equals(Type.NUMBER)));
    }

}
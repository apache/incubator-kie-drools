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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;

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

}
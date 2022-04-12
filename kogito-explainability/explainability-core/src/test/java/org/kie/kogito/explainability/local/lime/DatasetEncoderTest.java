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
package org.kie.kogito.explainability.local.lime;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.EncodingParams;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatasetEncoderTest {

    @Test
    void testEmptyDatasetEncoding() {
        List<PredictionInput> inputs = new LinkedList<>();
        List<Output> outputs = new LinkedList<>();
        List<Feature> features = new LinkedList<>();
        Output originalOutput = new Output("foo", Type.NUMBER, new Value(1), 1d);
        EncodingParams params = new EncodingParams(1, 0.1);
        DatasetEncoder datasetEncoder = new DatasetEncoder(inputs, outputs, features, originalOutput, params);
        Collection<Pair<double[], Double>> trainingSet = datasetEncoder.getEncodedTrainingSet();
        assertNotNull(trainingSet);
        assertTrue(trainingSet.isEmpty());
    }

    @Test
    void testDatasetEncodingWithBinaryData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                ByteBuffer byteBuffer = ByteBuffer.wrap((i + "" + j).getBytes(Charset.defaultCharset()));
                inputFeatures.add(TestUtils.getMockedFeature(Type.BINARY, new Value(byteBuffer)));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            ByteBuffer byteBuffer = ByteBuffer.wrap((i + "" + i).getBytes(Charset.defaultCharset()));
            features.add(TestUtils.getMockedFeature(Type.BINARY, new Value(byteBuffer)));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    @Test
    void testDatasetEncodingWithVectorData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                double[] doubles = new double[2];
                doubles[0] = i;
                doubles[1] = j;
                inputFeatures.add(TestUtils.getMockedFeature(Type.VECTOR, new Value(doubles)));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            double[] doubles = new double[2];
            doubles[0] = i;
            doubles[1] = i;
            features.add(TestUtils.getMockedFeature(Type.BINARY, new Value(doubles)));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    @Test
    void testDatasetEncodingWithCategoricalData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                double[] doubles = new double[2];
                inputFeatures.add(TestUtils.getMockedFeature(Type.CATEGORICAL, new Value(i + "" + j)));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            features.add(TestUtils.getMockedFeature(Type.CATEGORICAL, new Value(i + "" + i)));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    @Test
    void testDatasetEncodingWithBooleanData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                inputFeatures.add(TestUtils.getMockedFeature(Type.BOOLEAN, new Value(j % 2 == 0)));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            features.add(TestUtils.getMockedFeature(Type.BOOLEAN, new Value(i % 2 == 0)));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    @Test
    void testDatasetEncodingWithNumericData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                inputFeatures.add(TestUtils.getMockedNumericFeature(i + j));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    @Test
    void testDatasetEncodingWithTextData() {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            List<Feature> inputFeatures = new LinkedList<>();
            for (int j = 0; j < 3; j++) {
                inputFeatures.add(TestUtils.getMockedTextFeature(i + " " + j));
            }
            perturbedInputs.add(new PredictionInput(inputFeatures));
        }

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            features.add(TestUtils.getMockedTextFeature(i + " " + i));
        }
        PredictionInput originalInput = new PredictionInput(features);
        assertEncode(perturbedInputs, originalInput);
    }

    private void assertEncode(List<PredictionInput> perturbedInputs, PredictionInput originalInput) {
        List<Output> outputs = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            outputs.add(new Output("o", Type.NUMBER, new Value(i % 2 == 0 ? 1d : 0d), 1d));
        }
        Output originalOutput = new Output("o", Type.BOOLEAN, new Value(1d), 1d);
        EncodingParams params = new EncodingParams(1, 0.1);
        DatasetEncoder datasetEncoder = new DatasetEncoder(perturbedInputs, outputs, originalInput.getFeatures(),
                originalOutput, params);
        Collection<Pair<double[], Double>> trainingSet = datasetEncoder.getEncodedTrainingSet();
        assertNotNull(trainingSet);
        assertEquals(10, trainingSet.size());
        for (Pair<double[], Double> pair : trainingSet) {
            assertNotNull(pair.getKey());
            assertNotNull(pair.getValue());
            assertThat(pair.getValue()).isBetween(0d, 1d);
        }
    }
}
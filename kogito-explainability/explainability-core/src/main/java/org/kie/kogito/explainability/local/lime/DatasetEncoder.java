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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.LinearModel;

/**
 * Encoder algorithm to transform perturbed inputs and outputs into a training set that the {@link LinearModel} can use.
 * The target inputs and output are needed in order to distinguish when the value of a certain feature corresponds or
 * is close to the one of the prediction to be explained.
 */
class DatasetEncoder {

    private static final double CLUSTER_THRESHOLD = 1e-3;

    private final List<PredictionInput> perturbedInputs;
    private final List<Output> predictedOutputs;
    private final PredictionInput targetInput;
    private final Output originalOutput;

    DatasetEncoder(List<PredictionInput> perturbedInputs, List<Output> perturbedOutputs,
                   PredictionInput targetInput, Output targetOutput) {
        this.perturbedInputs = perturbedInputs;
        this.predictedOutputs = perturbedOutputs;
        this.targetInput = targetInput;
        this.originalOutput = targetOutput;
    }

    /**
     * Get the input and output predictions transformed into a numerical training set.
     *
     * @return a numerical training set
     */
    List<Pair<double[], Double>> getEncodedTrainingSet() {
        List<Pair<double[], Double>> trainingSet = new LinkedList<>();
        List<List<Double>> columnData;
        List<PredictionInput> flatInputs = DataUtils.linearizeInputs(perturbedInputs);
        if (!flatInputs.isEmpty() && !predictedOutputs.isEmpty() && !targetInput.getFeatures().isEmpty() && originalOutput != null) {
            columnData = getColumnData(flatInputs);

            int pi = 0;
            for (Output output : predictedOutputs) {
                double[] x = new double[columnData.size()];
                int i = 0;
                for (List<Double> column : columnData) {
                    x[i] = column.get(pi);
                    i++;
                }
                double y;
                if (Type.NUMBER.equals(originalOutput.getType()) || Type.BOOLEAN.equals(originalOutput.getType())) {
                    y = output.getValue().asNumber();
                } else {
                    Object originalObject = originalOutput.getValue().getUnderlyingObject();
                    Object outputObject = output.getValue().getUnderlyingObject();
                    if (originalObject == null || outputObject == null) {
                        y = originalObject == outputObject ? 1d : 0d;
                    } else {
                        y = originalObject.equals(outputObject) ? 1d : 0d;
                    }
                }
                Pair<double[], Double> sample = new ImmutablePair<>(x, y);
                trainingSet.add(sample);

                pi++;
            }
        }
        return trainingSet;
    }

    private List<List<Double>> getColumnData(List<PredictionInput> perturbedInputs) {
        List<List<Double>> columnData = new LinkedList<>();

        for (int t = 0; t < targetInput.getFeatures().size(); t++) {
            Feature originalFeature = targetInput.getFeatures().get(t);
            switch (originalFeature.getType()) {
                case NUMBER:
                    encodeNumbers(perturbedInputs, targetInput, columnData, t);
                    break;
                case TEXT:
                    encodeText(perturbedInputs, columnData, originalFeature);
                    break;
                case CATEGORICAL:
                case BINARY:
                case TIME:
                case URI:
                case DURATION:
                case VECTOR:
                case CURRENCY:
                case UNDEFINED:
                    encodeEquals(perturbedInputs, columnData, t, originalFeature);
                    break;
                case BOOLEAN:
                    // boolean are automatically encoded as 1s or 0s
                    List<Double> featureValues = new LinkedList<>();
                    for (PredictionInput pi : perturbedInputs) {
                        featureValues.add(pi.getFeatures().get(t).getValue().asNumber());
                    }
                    columnData.add(featureValues);
                    break;
                default:
                    throw new LocalExplanationException("could not encoded features of type " + originalFeature.getType());
            }
        }
        return columnData;
    }

    private static void encodeNumbers(List<PredictionInput> predictionInputs, PredictionInput originalInputs, List<List<Double>> columnData, int t) {
        // find maximum and minimum values
        double[] doubles = new double[predictionInputs.size() + 1];
        int i = 0;
        for (PredictionInput pi : predictionInputs) {
            Feature feature = pi.getFeatures().get(t);
            doubles[i] = feature.getValue().asNumber();
            i++;
        }
        Feature feature = originalInputs.getFeatures().get(t);
        double originalValue = feature.getValue().asNumber();
        doubles[i] = originalValue;
        double min = DoubleStream.of(doubles).min().getAsDouble();
        double max = DoubleStream.of(doubles).max().getAsDouble();
        // feature scaling + kernel based clustering
        double threshold = DataUtils.gaussianKernel((originalValue - min) / (max - min), 0, 1);
        List<Double> featureValues = DoubleStream.of(doubles).map(d -> (d - min) / (max - min))
                .map(d -> Double.isNaN(d) ? 1 : d).boxed().map(d -> DataUtils.gaussianKernel(d, 0, 1))
                .map(d -> (d - threshold < CLUSTER_THRESHOLD) ? 1d : 0d).collect(Collectors.toList());
        columnData.add(featureValues);
    }

    private static void encodeText(List<PredictionInput> predictionInputs, List<List<Double>> columnData, Feature originalFeature) {
        String originalString = originalFeature.getValue().asString();
        String[] words = originalString.split(" ");
        for (String word : words) {
            List<Double> featureValues = new LinkedList<>();
            for (PredictionInput pi : predictionInputs) {
                Feature feature = pi.getFeatures().stream().filter(f -> f.getName().equals(originalFeature.getName())).findFirst().orElse(null);
                double featureValue;
                if (feature != null && feature.getName().equals(originalFeature.getName())) {
                    String perturbedString = feature.getValue().asString();
                    String[] perturbedWords = perturbedString.split(" ");
                    featureValue = 0d;
                    for (String w : perturbedWords) {
                        if (w.equals(word)) {
                            featureValue = 1d;
                            break;
                        }
                    }
                } else {
                    featureValue = 0d;
                }
                featureValues.add(featureValue);
            }
            columnData.add(featureValues);
        }
    }

    private static void encodeEquals(List<PredictionInput> predictionInputs, List<List<Double>> columnData, int t, Feature originalFeature) {
        Object originalObject = originalFeature.getValue().getUnderlyingObject();
        List<Double> featureValues = new LinkedList<>();
        for (PredictionInput pi : predictionInputs) {
            double featureValue = originalObject.equals(pi.getFeatures().get(t).getValue().getUnderlyingObject()) ? 1d : 0d;
            featureValues.add(featureValue);
        }
        columnData.add(featureValues);
    }
}

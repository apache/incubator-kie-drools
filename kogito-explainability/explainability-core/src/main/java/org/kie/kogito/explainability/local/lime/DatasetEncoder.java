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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.model.EncodingParams;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.LinearModel;

/**
 * Encoder algorithm to transform perturbed inputs and outputs into a training set that the {@link LinearModel} can use.
 * The target inputs and output are needed in order to distinguish when the value of a certain feature corresponds or
 * is close to the one of the prediction to be explained.
 */
class DatasetEncoder {

    private final List<PredictionInput> perturbedInputs;
    private final List<Output> predictedOutputs;
    private final List<Feature> targetInputFeatures;
    private final Output originalOutput;
    private final EncodingParams encodingParams;

    DatasetEncoder(List<PredictionInput> perturbedInputs, List<Output> perturbedOutputs,
            List<Feature> targetInputFeatures, Output targetOutput,
            EncodingParams encodingParams) {
        this.perturbedInputs = perturbedInputs;
        this.predictedOutputs = perturbedOutputs;
        this.targetInputFeatures = targetInputFeatures;
        this.originalOutput = targetOutput;
        this.encodingParams = encodingParams;
    }

    /**
     * Get the input and output predictions transformed into a numerical training set.
     *
     * @return a numerical training set
     */
    List<Pair<double[], Double>> getEncodedTrainingSet() {
        List<Pair<double[], Double>> trainingSet = new LinkedList<>();
        List<List<double[]>> columnData;
        List<PredictionInput> flatInputs = DataUtils.linearizeInputs(perturbedInputs);
        if (!flatInputs.isEmpty() && !predictedOutputs.isEmpty() && !targetInputFeatures.isEmpty() && originalOutput != null) {
            columnData = getColumnData(flatInputs, encodingParams);

            int pi = 0;
            for (Output output : predictedOutputs) {
                List<Double> x = new LinkedList<>();
                for (List<double[]> column : columnData) {
                    double[] doubles = column.get(pi);
                    x.addAll(Arrays.asList(ArrayUtils.toObject(doubles)));
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
                double[] input = new double[x.size()];
                int i = 0;
                for (Double d : x) {
                    input[i] = d;
                    i++;
                }
                Pair<double[], Double> sample = new ImmutablePair<>(input, y);
                trainingSet.add(sample);

                pi++;
            }
        }
        return trainingSet;
    }

    private List<List<double[]>> getColumnData(List<PredictionInput> perturbedInputs, EncodingParams params) {
        List<List<double[]>> columnData = new LinkedList<>();

        for (int t = 0; t < targetInputFeatures.size(); t++) {
            Feature targetFeature = targetInputFeatures.get(t);
            int finalT = t;
            // encode all inputs with respect to the target, based on their type
            List<double[]> encode = targetFeature.getType().encode(params, targetFeature.getValue(), perturbedInputs
                    .stream().map(predictionInput -> predictionInput.getFeatures().get(finalT).getValue()).toArray(Value<?>[]::new));
            columnData.add(encode);
        }
        return columnData;
    }
}

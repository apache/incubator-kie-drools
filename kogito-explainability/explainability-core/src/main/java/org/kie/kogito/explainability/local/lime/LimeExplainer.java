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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.LinearModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of LIME algorithm (Ribeiro et al., 2016) that handles tabular data, text data, complex hierarchically
 * organized data, etc. seamlessly.
 * <p>
 * Differences with respect to the original (python) implementation:
 * - the linear (interpretable) model is based on a perceptron algorithm instead of Lasso + Ridge regression
 * - perturbing numerical features is done by sampling from a standard normal distribution centered around the value of the feature value associated with the prediction to be explained
 * - numerical features are max-min scaled and clustered via a gaussian kernel
 */
public class LimeExplainer implements LocalExplainer<Saliency> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimeExplainer.class);
    private static final double SEPARABLE_DATASET_RATIO = 0.99;

    /**
     * No. of samples to be generated for the local linear model training
     */
    private final int noOfSamples;

    /**
     * No. of perturbations to perform on a prediction
     */
    private final int noOfPerturbations;

    /**
     * No. of retries while trying to find a (linearly) separable dataset
     */
    private final int noOfRetries;

    public LimeExplainer(int noOfSamples, int noOfPerturbations, int noOfRetries) {
        this.noOfSamples = noOfSamples;
        this.noOfPerturbations = noOfPerturbations;
        this.noOfRetries = noOfRetries;
    }

    public LimeExplainer(int noOfSamples, int noOfPerturbations) {
        this.noOfSamples = noOfSamples;
        this.noOfPerturbations = noOfPerturbations;
        this.noOfRetries = 3;
    }

    @Override
    public Saliency explain(Prediction prediction, PredictionProvider model) {

        long start = System.currentTimeMillis();

        List<FeatureImportance> saliencies = new LinkedList<>();
        PredictionInput originalInput = prediction.getInput();
        List<Feature> inputFeatures = originalInput.getFeatures();

        if (inputFeatures.size() > 0) {
            // in case of composite / nested features, "linearize" the features
            List<PredictionInput> linearizedInputs = DataUtils.linearizeInputs(List.of(originalInput));
            if (linearizedInputs.size() > 0) {
                PredictionInput targetInput = linearizedInputs.get(0);
                List<Feature> linearizedTargetInputFeatures = targetInput.getFeatures();

                List<Output> actualOutputs = prediction.getOutput().getOutputs();
                int noOfInputFeatures = inputFeatures.size();
                int noOfOutputFeatures = linearizedTargetInputFeatures.size();
                double[] weights = new double[noOfOutputFeatures];

                // iterate through the different outputs in the prediction and explain each one separately
                for (int o = 0; o < actualOutputs.size(); o++) {
                    boolean separableDataset = false;

                    List<PredictionInput> trainingInputs = new LinkedList<>();
                    List<PredictionOutput> trainingOutputs = new LinkedList<>();

                    Output currentOutput = actualOutputs.get(o);
                    // do not explain the current output if it is 'null'
                    if (currentOutput.getValue() != null && currentOutput.getValue().getUnderlyingObject() != null) {
                        Map<Double, Long> rawClassesBalance = new HashMap<>();

                        /*
                        perturb the inputs so that the perturbed dataset contains more than just one output class, otherwise
                        it would be impossible to linearly separate it, and hence learn meaningful weights to be used as
                        feature importance scores.
                         */

                        boolean classification = false;

                        // in case of failure in separating the dataset, retry with newly perturbed inputs
                        for (int tries = this.noOfRetries; tries > 0; tries--) {
                            // perturb the inputs
                            List<PredictionInput> perturbedInputs = getPerturbedInputs(originalInput, noOfInputFeatures);

                            // perform predictions on the perturbed inputs
                            List<PredictionOutput> perturbedOutputs = model.predict(perturbedInputs);

                            // calculate the no. of samples belonging to each output class
                            Value<?> fv = currentOutput.getValue();
                            int finalO = o;
                            rawClassesBalance = perturbedOutputs.stream()
                                    .map(p -> p.getOutputs().get(finalO)) // get the (perturbed) output value corresponding to the one to be explained
                                    .map(output -> (Type.NUMBER.equals(output.getType())) ?
                                            output.getValue().asNumber() : // if numeric use it as it is
                                            (((output.getValue().getUnderlyingObject() == null // otherwise check if target and perturbed outputs are both null
                                                    && fv.getUnderlyingObject() == null)
                                                    || (output.getValue().getUnderlyingObject() != null  // if not null, check for underlying value equality
                                                    && output.getValue().asString().equals(fv.asString()))) ? 1d : 0d))
                                    .collect(Collectors.groupingBy(Double::doubleValue, Collectors.counting())); // then group-count distinct output values
                            LOGGER.debug("raw samples per class: {}", rawClassesBalance);

                            // check if the dataset is separable and also if the linear model should fit a regressor or a classifier
                            if (rawClassesBalance.size() > 1) {
                                Long max = rawClassesBalance.values().stream().max(Long::compareTo).orElse(1L);
                                if ((double) max / (double) perturbedInputs.size() < SEPARABLE_DATASET_RATIO) {
                                    separableDataset = true;
                                    classification = rawClassesBalance.size() == 2;

                                    // if dataset creation process succeeds use it to train the linear model
                                    trainingInputs.addAll(perturbedInputs);
                                    trainingOutputs.addAll(perturbedOutputs);
                                    break;
                                }
                            }
                        }
                        if (!separableDataset) { // fail the explanation if the dataset is not separable
                            throw new DatasetNotSeparableException(currentOutput, rawClassesBalance);
                        }

                        // only fetch the single output to explain in the generated prediction outputs
                        List<Output> predictedOutputs = new LinkedList<>();
                        for (PredictionOutput trainingOutput : trainingOutputs) {
                            Output output = trainingOutput.getOutputs().get(o);
                            predictedOutputs.add(output);
                        }

                        Output originalOutput = prediction.getOutput().getOutputs().get(o);

                        // encode the training data so that it can be fed into the linear model
                        DatasetEncoder datasetEncoder = new DatasetEncoder(trainingInputs, predictedOutputs, targetInput, originalOutput);
                        Collection<Pair<double[], Double>> trainingSet = datasetEncoder.getEncodedTrainingSet();

                        // weight the training samples based on the proximity to the target input to explain
                        double[] sampleWeights = SampleWeighter.getSampleWeights(targetInput, trainingSet);

                        // fit the linear model
                        LinearModel linearModel = new LinearModel(linearizedTargetInputFeatures.size(), classification);
                        double loss = linearModel.fit(trainingSet, sampleWeights);

                        if (!Double.isNaN(loss)) {
                            // update (and average) the weights of each feature using the corresponding linear model weight
                            weights = Arrays.stream(linearModel.getWeights()).map(x -> x / actualOutputs.size()).toArray();
                            LOGGER.debug("weights updated for output {}", currentOutput);
                        }
                    } else {
                        LOGGER.debug("skipping explanation of empty output {}", currentOutput);
                    }
                }
                // create the output saliency
                for (int i = 0; i < weights.length; i++) {
                    FeatureImportance featureImportance = new FeatureImportance(linearizedTargetInputFeatures.get(i), weights[i]);
                    saliencies.add(featureImportance);
                }
            } else {
                throw new LocalExplanationException("input features linearization failed");
            }
        } else {
            throw new LocalExplanationException("cannot explain a prediction whose input is empty");
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("explanation time: {}ms", (end - start));
        return new Saliency(saliencies);
    }

    private List<PredictionInput> getPerturbedInputs(PredictionInput predictionInput, int noOfFeatures) {
        List<PredictionInput> perturbedInputs = new LinkedList<>();
        // as per LIME paper, the dataset size should be at least |features|^2
        double perturbedDataSize = Math.max(noOfSamples, Math.pow(2, noOfFeatures));
        for (int i = 0; i < perturbedDataSize; i++) {
            perturbedInputs.add(DataUtils.perturbFeatures(predictionInput, noOfPerturbations));
        }
        return perturbedInputs;
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PerturbationContext;
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

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * An implementation of LIME algorithm (Ribeiro et al., 2016) that handles tabular data, text data, complex hierarchically
 * organized data, etc. seamlessly.
 * <p>
 * Differences with respect to the original (python) implementation:
 * - the linear (interpretable) model is based on a perceptron algorithm instead of Lasso + Ridge regression
 * - perturbing numerical features is done by sampling from a standard normal distribution centered around the value of the feature value associated with the prediction to be explained
 * - numerical features are max-min scaled and clustered via a gaussian kernel
 */
public class LimeExplainer implements LocalExplainer<Map<String, Saliency>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LimeExplainer.class);

    private final LimeConfig limeConfig;

    public LimeExplainer() {
        this(new LimeConfig());
    }

    public LimeExplainer(LimeConfig limeConfig) {
        this.limeConfig = limeConfig;
    }

    public LimeConfig getLimeConfig() {
        return limeConfig;
    }

    @Override
    public CompletableFuture<Map<String, Saliency>> explainAsync(Prediction prediction,
            PredictionProvider model,
            Consumer<Map<String, Saliency>> intermediateResultsConsumer) {
        PredictionInput originalInput = prediction.getInput();
        if (originalInput.getFeatures() == null || originalInput.getFeatures().isEmpty()) {
            throw new LocalExplanationException("cannot explain a prediction whose input is empty");
        }
        List<PredictionInput> linearizedInputs = DataUtils.linearizeInputs(List.of(originalInput));
        PredictionInput targetInput = linearizedInputs.get(0);
        List<Feature> linearizedTargetInputFeatures = targetInput.getFeatures();
        if (linearizedTargetInputFeatures.isEmpty()) {
            throw new LocalExplanationException("input features linearization failed");
        }
        List<Output> actualOutputs = prediction.getOutput().getOutputs();

        int noOfSamples = limeConfig.getNoOfSamples();

        if (noOfSamples <= 0) {
            noOfSamples = (int) Math.pow(2, linearizedTargetInputFeatures.size());
            LOGGER.debug("using 2^|features| samples ({})", noOfSamples);
        }

        return explainRetryCycle(
                model,
                originalInput,
                linearizedTargetInputFeatures,
                actualOutputs,
                limeConfig.getNoOfRetries(),
                noOfSamples,
                limeConfig.getPerturbationContext());
    }

    protected CompletableFuture<Map<String, Saliency>> explainRetryCycle(
            PredictionProvider model,
            PredictionInput originalInput,
            List<Feature> linearizedTargetInputFeatures,
            List<Output> actualOutputs,
            int noOfRetries,
            int noOfSamples,
            PerturbationContext perturbationContext) {

        List<PredictionInput> perturbedInputs = getPerturbedInputs(originalInput.getFeatures(), perturbationContext,
                noOfSamples, model);

        return model.predictAsync(perturbedInputs)
                .thenCompose(predictionOutputs -> {
                    try {
                        boolean strict = noOfRetries > 0;
                        List<LimeInputs> limeInputsList = getLimeInputs(linearizedTargetInputFeatures, actualOutputs, perturbedInputs, predictionOutputs, strict);
                        return completedFuture(getSaliencies(linearizedTargetInputFeatures, actualOutputs, limeInputsList));
                    } catch (DatasetNotSeparableException e) {
                        if (noOfRetries > 0) {
                            return explain(model, originalInput, linearizedTargetInputFeatures, actualOutputs, noOfRetries, noOfSamples, perturbationContext);
                        }
                        throw e;
                    }
                });
    }

    private CompletableFuture<Map<String, Saliency>> explain(PredictionProvider model, PredictionInput originalInput, List<Feature> linearizedTargetInputFeatures, List<Output> actualOutputs,
            int noOfRetries, int noOfSamples, PerturbationContext perturbationContext) {
        PerturbationContext newPerturbationContext;
        int newNoOfSamples;
        if (limeConfig.isAdaptDatasetVariance()) {
            newPerturbationContext = getNewPerturbationContext(linearizedTargetInputFeatures, noOfRetries, perturbationContext);
            newNoOfSamples = noOfSamples + noOfSamples / limeConfig.getNoOfRetries();
        } else {
            newPerturbationContext = perturbationContext;
            newNoOfSamples = noOfSamples;
        }
        return explainRetryCycle(model, originalInput, linearizedTargetInputFeatures,
                actualOutputs, noOfRetries - 1, newNoOfSamples,
                newPerturbationContext);
    }

    private PerturbationContext getNewPerturbationContext(List<Feature> linearizedTargetInputFeatures, int noOfRetries, PerturbationContext perturbationContext) {
        PerturbationContext newPerturbationContext;
        int nextPerturbationSize = Math.max(perturbationContext.getNoOfPerturbations() + 1,
                linearizedTargetInputFeatures.size() / noOfRetries);
        // make sure to stay within the max no. of features boundaries
        nextPerturbationSize = Math.min(linearizedTargetInputFeatures.size() - 1, nextPerturbationSize);
        Optional<Long> optionalSeed = perturbationContext.getSeed();
        if (optionalSeed.isPresent()) {
            Long seed = optionalSeed.get();
            newPerturbationContext = new PerturbationContext(seed, perturbationContext.getRandom(),
                    nextPerturbationSize);
        } else {
            newPerturbationContext = new PerturbationContext(perturbationContext.getRandom(),
                    nextPerturbationSize);
        }
        return newPerturbationContext;
    }

    /**
     * Obtain the inputs to the LIME algorithm, for each output in the original prediction.
     *
     * @param linearizedTargetInputFeatures the linarized features
     * @param actualOutputs the list of outputs to generate the explanations for
     * @param perturbedInputs the list of perturbed inputs
     * @param predictionOutputs the list of outputs associated to each perturbed input
     * @param strict whether accepting unique values for a given output in the {@code perturbedOutputs}
     * @return a list of inputs to the LIME algorithm
     */
    private List<LimeInputs> getLimeInputs(List<Feature> linearizedTargetInputFeatures,
            List<Output> actualOutputs,
            List<PredictionInput> perturbedInputs,
            List<PredictionOutput> predictionOutputs,
            boolean strict) {
        List<LimeInputs> limeInputsList = new ArrayList<>();
        for (int o = 0; o < actualOutputs.size(); o++) {
            Output currentOutput = actualOutputs.get(o);
            LimeInputs limeInputs = prepareInputs(perturbedInputs, predictionOutputs, linearizedTargetInputFeatures,
                    o, currentOutput, strict);
            limeInputsList.add(limeInputs);
        }
        return limeInputsList;
    }

    private Map<String, Saliency> getSaliencies(List<Feature> linearizedTargetInputFeatures, List<Output> actualOutputs, List<LimeInputs> limeInputsList) {
        Map<String, Saliency> result = new HashMap<>();
        for (int o = 0; o < actualOutputs.size(); o++) {
            LimeInputs limeInputs = limeInputsList.get(o);
            Output originalOutput = actualOutputs.get(o);

            getSaliency(linearizedTargetInputFeatures, result, limeInputs, originalOutput);
            LOGGER.debug("weights set for output {}", originalOutput);
        }
        return result;
    }

    private void getSaliency(List<Feature> linearizedTargetInputFeatures, Map<String, Saliency> result,
            LimeInputs limeInputs, Output originalOutput) {
        List<FeatureImportance> featureImportanceList = new ArrayList<>();

        // encode the training data so that it can be fed into the linear model
        DatasetEncoder datasetEncoder = new DatasetEncoder(limeInputs.getPerturbedInputs(),
                limeInputs.getPerturbedOutputs(),
                linearizedTargetInputFeatures, originalOutput,
                limeConfig.getEncodingParams());
        List<Pair<double[], Double>> trainingSet = datasetEncoder.getEncodedTrainingSet();

        // weight the training samples based on the proximity to the target input to explain
        double kernelWidth = limeConfig.getProximityKernelWidth() * Math.sqrt(linearizedTargetInputFeatures.size());
        double[] sampleWeights = SampleWeighter.getSampleWeights(linearizedTargetInputFeatures, trainingSet, kernelWidth);

        int ts = linearizedTargetInputFeatures.size();
        double[] featureWeights = new double[ts];
        Arrays.fill(featureWeights, 1);
        if (limeConfig.isPenalizeBalanceSparse()) {
            IndependentSparseFeatureBalanceFilter sparseFeatureBalanceFilter = new IndependentSparseFeatureBalanceFilter();
            sparseFeatureBalanceFilter.apply(featureWeights, linearizedTargetInputFeatures, trainingSet);
        }

        if (limeConfig.isProximityFilter()) {
            ProximityFilter proximityFilter = new ProximityFilter(limeConfig.getProximityThreshold(),
                    limeConfig.getProximityFilteredDatasetMinimum().doubleValue());
            proximityFilter.apply(trainingSet, sampleWeights);
        }

        LinearModel linearModel = new LinearModel(linearizedTargetInputFeatures.size(), limeInputs.isClassification());

        double loss = linearModel.fit(trainingSet, sampleWeights);
        if (!Double.isNaN(loss)) {
            // create the output saliency
            double[] weights = linearModel.getWeights();
            if (limeConfig.isNormalizeWeights() && weights.length > 0) {
                normalizeWeights(weights);
            }
            int i = 0;
            for (Feature linearizedFeature : linearizedTargetInputFeatures) {
                FeatureImportance featureImportance = new FeatureImportance(linearizedFeature, weights[i]
                        * featureWeights[i]);
                featureImportanceList.add(featureImportance);
                i++;
            }
        }
        Saliency saliency = new Saliency(originalOutput, featureImportanceList);
        result.put(originalOutput.getName(), saliency);
    }

    private void normalizeWeights(double[] weights) {
        double max = Arrays.stream(weights).max().orElse(1);
        double min = Arrays.stream(weights).min().orElse(0);
        if (max != min) {
            for (int k = 0; k < weights.length; k++) {
                weights[k] = (weights[k] - min) / (max - min);
            }
        }
    }

    /**
     * Check the perturbed inputs so that the dataset of perturbed input / outputs contains more than just one output
     * class, otherwise it would be impossible to linearly separate it, and hence learn meaningful weights to be used as
     * feature importance scores.
     * The check can be {@code strict} or not, if so it will throw a {@code DatasetNotSeparableException} when the dataset
     * for a given output is not separable.
     */
    private LimeInputs prepareInputs(List<PredictionInput> perturbedInputs,
            List<PredictionOutput> perturbedOutputs,
            List<Feature> linearizedTargetInputFeatures,
            int o,
            Output currentOutput, boolean strict) {

        if (currentOutput.getValue() != null && currentOutput.getValue().getUnderlyingObject() != null) {
            Map<Double, Long> rawClassesBalance;

            // calculate the no. of samples belonging to each output class
            Value fv = currentOutput.getValue();
            rawClassesBalance = getClassBalance(perturbedOutputs, fv, o);
            Long max = rawClassesBalance.values().stream().max(Long::compareTo).orElse(1L);
            double separationRatio = (double) max / (double) perturbedInputs.size();

            List<Output> outputs = perturbedOutputs.stream().map(po -> po.getOutputs().get(o)).collect(Collectors.toList());
            boolean classification = rawClassesBalance.size() == 2;
            if (strict) {
                // check if the dataset is separable and also if the linear model should fit a regressor or a classifier
                if (rawClassesBalance.size() > 1 && separationRatio < limeConfig.getSeparableDatasetRatio()) {
                    // if dataset creation process succeeds use it to train the linear model
                    return new LimeInputs(classification, linearizedTargetInputFeatures, currentOutput, perturbedInputs, outputs);
                } else {
                    throw new DatasetNotSeparableException(currentOutput, rawClassesBalance);
                }
            } else {
                LOGGER.warn("Using an hardly separable dataset for output '{}' of type '{}' with value '{}' ({})",
                        currentOutput.getName(), currentOutput.getType(), currentOutput.getValue(), rawClassesBalance);
                return new LimeInputs(classification, linearizedTargetInputFeatures, currentOutput, perturbedInputs, outputs);
            }

        } else {
            return new LimeInputs(false, linearizedTargetInputFeatures, currentOutput, emptyList(), emptyList());
        }
    }

    private Map<Double, Long> getClassBalance(List<PredictionOutput> perturbedOutputs, Value fv, int finalO) {
        Map<Double, Long> rawClassesBalance;
        rawClassesBalance = perturbedOutputs.stream()
                .map(p -> p.getOutputs().get(finalO)) // get the (perturbed) output value corresponding to the one to be explained
                .map(output -> toDouble(output, fv))
                .collect(Collectors.groupingBy(Double::doubleValue, Collectors.counting())); // then group-count distinct output values
        LOGGER.debug("raw samples per class: {}", rawClassesBalance);
        return rawClassesBalance;
    }

    private double toDouble(Output output, Value fv) {
        // if numeric use it as it is
        if (Type.NUMBER.equals(output.getType())) {
            return output.getValue().asNumber();
        }
        // otherwise check if target and perturbed outputs are both null
        boolean nullValues = output.getValue().getUnderlyingObject() == null
                && fv.getUnderlyingObject() == null;
        // if not null, check for underlying value equality
        boolean equalityCheck = output.getValue().getUnderlyingObject() != null
                && output.getValue().asString().equals(fv.asString());

        return nullValues || equalityCheck ? 1d : 0d;
    }

    private List<PredictionInput> getPerturbedInputs(List<Feature> features, PerturbationContext perturbationContext,
            int size, PredictionProvider predictionProvider) {
        List<PredictionInput> perturbedInputs = new ArrayList<>();

        DataDistribution dataDistribution = limeConfig.getDataDistribution();

        Map<String, FeatureDistribution> featureDistributionsMap;
        if (!dataDistribution.isEmpty()) {
            Map<String, HighScoreNumericFeatureZones> numericFeatureZonesMap;
            int max = limeConfig.getBoostrapInputs();
            if (limeConfig.isHighScoreFeatureZones()) {
                numericFeatureZonesMap = HighScoreNumericFeatureZonesProvider
                        .getHighScoreFeatureZones(dataDistribution, predictionProvider, features, max);
            } else {
                numericFeatureZonesMap = new HashMap<>();
            }

            // generate feature distributions, if possible
            featureDistributionsMap = DataUtils.boostrapFeatureDistributions(
                    dataDistribution, perturbationContext, 2 * size,
                    1, Math.min(size, max), numericFeatureZonesMap);
        } else {
            featureDistributionsMap = new HashMap<>();
        }

        for (int i = 0; i < size; i++) {
            List<Feature> newFeatures = DataUtils.perturbFeatures(features, perturbationContext, featureDistributionsMap);
            perturbedInputs.add(new PredictionInput(newFeatures));
        }
        return perturbedInputs;
    }
}

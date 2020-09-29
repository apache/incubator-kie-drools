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
package org.kie.kogito.explainability.global.pdp;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.global.GlobalExplainer;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.utils.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates the partial dependence plot for the features of a {@link PredictionProvider}.
 * This currently only works with models that work on {@code Feature}s of {@code Type.Number}.
 * While a strict PD implementation would need the whole training set used to train the model, this implementation seeks
 * to reproduce an approximate version of the training data by means of data distribution information (min, max, mean,
 * stdDev).
 * <p>
 * see also https://christophm.github.io/interpretable-ml-book/pdp.html
 */
public class PartialDependencePlotExplainer implements GlobalExplainer<List<PartialDependenceGraph>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartialDependencePlotExplainer.class);
    private static final int DEFAULT_SERIES_LENGTH = 100;

    private final int seriesLength;
    private final Random random;

    /**
     * Create a PDP provider.
     *
     * @param seriesLength the no. of data points sampled for each given feature.
     * @param random       random number generator
     */
    public PartialDependencePlotExplainer(int seriesLength, Random random) {
        this.seriesLength = seriesLength;
        this.random = random;
    }

    /**
     * Create a PDP provider.
     * <p>
     * Each feature is sampled {@code DEFAULT_SERIES_LENGTH} times.
     */
    public PartialDependencePlotExplainer() {
        this(DEFAULT_SERIES_LENGTH, new SecureRandom());
    }

    @Override
    public List<PartialDependenceGraph> explain(PredictionProvider model, PredictionProviderMetadata metadata) throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();

        List<PartialDependenceGraph> pdps = new ArrayList<>();
        DataDistribution dataDistribution = metadata.getDataDistribution();
        int noOfFeatures = metadata.getInputShape().getFeatures().size();

        List<FeatureDistribution> featureDistributions = dataDistribution.getFeatureDistributions();
        for (int featureIndex = 0; featureIndex < noOfFeatures; featureIndex++) {
            for (int outputIndex = 0; outputIndex < metadata.getOutputShape().getOutputs().size(); outputIndex++) {
                // generate samples for the feature under analysis
                double[] featureXSvalues = DataUtils.generateSamples(featureDistributions.get(featureIndex).getMin(),
                                                                     featureDistributions.get(featureIndex).getMax(), seriesLength);

                // generate data distributions for all features
                double[][] trainingData = generateDistributions(noOfFeatures, featureDistributions);

                double[] marginalImpacts = new double[featureXSvalues.length];
                for (int i = 0; i < featureXSvalues.length; i++) {
                    List<PredictionInput> predictionInputs = prepareInputs(noOfFeatures, featureIndex, featureXSvalues,
                                                                           trainingData, i);
                    List<PredictionOutput> predictionOutputs = getOutputs(model, predictionInputs);
                    // prediction requests are batched per value of feature 'Xs' under analysis
                    for (PredictionOutput predictionOutput : predictionOutputs) {
                        Output output = predictionOutput.getOutputs().get(outputIndex);
                        // use numerical output when possible, otherwise only use the score
                        double v = output.getValue().asNumber();
                        if (Double.isNaN(v)) { // check the output can be converted to a proper number
                            v = output.getScore();
                        }
                        marginalImpacts[i] += v / (double) seriesLength;
                    }
                }
                PartialDependenceGraph partialDependenceGraph = new PartialDependenceGraph(metadata.getInputShape().getFeatures().get(featureIndex),
                                                                                           featureXSvalues, marginalImpacts);
                pdps.add(partialDependenceGraph);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.debug("explanation time: {}ms", (end - start));
        return pdps;
    }

    /**
     * Perform batch predictions on the model.
     *
     * @param model            the model to be queried
     * @param predictionInputs a batch of inputs
     * @return a batch of outputs
     */
    private List<PredictionOutput> getOutputs(PredictionProvider model, List<PredictionInput> predictionInputs)
            throws InterruptedException, ExecutionException, TimeoutException {
        List<PredictionOutput> predictionOutputs;
        try {
            predictionOutputs = model.predictAsync(predictionInputs).get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Impossible to obtain prediction {}", e.getMessage());
            throw e;
        }
        return predictionOutputs;
    }

    /**
     * Generate inputs for a particular feature, using a specific discrete value from the data distribution of the
     * feature under analysis for that particular feature and values from a training data distribution (which we sample)
     * for all the other feature values.
     *
     * @param noOfFeatures    number of features in an input
     * @param featureIndex    the index of the feature under analysis
     * @param featureXSvalues discrete values of the feature under analysis
     * @param trainingData    training data for all the features
     * @param i               index of the specific discrete value of the feature under analysis to be evaluated
     * @return a list of prediction inputs
     */
    private List<PredictionInput> prepareInputs(int noOfFeatures, int featureIndex, double[] featureXSvalues,
                                                double[][] trainingData, int i) {
        List<PredictionInput> predictionInputs = new ArrayList<>(seriesLength);
        double[] inputs = new double[noOfFeatures];
        inputs[featureIndex] = featureXSvalues[i];
        for (int j = 0; j < seriesLength; j++) {
            for (int f = 0; f < noOfFeatures; f++) {
                if (f != featureIndex) {
                    inputs[f] = trainingData[f][j];
                }
            }
            PredictionInput input = new PredictionInput(DataUtils.doublesToFeatures(inputs));
            predictionInputs.add(input);
        }
        return predictionInputs;
    }

    /**
     * Sample training data distributions from each feature distribution.
     *
     * @param noOfFeatures         number of features
     * @param featureDistributions feature distributions
     * @return a matrix of numbers with {@code #noOfFeatures} rows and {@code #seriesLength} columns
     */
    private double[][] generateDistributions(int noOfFeatures, List<FeatureDistribution> featureDistributions) {
        double[][] trainingData = new double[noOfFeatures][seriesLength];
        for (int i = 0; i < noOfFeatures; i++) {
            double[] featureData = DataUtils.generateData(featureDistributions.get(i).getMean(),
                                                          featureDistributions.get(i).getStdDev(), seriesLength,
                                                          random);
            trainingData[i] = featureData;
        }
        return trainingData;
    }
}

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
import java.util.Collection;
import java.util.LinkedList;
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
 * Generates the partial dependence plot for a given feature.
 * While a strict PD implementation would need the whole training set used to train the model, this implementation seeks
 * to reproduce an approximate version of the training data by means of data distribution information (min, max, mean,
 * stdDev).
 * <p>
 * see also https://christophm.github.io/interpretable-ml-book/pdp.html
 */
public class PartialDependencePlotExplainer implements GlobalExplainer<Collection<PartialDependenceGraph>> {

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
     *
     * Each feature is sampled {@code DEFAULT_SERIES_LENGTH} times.
     */
    public PartialDependencePlotExplainer() {
        this(DEFAULT_SERIES_LENGTH, new SecureRandom());
    }

    @Override
    public Collection<PartialDependenceGraph> explain(PredictionProvider model, PredictionProviderMetadata metadata) throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();

        Collection<PartialDependenceGraph> pdps = new LinkedList<>();
        DataDistribution dataDistribution = metadata.getDataDistribution();
        int noOfFeatures = metadata.getInputShape().getFeatures().size();

        List<FeatureDistribution> featureDistributions = dataDistribution.getFeatureDistributions();
        for (int featureIndex = 0; featureIndex < noOfFeatures; featureIndex++) {
            for (int outputIndex = 0; outputIndex < metadata.getOutputShape().getOutputs().size(); outputIndex++) {
                double[] featureXSvalues = DataUtils.generateSamples(featureDistributions.get(featureIndex).getMin(),
                                                                     featureDistributions.get(featureIndex).getMax(), seriesLength);

                double[][] trainingData = new double[noOfFeatures][seriesLength];
                for (int i = 0; i < noOfFeatures; i++) {
                    double[] featureData = DataUtils.generateData(featureDistributions.get(i).getMean(),
                                                                  featureDistributions.get(i).getStdDev(), seriesLength,
                                                                  random);
                    trainingData[i] = featureData;
                }

                double[] marginalImpacts = new double[featureXSvalues.length];
                for (int i = 0; i < featureXSvalues.length; i++) {
                    List<PredictionInput> predictionInputs = new LinkedList<>();
                    double xs = featureXSvalues[i];
                    double[] inputs = new double[noOfFeatures];
                    inputs[featureIndex] = xs;
                    for (int j = 0; j < seriesLength; j++) {
                        for (int f = 0; f < noOfFeatures; f++) {
                            if (f != featureIndex) {
                                inputs[f] = trainingData[f][j];
                            }
                        }
                        PredictionInput input = new PredictionInput(DataUtils.doublesToFeatures(inputs));
                        predictionInputs.add(input);
                    }

                    List<PredictionOutput> predictionOutputs;
                    try {
                        predictionOutputs = model.predictAsync(predictionInputs).get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        LOGGER.error("Impossible to obtain prediction {}", e.getMessage());
                        throw e;
                    }
                    // prediction requests are batched per value of feature 'Xs' under analysis
                    for (PredictionOutput predictionOutput : predictionOutputs) {
                        Output output = predictionOutput.getOutputs().get(outputIndex);
                        marginalImpacts[i] += output.getScore() / (double) seriesLength;
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
}

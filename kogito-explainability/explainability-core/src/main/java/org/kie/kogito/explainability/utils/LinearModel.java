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
package org.kie.kogito.explainability.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A linear model implementation based on perceptron algorithm.
 */
public class LinearModel {

    private static final Logger logger = LoggerFactory.getLogger(LinearModel.class);
    private static final double GOOD_LOSS_THRESHOLD = 1e-2;
    private static final int MAX_NO_EPOCHS = 50;
    private static final double INITIAL_LEARNING_RATE = 1e-1;
    private static final double DECAY_RATE = 1e-5;

    private final double[] weights;
    private final boolean classification;
    private double bias;

    public LinearModel(int size, boolean classification, Random random) {
        this.bias = 0;
        this.classification = classification;
        this.weights = new double[size];
        for (int i = 0; i < size; i++) {
            this.weights[i] = random.nextGaussian();
        }
    }

    public double fit(Collection<Pair<double[], Double>> trainingSet) {
        double[] sampleWeights = new double[trainingSet.size()];
        Arrays.fill(sampleWeights, 1);
        return fit(trainingSet, sampleWeights);
    }

    public double fit(Collection<Pair<double[], Double>> trainingSet, double[] sampleWeights) {
        double finalLoss = Double.NaN;
        if (trainingSet.isEmpty()) {
            logger.warn("fitting an empty training set");
            Arrays.fill(weights, 0);
            return finalLoss;
        }
        double lr = INITIAL_LEARNING_RATE;
        int e = 0;
        while (checkFinalLoss(finalLoss) && e < MAX_NO_EPOCHS) {
            double loss = 0;
            int i = 0;
            for (Pair<double[], Double> sample : trainingSet) {
                double[] doubles = sample.getLeft();
                double predictedOutput = predict(doubles);
                double targetOutput = sample.getRight();
                double diff = finiteOrZero(targetOutput - predictedOutput);
                if (diff != 0) { // avoid null updates to save computation
                    loss += Math.abs(diff) / trainingSet.size();
                    for (int j = 0; j < weights.length; j++) {
                        double v = lr * diff * doubles[j];
                        if (trainingSet.size() == sampleWeights.length) {
                            v *= sampleWeights[i];
                        }
                        v = finiteOrZero(v);
                        weights[j] += v;
                        bias += lr * diff * sampleWeights[i];
                    }
                }
                i++;
            }
            lr *= (1d / (1d + DECAY_RATE * e)); // learning rate decay

            finalLoss = loss;
            e++;
            logger.debug("epoch {}, loss: {}", e, loss);
        }
        return finalLoss;
    }

    private boolean checkFinalLoss(double finalLoss) {
        return (Double.isNaN(finalLoss) || finalLoss > GOOD_LOSS_THRESHOLD);
    }

    private double finiteOrZero(double diff) {
        if (Double.isNaN(diff) || Double.isInfinite(diff)) {
            diff = 0;
        }
        return diff;
    }

    private double predict(double[] input) {
        double linearCombination = bias + IntStream.range(0, input.length).mapToDouble(i -> input[i] * weights[i]).sum();
        if (classification) {
            linearCombination = linearCombination >= 0 ? 1 : 0;
        }
        return linearCombination;
    }

    public double[] getWeights() {
        return weights;
    }
}

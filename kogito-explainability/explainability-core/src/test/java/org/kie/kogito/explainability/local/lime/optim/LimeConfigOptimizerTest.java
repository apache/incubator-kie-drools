/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimeConfigOptimizerTest {

    @Test
    void testImpactOptimization() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forImpactScore();
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoSampling() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forImpactScore().withSampling(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoWeighting() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forImpactScore().withWeighting(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoEncoding() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forImpactScore().withEncoding(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoProximity() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forImpactScore().withProximity(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoEntity() {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore()
                .withSampling(false)
                .withEncoding(false)
                .withWeighting(false)
                .withProximity(false);
        assertThrows(AssertionError.class, () -> assertConfigOptimized(limeConfigOptimizer));
    }

    @Test
    void testStabilityOptimization() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forStabilityScore();
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoSampling() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forStabilityScore().withSampling(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoWeighting() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forStabilityScore().withWeighting(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoEncoding() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forStabilityScore().withEncoding(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoProximity() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).forStabilityScore().withProximity(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoEntity() {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore()
                .withSampling(false)
                .withEncoding(false)
                .withWeighting(false)
                .withProximity(false);
        assertThrows(AssertionError.class, () -> assertConfigOptimized(limeConfigOptimizer));
    }

    @Test
    void testWeightedStabilityOptimization() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).withWeightedStability(0.5, 0.5);
        assertConfigOptimized(limeConfigOptimizer);

        limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).withWeightedStability(0.3, 0.7);
        assertConfigOptimized(limeConfigOptimizer);

        limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).withWeightedStability(0.7, 0.3);
        assertConfigOptimized(limeConfigOptimizer);

        limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).withWeightedStability(1, 0);
        assertConfigOptimized(limeConfigOptimizer);

        limeConfigOptimizer = new LimeConfigOptimizer().withTimeLimit(10).withWeightedStability(0, 1);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testWeightedStabilityWrongParamsOptimization() {
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.8, 0.7));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.1, 0.7));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.1, 1.1));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(2.1, 0.1));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(-0.1, 0.9));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.1, -0.9));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.1, 0.99));
        assertThrows(IllegalArgumentException.class, () -> new LimeConfigOptimizer().withWeightedStability(0.009, 0.99));
    }

    @Test
    void testSameConfig() throws ExecutionException, InterruptedException {
        long seed = 0;
        List<LimeConfig> optimizedConfigs = new ArrayList<>();

        PredictionProvider model = TestUtils.getSumSkipModel(1);
        DataDistribution dataDistribution = DataUtils.generateRandomDataDistribution(5, 100, new Random());
        List<PredictionInput> samples = dataDistribution.sample(3);
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);

        for (int i = 0; i < 2; i++) {
            Random random = new Random();
            LimeConfig initialConfig = new LimeConfig().withSamples(10).withPerturbationContext(new PerturbationContext(seed, random, 1));

            LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withDeterministicExecution(true)
                    .withStepCountLimit(10).withTimeLimit(10);
            LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);
            optimizedConfigs.add(optimizedConfig);
        }

        LimeConfig first = optimizedConfigs.get(0);
        LimeConfig second = optimizedConfigs.get(1);
        assertThat(first.getNoOfRetries()).isEqualTo(second.getNoOfRetries());
        assertThat(first.getNoOfSamples()).isEqualTo(second.getNoOfSamples());
        assertThat(first.getProximityFilteredDatasetMinimum()).isEqualTo(second.getProximityFilteredDatasetMinimum());
        assertThat(first.getProximityKernelWidth()).isEqualTo(second.getProximityKernelWidth());
        assertThat(first.getProximityThreshold()).isEqualTo(second.getProximityThreshold());
        assertThat(first.isProximityFilter()).isEqualTo(second.isProximityFilter());
        assertThat(first.isAdaptDatasetVariance()).isEqualTo(second.isAdaptDatasetVariance());
        assertThat(first.isPenalizeBalanceSparse()).isEqualTo(second.isPenalizeBalanceSparse());
        assertThat(first.getEncodingParams().getNumericTypeClusterGaussianFilterWidth()).isEqualTo(second.getEncodingParams().getNumericTypeClusterGaussianFilterWidth());
        assertThat(first.getEncodingParams().getNumericTypeClusterThreshold()).isEqualTo(second.getEncodingParams().getNumericTypeClusterThreshold());
        assertThat(first.getSeparableDatasetRatio()).isEqualTo(second.getSeparableDatasetRatio());
        assertThat(first.getPerturbationContext().getNoOfPerturbations()).isEqualTo(second.getPerturbationContext().getNoOfPerturbations());
    }

    private void assertConfigOptimized(LimeConfigOptimizer limeConfigOptimizer) throws InterruptedException, java.util.concurrent.ExecutionException {
        LimeConfig initialConfig = new LimeConfig().withSamples(10);
        PredictionProvider model = TestUtils.getSumSkipModel(1);
        Random random = new Random();
        random.setSeed(4);
        DataDistribution dataDistribution = DataUtils.generateRandomDataDistribution(5, 100, random);
        List<PredictionInput> samples = dataDistribution.sample(10);
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);
        assertThat(optimizedConfig).isNotNull();
        Assertions.assertThat(optimizedConfig).isNotSameAs(initialConfig);
    }
}
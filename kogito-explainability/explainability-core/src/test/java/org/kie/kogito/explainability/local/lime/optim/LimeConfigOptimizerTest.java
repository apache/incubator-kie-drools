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

import java.util.List;
import java.util.Random;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.DataDistribution;
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
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore();
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoSampling() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore().withSampling(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoWeighting() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore().withWeighting(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoEncoding() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore().withEncoding(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testImpactOptimizationNoProximity() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore().withProximity(false);
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
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore();
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoSampling() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore().withSampling(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoWeighting() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore().withWeighting(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoEncoding() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore().withEncoding(false);
        assertConfigOptimized(limeConfigOptimizer);
    }

    @Test
    void testStabilityOptimizationNoProximity() throws Exception {
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forStabilityScore().withProximity(false);
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

    private void assertConfigOptimized(LimeConfigOptimizer limeConfigOptimizer) throws InterruptedException, java.util.concurrent.ExecutionException {
        PredictionProvider model = TestUtils.getSumSkipModel(1);
        Random random = new Random();
        random.setSeed(4);
        DataDistribution dataDistribution = DataUtils.generateRandomDataDistribution(5, 100, random);
        List<PredictionInput> samples = dataDistribution.sample(10);
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfig initialConfig = new LimeConfig().withSamples(10);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);
        assertThat(optimizedConfig).isNotNull();
        Assertions.assertThat(optimizedConfig).isNotSameAs(initialConfig);
    }
}
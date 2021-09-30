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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.GenericFeatureDistribution;
import org.kie.kogito.explainability.model.IndependentFeaturesDataDistribution;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimeExplainerTest {

    private static final int DEFAULT_NO_OF_PERTURBATIONS = 1;

    @ParameterizedTest
    @ValueSource(longs = { 0, 1, 2, 3, 4 })
    void testEmptyPrediction(long seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        LimeConfig limeConfig = new LimeConfig()
                .withPerturbationContext(new PerturbationContext(seed, random, DEFAULT_NO_OF_PERTURBATIONS))
                .withSamples(10);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        PredictionInput input = new PredictionInput(Collections.emptyList());
        PredictionProvider model = TestUtils.getSumSkipModel(0);
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new SimplePrediction(input, output);

        assertThrows(LocalExplanationException.class, () -> limeExplainer.explainAsync(prediction, model));
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, 1, 2, 3, 4 })
    void testNonEmptyInput(long seed) throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        LimeConfig limeConfig = new LimeConfig()
                .withPerturbationContext(new PerturbationContext(seed, random, DEFAULT_NO_OF_PERTURBATIONS))
                .withSamples(10);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
        }
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getSumSkipModel(0);
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new SimplePrediction(input, output);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        assertNotNull(saliencyMap);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, 1, 2, 3, 4 })
    void testSparseBalance(long seed) throws InterruptedException, ExecutionException, TimeoutException {
        for (int nf = 1; nf < 4; nf++) {
            Random random = new Random();
            int noOfSamples = 100;
            LimeConfig limeConfigNoPenalty = new LimeConfig()
                    .withPerturbationContext(new PerturbationContext(seed, random, DEFAULT_NO_OF_PERTURBATIONS))
                    .withSamples(noOfSamples)
                    .withPenalizeBalanceSparse(false);
            LimeExplainer limeExplainerNoPenalty = new LimeExplainer(limeConfigNoPenalty);

            List<Feature> features = new ArrayList<>();
            for (int i = 0; i < nf; i++) {
                features.add(TestUtils.getMockedNumericFeature(i));
            }
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getSumSkipModel(0);
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new SimplePrediction(input, output);

            Map<String, Saliency> saliencyMapNoPenalty = limeExplainerNoPenalty.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            assertThat(saliencyMapNoPenalty).isNotNull();

            String decisionName = "sum-but0";
            Saliency saliencyNoPenalty = saliencyMapNoPenalty.get(decisionName);

            LimeConfig limeConfig = new LimeConfig()
                    .withSamples(noOfSamples)
                    .withPenalizeBalanceSparse(true);
            LimeExplainer limeExplainer = new LimeExplainer(limeConfig);

            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            assertThat(saliencyMap).isNotNull();

            Saliency saliency = saliencyMap.get(decisionName);

            for (int i = 0; i < features.size(); i++) {
                double score = saliency.getPerFeatureImportance().get(i).getScore();
                double scoreNoPenalty = saliencyNoPenalty.getPerFeatureImportance().get(i).getScore();
                assertThat(Math.abs(score)).isLessThanOrEqualTo(Math.abs(scoreNoPenalty));
            }
        }
    }

    @Test
    void testNormalizedWeights() throws InterruptedException, ExecutionException, TimeoutException {
        Random random = new Random();
        LimeConfig limeConfig = new LimeConfig()
                .withNormalizeWeights(true)
                .withPerturbationContext(new PerturbationContext(4L, random, 2))
                .withSamples(10);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        int nf = 4;
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < nf; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
        }
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getSumSkipModel(0);
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new SimplePrediction(input, output);

        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        assertThat(saliencyMap).isNotNull();

        String decisionName = "sum-but0";
        Saliency saliency = saliencyMap.get(decisionName);
        List<FeatureImportance> perFeatureImportance = saliency.getPerFeatureImportance();
        for (FeatureImportance featureImportance : perFeatureImportance) {
            assertThat(featureImportance.getScore()).isBetween(-1d, 1d);
        }
    }

    @Test
    void testWithDataDistribution() throws InterruptedException, ExecutionException, TimeoutException {
        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(4L, random, 1);
        List<FeatureDistribution> featureDistributions = new ArrayList<>();

        int nf = 4;
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < nf; i++) {
            Feature numericalFeature = FeatureFactory.newNumericalFeature("f-" + i, Double.NaN);
            features.add(numericalFeature);
            List<Value> values = new ArrayList<>();
            for (int r = 0; r < 4; r++) {
                values.add(Type.NUMBER.randomValue(perturbationContext));
            }
            featureDistributions.add(new GenericFeatureDistribution(numericalFeature, values));
        }

        DataDistribution dataDistribution = new IndependentFeaturesDataDistribution(featureDistributions);
        LimeConfig limeConfig = new LimeConfig()
                .withDataDistribution(dataDistribution)
                .withPerturbationContext(perturbationContext)
                .withSamples(10);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getSumThresholdModel(random.nextDouble(), random.nextDouble());
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new SimplePrediction(input, output);

        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        assertThat(saliencyMap).isNotNull();

        String decisionName = "inside";
        Saliency saliency = saliencyMap.get(decisionName);
        assertThat(saliency).isNotNull();
    }

    @Test
    void testZeroSampleSize() throws ExecutionException, InterruptedException, TimeoutException {
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(0);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            features.add(TestUtils.getMockedNumericFeature(i));
        }
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getSumSkipModel(0);
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new SimplePrediction(input, output);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        assertNotNull(saliencyMap);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, 1, 2, 3, 4 })
    void testDeterministic(long seed) throws ExecutionException, InterruptedException, TimeoutException {
        List<Saliency> saliencies = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            Random random = new Random();
            LimeConfig limeConfig = new LimeConfig()
                    .withPerturbationContext(new PerturbationContext(seed, random, DEFAULT_NO_OF_PERTURBATIONS))
                    .withSamples(10);
            LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
            List<Feature> features = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                features.add(TestUtils.getMockedNumericFeature(i));
            }
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getSumSkipModel(0);
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new SimplePrediction(input, output);
            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            saliencies.add(saliencyMap.get("sum-but0"));
        }
        assertThat(saliencies.get(0).getPerFeatureImportance().stream().map(FeatureImportance::getScore)
                .collect(Collectors.toList()))
                        .isEqualTo(saliencies.get(1).getPerFeatureImportance().stream().map(FeatureImportance::getScore)
                                .collect(Collectors.toList()));
    }
}

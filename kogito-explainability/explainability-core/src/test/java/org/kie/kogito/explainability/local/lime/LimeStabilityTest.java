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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.LocalSaliencyStability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimeStabilityTest {

    static final double TOP_FEATURE_THRESHOLD = 0.9;

    @ParameterizedTest
    @ValueSource(longs = { 0 })
    void testStabilityWithNumericData(long seed) throws Exception {
        Random random = new Random();
        PredictionProvider sumSkipModel = TestUtils.getSumSkipModel(0);
        List<Feature> featureList = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            featureList.add(TestUtils.getMockedNumericFeature(i));
        }
        LimeConfig limeConfig = new LimeConfig().withSamples(10).withPerturbationContext(new PerturbationContext(seed, random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        assertStable(limeExplainer, sumSkipModel, featureList);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0 })
    void testStabilityWithTextData(long seed) throws Exception {
        Random random = new Random();
        PredictionProvider sumSkipModel = TestUtils.getDummyTextClassifier();
        List<Feature> featureList = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            featureList.add(TestUtils.getMockedTextFeature("foo " + i));
        }
        featureList.add(TestUtils.getMockedTextFeature("money"));
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(new PerturbationContext(seed, random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        assertStable(limeExplainer, sumSkipModel, featureList);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0 })
    void testAdaptiveVariance(long seed) throws Exception {
        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(seed, random, 1);

        int samples = 1;
        int retries = 4;
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(samples)
                .withPerturbationContext(perturbationContext)
                .withRetries(retries)
                .withAdaptiveVariance(true);
        LimeExplainer adaptiveVarianceLE = new LimeExplainer(limeConfig);

        List<Feature> features = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            features.add(FeatureFactory.newNumericalFeature("f-" + i, 2));
        }
        PredictionProvider model = TestUtils.getEvenSumModel(0);
        assertStable(adaptiveVarianceLE, model, features);
    }

    private void assertStable(LimeExplainer limeExplainer, PredictionProvider model, List<Feature> featureList)
            throws Exception {
        PredictionInput input = new PredictionInput(featureList);
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (PredictionOutput predictionOutput : predictionOutputs) {
            Prediction prediction = new SimplePrediction(input, predictionOutput);
            List<Saliency> saliencies = new LinkedList<>();
            for (int i = 0; i < 100; i++) {
                Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                        .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
                saliencies.addAll(saliencyMap.values());
            }
            // check that the topmost important feature is stable
            List<String> names = new LinkedList<>();
            saliencies.stream().map(s -> s.getPositiveFeatures(1)).filter(f -> !f.isEmpty())
                    .forEach(f -> names.add(f.get(0).getFeature().getName()));
            Map<String, Long> frequencyMap =
                    names.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            boolean topFeature = false;
            for (Map.Entry<String, Long> entry : frequencyMap.entrySet()) {
                if (entry.getValue() >= TOP_FEATURE_THRESHOLD) {
                    topFeature = true;
                    break;
                }
            }
            assertTrue(topFeature);

            // check that the impact is stable
            List<Double> impacts = new ArrayList<>(saliencies.size());
            for (Saliency saliency : saliencies) {
                double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(2));
                impacts.add(v);
            }
            Map<Double, Long> impactMap =
                    impacts.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            boolean topImpact = false;
            for (Map.Entry<Double, Long> entry : impactMap.entrySet()) {
                if (entry.getValue() >= TOP_FEATURE_THRESHOLD) {
                    topImpact = true;
                    break;
                }
            }
            assertTrue(topImpact);
        }
    }

    @ParameterizedTest
    @ValueSource(longs = { 0, 1, 2, 3, 4 })
    void testStabilityDeterministic(long seed) throws Exception {
        List<LocalSaliencyStability> stabilities = new ArrayList<>();
        for (int j = 0; j < 2; j++) {
            Random random = new Random();
            PredictionProvider model = TestUtils.getSumSkipModel(0);
            List<Feature> featureList = new LinkedList<>();
            for (int i = 0; i < 5; i++) {
                featureList.add(TestUtils.getMockedNumericFeature(i));
            }
            PredictionInput input = new PredictionInput(featureList);
            List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());

            Prediction prediction = new SimplePrediction(input, predictionOutputs.get(0));

            LimeConfig limeConfig = new LimeConfig().withSamples(10).withPerturbationContext(new PerturbationContext(seed, random, 1));
            LimeExplainer explainer = new LimeExplainer(limeConfig);
            LocalSaliencyStability stability = ExplainabilityMetrics.getLocalSaliencyStability(model, prediction, explainer,
                    2, 10);
            stabilities.add(stability);
        }
        LocalSaliencyStability first = stabilities.get(0);
        LocalSaliencyStability second = stabilities.get(1);
        String decisionName = "sum-but0";
        assertThat(first.getNegativeStabilityScore(decisionName, 1)).isEqualTo(second.getNegativeStabilityScore(decisionName, 1));
        assertThat(first.getPositiveStabilityScore(decisionName, 1)).isEqualTo(second.getPositiveStabilityScore(decisionName, 1));
        assertThat(first.getNegativeStabilityScore(decisionName, 2)).isEqualTo(second.getNegativeStabilityScore(decisionName, 2));
        assertThat(first.getPositiveStabilityScore(decisionName, 2)).isEqualTo(second.getPositiveStabilityScore(decisionName, 2));
    }
}

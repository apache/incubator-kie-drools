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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DummyModelsLimeExplainerTest {

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testMapOneFeatureToOutputRegression(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        int idx = 1;
        List<Feature> features = new LinkedList<>();
        features.add(TestUtils.getMockedNumericFeature(100));
        features.add(TestUtils.getMockedNumericFeature(20));
        features.add(TestUtils.getMockedNumericFeature(0.1));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getFeaturePassModel(idx);
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));

        LimeConfig limeConfig = new LimeConfig().withSamples(100).withPerturbationContext(new PerturbationContext(random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        int topK = 1;
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.5;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);
        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "feature-" + idx;
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isZero();
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isZero();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testUnusedFeatureRegression(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        int idx = 2;
        List<Feature> features = new LinkedList<>();
        features.add(TestUtils.getMockedNumericFeature(100));
        features.add(TestUtils.getMockedNumericFeature(20));
        features.add(TestUtils.getMockedNumericFeature(10));
        PredictionProvider model = TestUtils.getSumSkipModel(idx);
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));
        LimeConfig limeConfig = new LimeConfig().withSamples(10)
                .withPerturbationContext(new PerturbationContext(random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        int topK = 1;
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.5;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);
        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "sum-but" + idx;
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isEqualTo(1);
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testMapOneFeatureToOutputClassification(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        int idx = 1;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 1));
        features.add(FeatureFactory.newNumericalFeature("f2", 1));
        features.add(FeatureFactory.newNumericalFeature("f3", 3));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getEvenFeatureModel(idx);
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));

        LimeConfig limeConfig = new LimeConfig().withSamples(100)
                .withPerturbationContext(new PerturbationContext(random, 2));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.5;
        int topK = 1;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);

        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "feature-" + idx;
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isEqualTo(1);
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testTextSpamClassification(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        List<Feature> features = new LinkedList<>();
        Function<String, List<String>> tokenizer = s -> Arrays.asList(s.split(" ").clone());
        features.add(FeatureFactory.newFulltextFeature("f1", "we go here and there", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("f2", "please give me some money", tokenizer));
        features.add(FeatureFactory.newFulltextFeature("f3", "dear friend, please reply", tokenizer));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));

        LimeConfig limeConfig = new LimeConfig()
                .withSamples(100).withPerturbationContext(new PerturbationContext(random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model).toCompletableFuture()
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(1);
            assertEquals(1, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        int topK = 1;
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.2;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);

        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "spam";
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isEqualTo(1);
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testUnusedFeatureClassification(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        int idx = 2;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 6));
        features.add(FeatureFactory.newNumericalFeature("f2", 3));
        features.add(FeatureFactory.newNumericalFeature("f3", 5));
        PredictionProvider model = TestUtils.getEvenSumModel(idx);
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(100).withPerturbationContext(new PerturbationContext(random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            assertEquals(1d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        int topK = 1;
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.5;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);

        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "sum-even-but" + idx;
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isEqualTo(1);
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 })
    void testFixedOutput(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 6));
        features.add(FeatureFactory.newNumericalFeature("f2", 3));
        features.add(FeatureFactory.newNumericalFeature("f3", 5));
        PredictionProvider model = TestUtils.getFixedOutputClassifier();
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> outputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(input, outputs.get(0));
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10).withPerturbationContext(new PerturbationContext(random, 1));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
            assertEquals(3, topFeatures.size());
            for (FeatureImportance featureImportance : topFeatures) {
                assertEquals(0, featureImportance.getScore());
            }
            assertEquals(0d, ExplainabilityMetrics.impactScore(model, prediction, topFeatures));
        }
        int topK = 1;
        double minimumPositiveStabilityRate = 0.5;
        double minimumNegativeStabilityRate = 0.5;
        TestUtils.assertLimeStability(model, prediction, limeExplainer, topK, minimumPositiveStabilityRate,
                minimumNegativeStabilityRate);

        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<Feature> fs = new LinkedList<>();
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            fs.add(TestUtils.getMockedNumericFeature());
            inputs.add(new PredictionInput(fs));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 10;
        String decision = "class";
        double precision =
                ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isEqualTo(1);
        double recall =
                ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isEqualTo(1);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isEqualTo(1);
    }
}

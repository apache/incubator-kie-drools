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
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.local.lime.optim.LimeConfigOptimizer;
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
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.ValidationUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FraudScoringDmnLimeExplainerTest {

    @Test
    void testFraudScoringDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        PredictionInput predictionInput = getTestInput();
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(predictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(predictionInput, predictionOutputs.get(0));

        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(0L, random, 1);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<FeatureImportance> topFeatures = saliency.getTopFeatures(4);
            double topScore = Math.abs(topFeatures.stream().map(FeatureImportance::getScore).findFirst().orElse(0d));
            if (!topFeatures.isEmpty() && topScore > 0) {
                double v = ExplainabilityMetrics.impactScore(model, prediction, topFeatures);
                assertThat(v).isPositive(); // checks the drop of important features triggers a flipped prediction (or a significant drop in the output score).
            }
        }
        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.4, 0.4)); // set to 0.4 since "Last Transaction" is inherently unstable output

        String decision = "Risk Score";
        List<PredictionInput> inputs = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            inputs.add(new PredictionInput(DataUtils.perturbFeatures(predictionInput.getFeatures(), perturbationContext)));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 2;
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        AssertionsForClassTypes.assertThat(f1).isBetween(0.5d, 1d);
    }

    private PredictionProvider getModel() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/fraud.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());
        final String FRAUD_NS = "http://www.redhat.com/dmn/definitions/_81556584-7d78-4f8c-9d5f-b3cddb9b5c73";
        final String FRAUD_NAME = "fraud-scoring";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, FRAUD_NS, FRAUD_NAME);
        return new DecisionModelWrapper(decisionModel, List.of("Last Transaction", "Merchant Blacklist"));
    }

    private PredictionInput getTestInput() {
        List<Map<String, Object>> transactions = new LinkedList<>();
        Map<String, Object> t1 = new HashMap<>();
        t1.put("Card Type", "Prepaid");
        t1.put("Location", "Global");
        t1.put("Amount", 141);
        t1.put("Auth Code", "Denied");
        transactions.add(t1);
        Map<String, Object> t2 = new HashMap<>();
        t2.put("Card Type", "Debit");
        t2.put("Location", "Local");
        t2.put("Amount", 19);
        t2.put("Auth Code", "Approved");
        transactions.add(t2);
        Map<String, Object> map = new HashMap<>();
        map.put("Transactions", transactions);

        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", map));
        return new PredictionInput(features);
    }

    @Test
    void testExplanationStabilityWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomFraudScoringInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        long seed = 0;
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().withDeterministicExecution(true)
                .withStepCountLimit(20);
        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(seed, random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);

        assertThat(optimizedConfig).isNotSameAs(initialConfig);
        LimeExplainer limeExplainer = new LimeExplainer(optimizedConfig);
        PredictionInput testPredictionInput = getTestInput();
        List<PredictionOutput> testPredictionOutputs = model.predictAsync(List.of(testPredictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction instance = new SimplePrediction(testPredictionInput, testPredictionOutputs.get(0));

        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, instance, limeExplainer, 1,
                0.5, 0.5));
    }

    @Test
    void testExplanationWithDataDistribution() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomFraudScoringInputs();
        List<PredictionInput> inputs = samples.subList(0, 10);

        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withDataDistribution(new PredictionInputsDataDistribution(inputs))
                .withPerturbationContext(perturbationContext);

        LimeExplainer limeExplainer = new LimeExplainer(initialConfig);
        PredictionInput testPredictionInput = getTestInput();
        List<PredictionOutput> testPredictionOutputs = model.predictAsync(List.of(testPredictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction instance = new SimplePrediction(testPredictionInput, testPredictionOutputs.get(0));

        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, instance, limeExplainer, 1,
                0.5, 0.5));
    }

    @Test
    void testExplanationImpactScoreWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomFraudScoringInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 10)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        long seed = 0;
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer()
                .withDeterministicExecution(true).forImpactScore().withSampling(false).withStepCountLimit(20);

        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(seed, random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);

        assertThat(optimizedConfig).isNotSameAs(initialConfig);

    }

    @Test
    void testExplanationWeightedStabilityWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomFraudScoringInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        long seed = 0;
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer()
                .withDeterministicExecution(true).withWeightedStability(0.4, 0.6).withStepCountLimit(10);
        Random random = new Random();
        PerturbationContext perturbationContext = new PerturbationContext(seed, random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);

        assertThat(optimizedConfig).isNotSameAs(initialConfig);
        LimeExplainer limeExplainer = new LimeExplainer(optimizedConfig);
        PredictionInput testPredictionInput = getTestInput();
        List<PredictionOutput> testPredictionOutputs = model.predictAsync(List.of(testPredictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction instance = new SimplePrediction(testPredictionInput, testPredictionOutputs.get(0));

        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, instance, limeExplainer, 1,
                0.6, 0.4));
    }
}

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
import java.util.stream.Collectors;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrafficViolationDmnLimeExplainerTest {

    @Test
    void testTrafficViolationDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        PredictionInput predictionInput = getTestInput();
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(predictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(predictionInput, predictionOutputs.get(0));
        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 1);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            List<String> strings = saliency.getTopFeatures(3).stream().map(f -> f.getFeature().getName()).collect(Collectors.toList());
            assertTrue(strings.contains("Actual Speed") || strings.contains("Speed Limit"));
        }
        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.3, 0.3));

        String decision = "Fine";
        List<PredictionInput> inputs = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            inputs.add(new PredictionInput(DataUtils.perturbFeatures(predictionInput.getFeatures(), perturbationContext)));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 5;
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        AssertionsForClassTypes.assertThat(f1).isBetween(0.5d, 1d);
    }

    @Test
    void testExplanationStabilityWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomTrafficViolationInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer()
                .withSampling(false);
        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 1);
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
    void testExplanationImpactScoreWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomTrafficViolationInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 10)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore().withSampling(false);

        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);
        assertThat(optimizedConfig).isNotSameAs(initialConfig);
    }

    @Test
    void testExplanationWeightedStabilityWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = DmnTestUtils.randomTrafficViolationInputs();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer()
                .withSampling(false)
                .withWeightedStability(0.4, 0.6);

        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 2);
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
                0.4, 0.6));
    }

    private PredictionProvider getModel() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/TrafficViolation.dmn")));
        assertEquals(1, dmnRuntime.getModels().size());

        final String TRAFFIC_VIOLATION_NS = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        final String TRAFFIC_VIOLATION_NAME = "Traffic Violation";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, TRAFFIC_VIOLATION_NS, TRAFFIC_VIOLATION_NAME);
        return new DecisionModelWrapper(decisionModel);
    }

    private PredictionInput getTestInput() {
        final Map<String, Object> driver = new HashMap<>();
        driver.put("Points", 10);
        final Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 150);
        violation.put("Speed Limit", 130);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Driver", driver);
        contextVariables.put("Violation", violation);

        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCompositeFeature("context", contextVariables));
        return new PredictionInput(features);
    }
}

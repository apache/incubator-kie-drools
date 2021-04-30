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
package org.kie.kogito.explainability.explainability.integrationtests.dmn;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class DummyDmnModelsLimeExplainerTest {

    @Test
    void testFunctional1DMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/functionalTest1.dmn")));
        assertThat(dmnRuntime.getModels().size()).isEqualTo(1);

        final String namespace = "https://kiegroup.org/dmn/_049CD980-1310-4B02-9E90-EFC57059F44A";
        final String name = "functionalTest1";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, namespace, name);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);
        Map<String, Object> context = new HashMap<>();
        context.put("booleanInput", true);
        context.put("notUsedInput", 1);

        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", context));
        PredictionInput predictionInput = new PredictionInput(features);
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
            assertThat(saliency).isNotNull();
            List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(2);
            assertThat(topFeatures.isEmpty()).isFalse();
            assertThat(topFeatures.get(0).getFeature().getName()).isEqualTo("booleanInput");
        }
        assertThatCode(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.5, 0.5)).doesNotThrowAnyException();
        String decision = "decision";
        List<PredictionInput> inputs = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            inputs.add(new PredictionInput(DataUtils.perturbFeatures(features, perturbationContext)));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 5;
        double precision = ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isBetween(0d, 1d);
        double recall = ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isBetween(0d, 1d);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isBetween(0d, 1d);
    }

    @Test
    void testFunctional2DMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/functionalTest2.dmn")));
        assertThat(dmnRuntime.getModels().size()).isEqualTo(1);

        final String namespace = "https://kiegroup.org/dmn/_049CD980-1310-4B02-9E90-EFC57059F44A";
        final String name = "new-file";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, namespace, name);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);
        Map<String, Object> context = new HashMap<>();
        context.put("numberInput", 1);
        context.put("notUsedInput", 1);

        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", context));
        PredictionInput predictionInput = new PredictionInput(features);
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
            assertThat(saliency).isNotNull();
            List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(2);
            assertThat(topFeatures.isEmpty()).isFalse();
            assertThat(topFeatures.get(0).getFeature().getName()).isEqualTo("numberInput");
        }
        assertThatCode(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.5, 0.5)).doesNotThrowAnyException();

        String decision = "decision";
        List<PredictionInput> inputs = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            inputs.add(new PredictionInput(DataUtils.perturbFeatures(features, perturbationContext)));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 5;
        double precision = ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isBetween(0d, 1d);
        double recall = ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isBetween(0d, 1d);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isBetween(0d, 1d);
    }

    @Test
    void testAllTypesDMNExplanation() throws ExecutionException, InterruptedException, TimeoutException {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(getClass().getResourceAsStream("/dmn/allTypes.dmn")));
        assertThat(dmnRuntime.getModels().size()).isEqualTo(1);

        final String namespace = "https://kiegroup.org/dmn/_24B9EC8C-2F02-40EB-B6BB-E8CDE82FBF08";
        final String name = "new-file";
        DecisionModel decisionModel = new DmnDecisionModel(dmnRuntime, namespace, name);

        PredictionProvider model = new DecisionModelWrapper(decisionModel);

        Map<String, Object> context = new HashMap<>();
        context.put("stringInput", "test");
        context.put("listOfStringInput", Collections.singletonList("test"));
        context.put("numberInput", 1);
        context.put("listOfNumbersInput", Collections.singletonList(1));
        context.put("booleanInput", true);
        context.put("listOfBooleansInput", Collections.singletonList(true));

        context.put("timeInput", "h09:00");
        context.put("dateInput", "2020-04-02");
        context.put("dateAndTimeInput", "2020-04-02T09:00:00");
        context.put("daysAndTimeDurationInput", "P1DT1H");
        context.put("yearsAndMonthDurationInput", "P1Y1M");

        Map<String, Object> complexInput = new HashMap<>();
        complexInput.put("aNestedListOfNumbers", Collections.singletonList(1));
        complexInput.put("aNestedString", "test");
        complexInput.put("aNestedComplexInput", Collections.singletonMap("doubleNestedNumber", 1));

        context.put("complexInput", complexInput);
        context.put("listOfComplexInput", Collections.singletonList(complexInput));

        List<Feature> features = new ArrayList<>();
        features.add(FeatureFactory.newCompositeFeature("context", context));
        PredictionInput predictionInput = new PredictionInput(features);
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(predictionInput))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        Prediction prediction = new SimplePrediction(predictionInput, predictionOutputs.get(0));
        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 3);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertThat(saliency).isNotNull();
        }
        assertThatCode(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.5, 0.2)).doesNotThrowAnyException();

        String decision = "myDecision";
        List<PredictionInput> inputs = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            inputs.add(new PredictionInput(DataUtils.perturbFeatures(features, perturbationContext)));
        }
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 5;
        double precision = ExplainabilityMetrics.getLocalSaliencyPrecision(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(precision).isBetween(0d, 1d);
        double recall = ExplainabilityMetrics.getLocalSaliencyRecall(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(recall).isBetween(0d, 1d);
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isBetween(0d, 1d);
    }
}

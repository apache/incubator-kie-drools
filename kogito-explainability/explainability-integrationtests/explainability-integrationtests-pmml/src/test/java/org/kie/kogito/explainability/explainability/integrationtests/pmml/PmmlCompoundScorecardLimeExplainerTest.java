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
package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.local.lime.optim.LimeConfigOptimizer;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionInputsDataDistribution;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.ValidationUtils;
import org.kie.pmml.api.runtime.PMMLRuntime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuntimeFactoryInternal.getPMMLRuntime;

class PmmlCompoundScorecardLimeExplainerTest {

    private static PMMLRuntime compoundScoreCardRuntime;
    private static final String[] CATEGORY_TWO = new String[] { "classA", "classB", "classC", "NA" };

    @BeforeAll
    static void setUpBefore() throws URISyntaxException {
        compoundScoreCardRuntime = getPMMLRuntime(ResourceReaderUtils.getResourceAsFile("compoundnestedpredicatescorecard/CompoundNestedPredicateScorecard.pmml"));
    }

    @Test
    void testPMMLCompoundScorecard() throws Exception {
        Random random = new Random();
        random.setSeed(0);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(new PerturbationContext(random, 1));

        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
        PredictionInput input = getTestInput();

        PredictionProvider model = getModel();
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        assertThat(predictionOutputs).isNotNull();
        assertThat(predictionOutputs).isNotEmpty();
        PredictionOutput output = predictionOutputs.get(0);
        assertThat(output).isNotNull();
        Prediction prediction = new SimplePrediction(input, output);

        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertThat(saliency).isNotNull();
            double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(2));
            assertThat(v).isEqualTo(1d);
        }
        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 1,
                0.5, 0.5));

        List<PredictionInput> inputs = getSamples();
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        String decision = "score";
        int k = 1;
        int chunkSize = 2;
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        AssertionsForClassTypes.assertThat(f1).isBetween(0d, 1d);
    }

    @Test
    void testExplanationStabilityWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = getSamples();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer();
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
                0.6, 0.6));
    }

    @Test
    void testExplanationImpactScoreWithOptimization() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = getModel();

        List<PredictionInput> samples = getSamples();
        List<PredictionOutput> predictionOutputs = model.predictAsync(samples.subList(0, 5)).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        LimeConfigOptimizer limeConfigOptimizer = new LimeConfigOptimizer().forImpactScore();
        Random random = new Random();
        random.setSeed(0);
        PerturbationContext perturbationContext = new PerturbationContext(random, 1);
        LimeConfig initialConfig = new LimeConfig()
                .withSamples(10)
                .withPerturbationContext(perturbationContext);
        LimeConfig optimizedConfig = limeConfigOptimizer.optimize(initialConfig, predictions, model);
        assertThat(optimizedConfig).isNotSameAs(initialConfig);
    }

    private List<PredictionInput> getSamples() {
        List<PredictionInput> inputs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<Feature> fs = new ArrayList<>();
            fs.add(FeatureFactory.newNumericalFeature("input1", i + 1));
            fs.add(FeatureFactory.newCategoricalFeature("input2", CATEGORY_TWO[i % CATEGORY_TWO.length]));
            inputs.add(new PredictionInput(fs));
        }
        return inputs;
    }

    private PredictionInput getTestInput() {
        List<Feature> features = new ArrayList<>(2);
        features.add(FeatureFactory.newNumericalFeature("input1", -50));
        features.add(FeatureFactory.newCategoricalFeature("input2", CATEGORY_TWO[1]));
        return new PredictionInput(features);
    }

    private PredictionProvider getModel() {
        return inputs -> CompletableFuture.supplyAsync(() -> {
            List<PredictionOutput> outputs = new ArrayList<>(inputs.size());
            for (PredictionInput predictionInput : inputs) {
                List<Feature> inputFeatures = predictionInput.getFeatures();
                CompoundNestedPredicateScorecardExecutor pmmlModel = new CompoundNestedPredicateScorecardExecutor(
                        inputFeatures.get(0).getValue().asNumber(), inputFeatures.get(1).getValue().asString());
                PMML4Result result = pmmlModel.execute(compoundScoreCardRuntime);
                Map<String, Object> resultVariables = result.getResultVariables();
                String score = "" + resultVariables.get(CompoundNestedPredicateScorecardExecutor.TARGET_FIELD);
                String reason1 = "" + resultVariables.get(CompoundNestedPredicateScorecardExecutor.REASON_CODE1_FIELD);
                PredictionOutput predictionOutput = new PredictionOutput(List.of(
                        new Output("score", Type.TEXT, new Value(score), 1d),
                        new Output("reason1", Type.TEXT, new Value(reason1), 1d)));
                outputs.add(predictionOutput);
            }
            return outputs;
        });
    }
}

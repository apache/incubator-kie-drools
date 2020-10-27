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
package org.kie.kogito.explainability.explainability.integrationtests.pmml;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.pmml.api.runtime.PMMLRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.explainability.explainability.integrationtests.pmml.AbstractPMMLTest.getPMMLRuntime;
import static org.kie.test.util.filesystem.FileUtils.getFile;

class PmmlLimeExplainerTest {

    private static PMMLRuntime logisticRegressionIrisRuntime;
    private static PMMLRuntime categoricalVariableRegressionRuntime;
    private static PMMLRuntime scorecardCategoricalRuntime;
    private static PMMLRuntime compoundScoreCardRuntime;

    @BeforeAll
    static void setUpBefore() {
        logisticRegressionIrisRuntime = getPMMLRuntime(getFile("logisticRegressionIrisData.pmml"));
        categoricalVariableRegressionRuntime = getPMMLRuntime(getFile("categoricalVariablesRegression.pmml"));
        scorecardCategoricalRuntime = getPMMLRuntime(getFile("SimpleScorecardCategorical.pmml"));
        compoundScoreCardRuntime = getPMMLRuntime(getFile("CompoundNestedPredicateScorecard.pmml"));
    }

    @Test
    void testPMMLRegression() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(100, 1, random);
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("sepalLength", 6.9));
            features.add(FeatureFactory.newNumericalFeature("sepalWidth", 3.1));
            features.add(FeatureFactory.newNumericalFeature("petalLength", 5.1));
            features.add(FeatureFactory.newNumericalFeature("petalWidth", 2.3));
            PredictionInput input = new PredictionInput(features);

            PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
                List<PredictionOutput> outputs = new LinkedList<>();
                for (PredictionInput input1 : inputs) {
                    List<Feature> features1 = input1.getFeatures();
                    LogisticRegressionIrisDataExecutor pmmlModel = new LogisticRegressionIrisDataExecutor(
                            features1.get(0).getValue().asNumber(), features1.get(1).getValue().asNumber(),
                            features1.get(2).getValue().asNumber(), features1.get(3).getValue().asNumber());
                    PMML4Result result = pmmlModel.execute(logisticRegressionIrisRuntime);
                    String species = result.getResultVariables().get("Species").toString();
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("species", Type.TEXT, new Value<>(species), 1d)));
                    outputs.add(predictionOutput);
                }
                return outputs;
            });
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new Prediction(input, output);
            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            for (Saliency saliency : saliencyMap.values()) {
                assertNotNull(saliency);
                double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getPositiveFeatures(2));
                assertEquals(1d, v);
            }
        }
    }

    @Test
    void testPMMLRegressionCategorical() throws Exception {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCategoricalFeature("mapX", "red"));
        features.add(FeatureFactory.newCategoricalFeature("mapY", "classB"));
        PredictionInput input = new PredictionInput(features);

        LimeExplainer limeExplainer = new LimeExplainer(10, 1);
        PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
            List<PredictionOutput> outputs = new LinkedList<>();
            for (PredictionInput input1 : inputs) {
                List<Feature> features1 = input1.getFeatures();
                CategoricalVariablesRegressionExecutor pmmlModel = new CategoricalVariablesRegressionExecutor(
                        features1.get(0).getValue().asString(), features1.get(1).getValue().asString());
                PMML4Result result = pmmlModel.execute(categoricalVariableRegressionRuntime);
                String score = result.getResultVariables().get("result").toString();
                PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("result", Type.NUMBER, new Value<>(score), 1d)));
                outputs.add(predictionOutput);
            }
            return outputs;
        });
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new Prediction(input, output);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(1));
            assertEquals(1d, v);
        }
    }

    @Test
    void testPMMLScorecardCategorical() throws Exception {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newCategoricalFeature("input1", "classA"));
        features.add(FeatureFactory.newCategoricalFeature("input2", "classB"));
        PredictionInput input = new PredictionInput(features);

        LimeExplainer limeExplainer = new LimeExplainer(10, 1);
        PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
            List<PredictionOutput> outputs = new LinkedList<>();
            for (PredictionInput input1 : inputs) {
                List<Feature> features1 = input1.getFeatures();
                SimpleScorecardCategoricalExecutor pmmlModel = new SimpleScorecardCategoricalExecutor(
                        features1.get(0).getValue().asString(), features1.get(1).getValue().asString());
                PMML4Result result = pmmlModel.execute(scorecardCategoricalRuntime);
                String score = "" + result.getResultVariables().get(SimpleScorecardCategoricalExecutor.TARGET_FIELD);
                String reason1 = "" + result.getResultVariables().get(SimpleScorecardCategoricalExecutor.REASON_CODE1_FIELD);
                String reason2 = "" + result.getResultVariables().get(SimpleScorecardCategoricalExecutor.REASON_CODE2_FIELD);
                PredictionOutput predictionOutput = new PredictionOutput(List.of(
                        new Output("score", Type.TEXT, new Value<>(score), 1d),
                        new Output("reason1", Type.TEXT, new Value<>(reason1), 1d),
                        new Output("reason2", Type.TEXT, new Value<>(reason2), 1d)
                ));
                outputs.add(predictionOutput);
            }
            return outputs;
        });

        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new Prediction(input, output);
        Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
        for (Saliency saliency : saliencyMap.values()) {
            assertNotNull(saliency);
            double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(1));
            assertEquals(0.33d, v, 1e-2);
        }
    }

    @Test
    void testPMMLCompoundScorecard() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(100, 2, random);
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("input1", -50));
            features.add(FeatureFactory.newTextFeature("input2", "classB"));
            PredictionInput input = new PredictionInput(features);

            PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
                List<PredictionOutput> outputs = new LinkedList<>();
                for (PredictionInput input1 : inputs) {
                    List<Feature> features1 = input1.getFeatures();
                    CompoundNestedPredicateScorecardExecutor pmmlModel = new CompoundNestedPredicateScorecardExecutor(
                            features1.get(0).getValue().asNumber(), features1.get(1).getValue().asString());
                    PMML4Result result = pmmlModel.execute(compoundScoreCardRuntime);
                    String score = "" + result.getResultVariables().get(CompoundNestedPredicateScorecardExecutor.TARGET_FIELD);
                    String reason1 = "" + result.getResultVariables().get(CompoundNestedPredicateScorecardExecutor.REASON_CODE1_FIELD);
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(
                            new Output("score", Type.TEXT, new Value<>(score), 1d),
                            new Output("reason1", Type.TEXT, new Value<>(reason1), 1d)
                    ));
                    outputs.add(predictionOutput);
                }
                return outputs;
            });
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new Prediction(input, output);
            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            for (Saliency saliency : saliencyMap.values()) {
                assertNotNull(saliency);
                double v = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(2));
                assertEquals(1d, v);
            }
        }
    }
}
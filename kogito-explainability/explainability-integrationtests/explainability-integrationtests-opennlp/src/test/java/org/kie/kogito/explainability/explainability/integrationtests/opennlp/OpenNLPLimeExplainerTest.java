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
package org.kie.kogito.explainability.explainability.integrationtests.opennlp;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenNLPLimeExplainerTest {

    @BeforeAll
    static void init() {
        Config.INSTANCE.setAsyncTimeout(5000);
        Config.INSTANCE.setAsyncTimeUnit(TimeUnit.MILLISECONDS);
    }

    @Test
    void testOpenNLPLangDetect() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(100, 2, random);
            InputStream is = getClass().getResourceAsStream("/opennlp/langdetect-183.bin");
            LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(is);
            LanguageDetector languageDetector = new LanguageDetectorME(languageDetectorModel);

            PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
                List<PredictionOutput> results = new LinkedList<>();
                for (PredictionInput predictionInput : inputs) {
                    StringBuilder builder = new StringBuilder();
                    for (Feature f : predictionInput.getFeatures()) {
                        if (builder.length() > 0) {
                            builder.append(' ');
                        }
                        builder.append(f.getValue().asString());
                    }
                    Language language = languageDetector.predictLanguage(builder.toString());
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("lang", Type.TEXT, new Value<>(language.getLang()), language.getConfidence())));
                    results.add(predictionOutput);
                }
                return results;
            });

            String inputText = "italiani,spaghetti pizza mandolino";
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newFulltextFeature("text", inputText, s -> Arrays.asList(s.split("\\W"))));
            PredictionInput input = new PredictionInput(features);

            List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input)).get();
            assertNotNull(predictionOutputs);
            assertFalse(predictionOutputs.isEmpty());
            PredictionOutput output = predictionOutputs.get(0);
            assertNotNull(output);
            assertNotNull(output.getOutputs());
            assertEquals(1, output.getOutputs().size());
            assertEquals("ita", output.getOutputs().get(0).getValue().asString());
            assertEquals(0.03, output.getOutputs().get(0).getScore(), 1e-2);

            Prediction prediction = new Prediction(input, output);

            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            for (Saliency saliency : saliencyMap.values()) {
                assertNotNull(saliency);
                double i1 = ExplainabilityMetrics.impactScore(model, prediction, saliency.getPositiveFeatures(3));
                assertEquals(1d, i1);
            }
        }
    }
}
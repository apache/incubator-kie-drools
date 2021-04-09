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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
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
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;
import org.kie.kogito.explainability.utils.ValidationUtils;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenNLPLimeExplainerTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testOpenNLPLangDetect(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(100)
                .withPerturbationContext(new PerturbationContext(random, 2));
        LimeExplainer limeExplainer = new LimeExplainer(limeConfig);
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
                PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("lang", Type.TEXT, new Value(language.getLang()), language.getConfidence())));
                results.add(predictionOutput);
            }
            return results;
        });

        String inputText = "italiani,spaghetti pizza mandolino";
        List<Feature> features = new ArrayList<>();
        Function<String, List<String>> tokenizer = s -> Arrays.asList(s.split("\\W"));
        features.add(FeatureFactory.newFulltextFeature("text", inputText, tokenizer));
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
        assertDoesNotThrow(() -> ValidationUtils.validateLocalSaliencyStability(model, prediction, limeExplainer, 2,
                0.8, 0.8));

        List<String> texts = List.of("we want your money", "please reply quickly", "you are the lucky winner",
                "italiani, spaghetti pizza mandolino", "guten tag", "allez les bleus", "daje roma");

        List<PredictionInput> inputs = new ArrayList<>();
        for (String text : texts) {
            inputs.add(new PredictionInput(List.of(FeatureFactory.newFulltextFeature("text", text, tokenizer))));
        }

        String decision = "lang";
        DataDistribution distribution = new PredictionInputsDataDistribution(inputs);
        int k = 2;
        int chunkSize = 2;
        double f1 = ExplainabilityMetrics.getLocalSaliencyF1(decision, model, limeExplainer, distribution, k, chunkSize);
        assertThat(f1).isBetween(0.5d, 1d);
    }
}

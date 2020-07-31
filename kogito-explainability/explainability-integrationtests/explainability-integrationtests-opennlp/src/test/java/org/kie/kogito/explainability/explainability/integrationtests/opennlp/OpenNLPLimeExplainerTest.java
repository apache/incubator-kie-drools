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
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.junit.jupiter.api.Test;
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
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenNLPLimeExplainerTest {

    @Test
    void testOpenNLPLangDetect() throws Exception {
        for (int seed = 0; seed < 5; seed++) {
            DataUtils.setSeed(seed);
            InputStream is = getClass().getResourceAsStream("/opennlp/langdetect-183.bin");
            LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(is);
            String inputText = "italiani, spaghetti pizza mandolino";
            LanguageDetector languageDetector = new LanguageDetectorME(languageDetectorModel);
            Language bestLanguage = languageDetector.predictLanguage(inputText);

            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newTextFeature("text", inputText));
            PredictionInput input = new PredictionInput(features);
            PredictionOutput output = new PredictionOutput(List.of(new Output("lang", Type.TEXT, new Value<>(bestLanguage.getLang()),
                                                                              bestLanguage.getConfidence())));
            Prediction prediction = new Prediction(input, output);

            LimeExplainer limeExplainer = new LimeExplainer(100, 2);
            PredictionProvider model = inputs -> {
                List<PredictionOutput> results = new LinkedList<>();
                for (PredictionInput predictionInput : inputs) {
                    StringBuilder builder = new StringBuilder();
                    for (Feature f : predictionInput.getFeatures()) {
                        if (Type.TEXT.equals(f.getType())) {
                            if (builder.length() > 0) {
                                builder.append(' ');
                            }
                            builder.append(f.getValue().asString());
                        }
                    }
                    Language language = languageDetector.predictLanguage(builder.toString());
                    PredictionOutput predictionOutput = new PredictionOutput(List.of(new Output("lang", Type.TEXT, new Value<>(language.getLang()), language.getConfidence())));
                    results.add(predictionOutput);
                }
                return results;
            };
            Saliency saliency = limeExplainer.explain(prediction, model);
            assertNotNull(saliency);
            double i1 = ExplainabilityMetrics.impactScore(model, prediction, saliency.getTopFeatures(3));
            assertTrue(i1 > 0);
        }
    }
}
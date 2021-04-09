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
package org.kie.kogito.explainability.explainability.integrationtests.opennlp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.global.pdp.PartialDependencePlotExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import static org.assertj.core.api.Assertions.assertThat;

class OpenNLPPDPExplainerTest {

    @Test
    void testOpenNLPLangDetect() throws Exception {
        PartialDependencePlotExplainer partialDependencePlotExplainer = new PartialDependencePlotExplainer();
        InputStream is = getClass().getResourceAsStream("/opennlp/langdetect-183.bin");
        LanguageDetectorModel languageDetectorModel = new LanguageDetectorModel(is);
        LanguageDetector languageDetector = new LanguageDetectorME(languageDetectorModel);

        PredictionProvider model = inputs -> CompletableFuture.supplyAsync(() -> {
            List<PredictionOutput> results = new ArrayList<>();
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

        List<String> texts = List.of("we want your money", "please reply quickly", "you are the lucky winner",
                "italiani, spaghetti pizza mandolino", "guten tag", "allez les bleus", "daje roma");

        List<Prediction> predictions = new ArrayList<>();
        for (String text : texts) {
            List<Feature> features = new ArrayList<>();
            features.add(FeatureFactory.newFulltextFeature("text", text));
            PredictionInput predictionInput = new PredictionInput(features);
            PredictionOutput predictionOutput = model.predictAsync(List.of(predictionInput)).get().get(0);
            predictions.add(new Prediction(predictionInput, predictionOutput));
        }
        List<PartialDependenceGraph> pdps = partialDependencePlotExplainer.explainFromPredictions(model, predictions);
        assertThat(pdps).isNotEmpty();
    }

}

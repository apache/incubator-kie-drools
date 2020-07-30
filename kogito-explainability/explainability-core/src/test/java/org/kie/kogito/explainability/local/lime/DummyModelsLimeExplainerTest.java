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

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.DataUtils;
import org.kie.kogito.explainability.utils.ExplainabilityMetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DummyModelsLimeExplainerTest {

    @BeforeAll
    static void setUpBefore() {
        DataUtils.setSeed(4);
    }

    @Test
    void testMapOneFeatureToOutputRegression() {
        int idx = 1;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 100));
        features.add(FeatureFactory.newNumericalFeature("f2", 20));
        features.add(FeatureFactory.newNumericalFeature("f3", 0.1));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getFeaturePassModel(idx);
        List<PredictionOutput> outputs = model.predict(List.of(input));
        Prediction prediction = new Prediction(input, outputs.get(0));

        LimeExplainer limeExplainer = new LimeExplainer(100, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<FeatureImportance> topFeatures = saliency.getTopFeatures(3);
        assertEquals(topFeatures.get(0).getFeature().getName(), features.get(idx).getName());
        assertTrue(topFeatures.get(1).getScore() < topFeatures.get(0).getScore() / 2);
        assertTrue(topFeatures.get(2).getScore() < topFeatures.get(0).getScore() / 2);
        double v = ExplainabilityMetrics.saliencyImpact(model, prediction, saliency.getTopFeatures(1));
        assertThat(v).isGreaterThan(0);
    }

    @Test
    void testUnusedFeatureRegression() {
        int idx = 2;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 100));
        features.add(FeatureFactory.newNumericalFeature("f2", 20));
        features.add(FeatureFactory.newNumericalFeature("f3", 10));
        PredictionProvider model = TestUtils.getSumSkipModel(idx);
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> outputs = model.predict(List.of(input));
        Prediction prediction = new Prediction(input, outputs.get(0));
        LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<FeatureImportance> perFeatureImportance = saliency.getPerFeatureImportance();

        perFeatureImportance.sort((t1, t2) -> (int) (t2.getScore() - t1.getScore()));
        assertTrue(perFeatureImportance.get(0).getScore() > 0);
        assertTrue(perFeatureImportance.get(1).getScore() > 0);
        assertEquals(features.get(idx).getName(), perFeatureImportance.get(2).getFeature().getName());
        double v = ExplainabilityMetrics.saliencyImpact(model, prediction, saliency.getTopFeatures(1));
        assertThat(v).isGreaterThan(0);
    }

    @Test
    void testMapOneFeatureToOutputClassification() {
        int idx = 1;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 1));
        features.add(FeatureFactory.newNumericalFeature("f2", 2));
        features.add(FeatureFactory.newNumericalFeature("f3", 3));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getEvenFeatureModel(idx);
        List<PredictionOutput> outputs = model.predict(List.of(input));
        Prediction prediction = new Prediction(input, outputs.get(0));

        LimeExplainer limeExplainer = new LimeExplainer(100, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(1);
        assertFalse(topFeatures.isEmpty());
        assertEquals(features.get(idx).getName(), topFeatures.get(0).getFeature().getName());
    }

    @Test
    void testTextSpamClassification() {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newTextFeature("f1", "we go here and there"));
        features.add(FeatureFactory.newTextFeature("f2", "please give me some money"));
        features.add(FeatureFactory.newTextFeature("f3", "dear friend, please reply"));
        PredictionInput input = new PredictionInput(features);
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        List<PredictionOutput> outputs = model.predict(List.of(input));
        Prediction prediction = new Prediction(input, outputs.get(0));

        LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<FeatureImportance> topFeatures = saliency.getPositiveFeatures(1);
        assertEquals("money (f2)", topFeatures.get(0).getFeature().getName());
        double v = ExplainabilityMetrics.saliencyImpact(model, prediction, saliency.getTopFeatures(1));
        assertThat(v).isGreaterThan(0);
    }

    @Test
    void testUnusedFeatureClassification() {
        int idx = 2;
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("f1", 6));
        features.add(FeatureFactory.newNumericalFeature("f2", 3));
        features.add(FeatureFactory.newNumericalFeature("f3", 5));
        PredictionProvider model = TestUtils.getEvenSumModel(idx);
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> outputs = model.predict(List.of(input));
        Prediction prediction = new Prediction(input, outputs.get(0));
        LimeExplainer limeExplainer = new LimeExplainer(1000, 1);
        Saliency saliency = limeExplainer.explain(prediction, model);

        assertNotNull(saliency);
        List<FeatureImportance> perFeatureImportance = saliency.getNegativeFeatures(3);
        assertFalse(perFeatureImportance.stream().map(fi -> fi.getFeature().getName()).collect(Collectors.toList()).contains(features.get(idx).getName()));
        double v = ExplainabilityMetrics.saliencyImpact(model, prediction, saliency.getNegativeFeatures(2));
        assertThat(v).isGreaterThan(0);
    }
}
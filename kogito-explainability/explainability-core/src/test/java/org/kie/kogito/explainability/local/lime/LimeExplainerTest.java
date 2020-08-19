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
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class LimeExplainerTest {

    @Test
    void testEmptyPrediction() {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(10, 1, random);
            PredictionOutput output = mock(PredictionOutput.class);
            PredictionInput input = mock(PredictionInput.class);
            Prediction prediction = new Prediction(input, output);
            PredictionProvider model = mock(PredictionProvider.class);
            Assertions.assertThrows(LocalExplanationException.class, () -> limeExplainer.explain(prediction, model));
        }
    }

    @Test
    void testNonEmptyInput() {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(10, 1, random);
            PredictionOutput output = mock(PredictionOutput.class);
            List<Feature> features = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                features.add(TestUtils.getMockedNumericFeature());
            }
            PredictionInput input = new PredictionInput(features);
            Prediction prediction = new Prediction(input, output);
            PredictionProvider model = mock(PredictionProvider.class);
            Map<String, Saliency> saliencyMap = limeExplainer.explain(prediction, model);
            assertNotNull(saliencyMap);
        }
    }
}
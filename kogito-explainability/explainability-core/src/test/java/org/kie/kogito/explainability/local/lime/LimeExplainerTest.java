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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.LocalExplanationException;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimeExplainerTest {

    @Test
    void testEmptyPrediction() throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(10, 1, random);
            PredictionInput input = new PredictionInput(Collections.emptyList());
            PredictionProvider model = TestUtils.getSumSkipModel(0);
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new Prediction(input, output);

            assertThrows(LocalExplanationException.class, () -> limeExplainer.explainAsync(prediction, model));
        }
    }

    @Test
    void testNonEmptyInput() throws ExecutionException, InterruptedException, TimeoutException {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            LimeExplainer limeExplainer = new LimeExplainer(10, 1, random);
            List<Feature> features = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                features.add(TestUtils.getMockedNumericFeature(i));
            }
            PredictionInput input = new PredictionInput(features);
            PredictionProvider model = TestUtils.getSumSkipModel(0);
            PredictionOutput output = model.predictAsync(List.of(input))
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                    .get(0);
            Prediction prediction = new Prediction(input, output);
            Map<String, Saliency> saliencyMap = limeExplainer.explainAsync(prediction, model)
                    .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit());
            assertNotNull(saliencyMap);
        }
    }
}
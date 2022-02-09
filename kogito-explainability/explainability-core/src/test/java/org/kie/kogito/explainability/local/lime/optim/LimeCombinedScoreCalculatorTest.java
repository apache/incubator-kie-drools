/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.local.lime.optim;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LimeCombinedScoreCalculatorTest {

    @Test
    void testScoreWithEmptyPredictions() {
        LimeCombinedScoreCalculator scoreCalculator = new LimeCombinedScoreCalculator();
        LimeConfig config = new LimeConfig();
        List<Prediction> predictions = Collections.emptyList();
        List<LimeConfigEntity> entities = Collections.emptyList();
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        LimeConfigSolution solution = new LimeConfigSolution(config, predictions, entities, model);
        SimpleBigDecimalScore score = scoreCalculator.calculateScore(solution);
        assertThat(score).isNotNull();
        assertThat(score.getScore()).isNotNull().isEqualTo(BigDecimal.valueOf(0));
    }

    @Test
    void testNonZeroScore() throws ExecutionException, InterruptedException, TimeoutException {
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        LimeCombinedScoreCalculator scoreCalculator = new LimeCombinedScoreCalculator();
        LimeConfig config = new LimeConfig();
        List<Feature> features = List.of(FeatureFactory.newFulltextFeature("text", "money so they say is the root of all evil today"));
        PredictionInput input = new PredictionInput(features);
        List<PredictionOutput> predictionOutputs = model.predictAsync(List.of(input))
                .get(Config.DEFAULT_ASYNC_TIMEOUT, Config.DEFAULT_ASYNC_TIMEUNIT);
        assertThat(predictionOutputs).isNotNull();
        assertThat(predictionOutputs.size()).isEqualTo(1);
        PredictionOutput output = predictionOutputs.get(0);
        Prediction prediction = new SimplePrediction(input, output);
        List<Prediction> predictions = List.of(prediction);
        List<LimeConfigEntity> entities = LimeConfigEntityFactory.createEncodingEntities(config);
        LimeConfigSolution solution = new LimeConfigSolution(config, predictions, entities, model);
        SimpleBigDecimalScore score = scoreCalculator.calculateScore(solution);
        assertThat(score).isNotNull();
        assertThat(score.getScore()).isNotNull().isNotEqualTo(BigDecimal.valueOf(0));
    }

}
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
package org.kie.kogito.explainability.local.lime.optim;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LimeStabilityScoreCalculatorTest {

    @Test
    void testScoreWithEmptyPredictions() {
        LimeStabilityScoreCalculator scoreCalculator = new LimeStabilityScoreCalculator();
        LimeConfig config = new LimeConfig();
        List<Prediction> predictions = Collections.emptyList();
        List<LimeConfigEntity> entities = Collections.emptyList();
        PredictionProvider model = TestUtils.getDummyTextClassifier();
        LimeConfigSolution solution = new LimeConfigSolution(config, predictions, entities, model);
        SimpleBigDecimalScore score = scoreCalculator.calculateScore(solution);
        assertThat(score).isNotNull();
        assertThat(score.getScore()).isNotNull();
        assertThat(score.getScore()).isEqualTo(BigDecimal.valueOf(0));
    }
}

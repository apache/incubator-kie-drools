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
package org.kie.kogito.explainability.local.counterfactual.score;

import java.math.BigDecimal;

import org.kie.kogito.explainability.local.counterfactual.CounterfactualSolution;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

/**
 * Mock score calculation class which guarantees an increasing soft score with each call
 */
public class MockCounterFactualScoreCalculator implements EasyScoreCalculator<CounterfactualSolution, BendableBigDecimalScore> {

    private double score = -1000.0;
    private int steps = 0;

    public MockCounterFactualScoreCalculator() {

    }

    @Override
    public BendableBigDecimalScore calculateScore(CounterfactualSolution solution) {

        steps += 1;

        if (steps % 10 == 0) {
            this.score += 0.1;
        }

        return BendableBigDecimalScore.of(
                new BigDecimal[] {
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(0),
                        BigDecimal.valueOf(0)
                },
                new BigDecimal[] { BigDecimal.valueOf(0), BigDecimal.valueOf(score) });
    }
}

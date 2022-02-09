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

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;

/**
 * A score calculator which combines stability and impact-score scores.
 */
public class LimeCombinedScoreCalculator implements EasyScoreCalculator<LimeConfigSolution, SimpleBigDecimalScore> {

    private final EasyScoreCalculator<LimeConfigSolution, SimpleBigDecimalScore> stability = new LimeStabilityScoreCalculator();
    private final EasyScoreCalculator<LimeConfigSolution, SimpleBigDecimalScore> impact = new LimeImpactScoreCalculator();
    private final double impactWeight;
    private final double stabilityWeight;

    public LimeCombinedScoreCalculator() {
        this(0.5, 0.5);
    }

    public LimeCombinedScoreCalculator(double stabilityWeight, double impactWeight) {
        this.stabilityWeight = stabilityWeight;
        this.impactWeight = impactWeight;
    }

    @Override
    public SimpleBigDecimalScore calculateScore(LimeConfigSolution limeConfigSolution) {
        return stability.calculateScore(limeConfigSolution).multiply(stabilityWeight).add(
                impact.calculateScore(limeConfigSolution).multiply(impactWeight));
    }

}

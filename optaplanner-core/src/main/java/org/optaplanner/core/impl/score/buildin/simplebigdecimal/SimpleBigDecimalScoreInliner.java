/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class SimpleBigDecimalScoreInliner extends ScoreInliner<SimpleBigDecimalScore> {

    protected BigDecimal score = BigDecimal.ZERO;

    protected SimpleBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public BigDecimalWeightedScoreImpacter buildWeightedScoreImpacter(SimpleBigDecimalScore constraintWeight) {
        if (constraintWeight.equals(SimpleBigDecimalScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        BigDecimal simpleConstraintWeight = constraintWeight.getScore();
        return (BigDecimal matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
            BigDecimal impact = simpleConstraintWeight.multiply(matchWeight);
            this.score = this.score.add(impact);
            if (constraintMatchEnabled) {
                matchScoreConsumer.accept(SimpleBigDecimalScore.of(impact));
            }
            return () -> this.score = this.score.subtract(impact);
        };
    }

    @Override
    public SimpleBigDecimalScore extractScore(int initScore) {
        return SimpleBigDecimalScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

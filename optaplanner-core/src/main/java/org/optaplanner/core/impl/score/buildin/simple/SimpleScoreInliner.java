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

package org.optaplanner.core.impl.score.buildin.simple;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class SimpleScoreInliner extends ScoreInliner<SimpleScore> {

    protected int score;

    protected SimpleScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public IntWeightedScoreImpacter buildWeightedScoreImpacter(SimpleScore constraintWeight) {
        if (constraintWeight.equals(SimpleScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        int simpleConstraintWeight = constraintWeight.getScore();
        return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
            int impact = simpleConstraintWeight * matchWeight;
            this.score += impact;
            if (constraintMatchEnabled) {
                matchScoreConsumer.accept(SimpleScore.of(impact));
            }
            return () -> this.score -= impact;
        };
    }

    @Override
    public SimpleScore extractScore(int initScore) {
        return SimpleScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleScore.class.getSimpleName() + " inliner";
    }

}

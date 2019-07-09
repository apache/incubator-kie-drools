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

package org.optaplanner.core.impl.score.buildin.simplelong;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class SimpleLongScoreInliner extends ScoreInliner<SimpleLongScore> {

    protected long score;

    protected SimpleLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public LongWeightedScoreImpacter buildWeightedScoreImpacter(SimpleLongScore constraintWeight) {
        if (constraintWeight.equals(SimpleLongScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        long simpleConstraintWeight = constraintWeight.getScore();
        return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
            long impact = simpleConstraintWeight * matchWeight;
            this.score += impact;
            if (constraintMatchEnabled) {
                matchScoreConsumer.accept(SimpleLongScore.of(impact));
            }
            return () -> this.score -= impact;
        };
    }

    @Override
    public SimpleLongScore extractScore(int initScore) {
        return SimpleLongScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleLongScore.class.getSimpleName() + " inliner";
    }

}

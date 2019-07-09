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

package org.optaplanner.core.impl.score.buildin.hardsoft;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class HardSoftScoreInliner extends ScoreInliner<HardSoftScore> {

    protected int hardScore;
    protected int softScore;

    protected HardSoftScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public IntWeightedScoreImpacter buildWeightedScoreImpacter(HardSoftScore constraintWeight) {
        if (constraintWeight.equals(HardSoftScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        int hardConstraintWeight = constraintWeight.getHardScore();
        int softConstraintWeight = constraintWeight.getSoftScore();
        if (softConstraintWeight == 0) {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftScore.ofHard(hardImpact));
                }
                return () -> this.hardScore -= hardImpact;
            };
        } else if (hardConstraintWeight == 0) {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftScore.ofSoft(softImpact));
                }
                return () -> this.softScore -= softImpact;
            };
        } else {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                int softImpact = softConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftScore.of(hardImpact, softImpact));
                }
                return () -> {
                    this.hardScore -= hardImpact;
                    this.softScore -= softImpact;
                };
            };
        }
    }

    @Override
    public HardSoftScore extractScore(int initScore) {
        return HardSoftScore.ofUninitialized(initScore, hardScore, softScore);
    }

    @Override
    public String toString() {
        return HardSoftScore.class.getSimpleName() + " inliner";
    }

}

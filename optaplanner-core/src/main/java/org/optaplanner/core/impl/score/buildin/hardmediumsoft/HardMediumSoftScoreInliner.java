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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class HardMediumSoftScoreInliner extends ScoreInliner<HardMediumSoftScore> {

    protected int hardScore;
    protected int mediumScore;
    protected int softScore;

    protected HardMediumSoftScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public IntWeightedScoreImpacter buildWeightedScoreImpacter(HardMediumSoftScore constraintWeight) {
        if (constraintWeight.equals(HardMediumSoftScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        int hardConstraintWeight = constraintWeight.getHardScore();
        int mediumConstraintWeight = constraintWeight.getMediumScore();
        int softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight == 0 && softConstraintWeight == 0) {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftScore.ofHard(hardImpact));
                }
                return () -> this.hardScore -= hardImpact;
            };
        } else if (hardConstraintWeight == 0 && softConstraintWeight == 0) {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int mediumImpact = mediumConstraintWeight * matchWeight;
                this.mediumScore += mediumImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftScore.ofMedium(mediumImpact));
                }
                return () -> this.mediumScore -= mediumImpact;
            };
        } else if (hardConstraintWeight == 0 && mediumConstraintWeight == 0) {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftScore.ofSoft(softImpact));
                }
                return () -> this.softScore -= softImpact;
            };
        } else {
            return (int matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                int mediumImpact = mediumConstraintWeight * matchWeight;
                int softImpact = softConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                this.mediumScore += mediumImpact;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftScore.of(hardImpact, mediumImpact, softImpact));
                }
                return () -> {
                    this.hardScore -= hardImpact;
                    this.mediumScore -= mediumImpact;
                    this.softScore -= softImpact;
                };
            };
        }
    }

    @Override
    public HardMediumSoftScore extractScore(int initScore) {
        return HardMediumSoftScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public String toString() {
        return HardMediumSoftScore.class.getSimpleName() + " inliner";
    }

}

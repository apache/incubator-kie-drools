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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class HardMediumSoftLongScoreInliner extends ScoreInliner<HardMediumSoftLongScore> {

    protected long hardScore;
    protected long mediumScore;
    protected long softScore;

    protected HardMediumSoftLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public LongWeightedScoreImpacter buildWeightedScoreImpacter(HardMediumSoftLongScore constraintWeight) {
        if (constraintWeight.equals(HardMediumSoftLongScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        long hardConstraintWeight = constraintWeight.getHardScore();
        long mediumConstraintWeight = constraintWeight.getMediumScore();
        long softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight == 0L && softConstraintWeight == 0L) {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftLongScore.ofHard(hardImpact));
                }
                return () -> this.hardScore -= hardImpact;
            };
        } else if (hardConstraintWeight == 0L && softConstraintWeight == 0L) {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long mediumImpact = mediumConstraintWeight * matchWeight;
                this.mediumScore += mediumImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftLongScore.ofMedium(mediumImpact));
                }
                return () -> this.mediumScore -= mediumImpact;
            };
        } else if (hardConstraintWeight == 0L && mediumConstraintWeight == 0L) {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftLongScore.ofSoft(softImpact));
                }
                return () -> this.softScore -= softImpact;
            };
        } else {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                long mediumImpact = mediumConstraintWeight * matchWeight;
                long softImpact = softConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                this.mediumScore += mediumImpact;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftLongScore.of(hardImpact, mediumImpact, softImpact));
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
    public HardMediumSoftLongScore extractScore(int initScore) {
        return HardMediumSoftLongScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public String toString() {
        return HardMediumSoftLongScore.class.getSimpleName() + " inliner";
    }

}

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

package org.optaplanner.core.impl.score.buildin.hardsoftlong;

import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class HardSoftLongScoreInliner extends ScoreInliner<HardSoftLongScore> {

    protected long hardScore;
    protected long softScore;

    protected HardSoftLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public LongWeightedScoreImpacter buildWeightedScoreImpacter(HardSoftLongScore constraintWeight) {
        if (constraintWeight.equals(HardSoftLongScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        long hardConstraintWeight = constraintWeight.getHardScore();
        long softConstraintWeight = constraintWeight.getSoftScore();
        if (softConstraintWeight == 0L) {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftLongScore.ofHard(hardImpact));
                }
                return () -> this.hardScore -= hardImpact;
            };
        } else if (hardConstraintWeight == 0L) {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftLongScore.ofSoft(softImpact));
                }
                return () -> this.softScore -= softImpact;
            };
        } else {
            return (long matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                long softImpact = softConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                this.softScore += softImpact;
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardSoftLongScore.of(hardImpact, softImpact));
                }
                return () -> {
                    this.hardScore -= hardImpact;
                    this.softScore -= softImpact;
                };
            };
        }
    }

    @Override
    public HardSoftLongScore extractScore(int initScore) {
        return HardSoftLongScore.ofUninitialized(initScore, hardScore, softScore);
    }

    @Override
    public String toString() {
        return HardSoftLongScore.class.getSimpleName() + " inliner";
    }

}

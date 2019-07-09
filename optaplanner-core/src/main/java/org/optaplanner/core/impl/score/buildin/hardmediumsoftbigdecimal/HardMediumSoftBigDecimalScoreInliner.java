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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public class HardMediumSoftBigDecimalScoreInliner extends ScoreInliner<HardMediumSoftBigDecimalScore> {

    protected BigDecimal hardScore = BigDecimal.ZERO;
    protected BigDecimal mediumScore = BigDecimal.ZERO;
    protected BigDecimal softScore = BigDecimal.ZERO;

    protected HardMediumSoftBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public BigDecimalWeightedScoreImpacter buildWeightedScoreImpacter(HardMediumSoftBigDecimalScore constraintWeight) {
        if (constraintWeight.equals(HardMediumSoftBigDecimalScore.ZERO)) {
            throw new IllegalArgumentException("The constraintWeight (" + constraintWeight + ") cannot be zero,"
                    + " this constraint should have been culled during node creation.");
        }
        BigDecimal hardConstraintWeight = constraintWeight.getHardScore();
        BigDecimal mediumConstraintWeight = constraintWeight.getMediumScore();
        BigDecimal softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return (BigDecimal matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftBigDecimalScore.ofHard(hardImpact));
                }
                return () -> this.hardScore = this.hardScore.subtract(hardImpact);
            };
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return (BigDecimal matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                BigDecimal mediumImpact = mediumConstraintWeight.multiply(matchWeight);
                this.mediumScore = this.mediumScore.add(mediumImpact);
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftBigDecimalScore.ofMedium(mediumImpact));
                }
                return () -> this.mediumScore = this.mediumScore.subtract(mediumImpact);
            };
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && mediumConstraintWeight.equals(BigDecimal.ZERO)) {
            return (BigDecimal matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.softScore = this.softScore.add(softImpact);
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftBigDecimalScore.ofSoft(softImpact));
                }
                return () -> this.softScore = this.softScore.subtract(softImpact);
            };
        } else {
            return (BigDecimal matchWeight, Consumer<Score<?>> matchScoreConsumer) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                BigDecimal mediumImpact = mediumConstraintWeight.multiply(matchWeight);
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                this.mediumScore = this.mediumScore.add(mediumImpact);
                this.softScore = this.softScore.add(softImpact);
                if (constraintMatchEnabled) {
                    matchScoreConsumer.accept(HardMediumSoftBigDecimalScore.of(hardImpact, mediumImpact, softImpact));
                }
                return () -> {
                    this.hardScore = this.hardScore.subtract(hardImpact);
                    this.mediumScore = this.mediumScore.subtract(mediumImpact);
                    this.softScore = this.softScore.subtract(softImpact);
                };
            };
        }
    }

    @Override
    public HardMediumSoftBigDecimalScore extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public String toString() {
        return HardMediumSoftBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

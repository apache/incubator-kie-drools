/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public final class BendableBigDecimalScoreInliner extends ScoreInliner<BendableBigDecimalScore> {

    private final BigDecimal[] hardScores;
    private final BigDecimal[] softScores;

    public BendableBigDecimalScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled, BendableBigDecimalScore.zero(hardLevelsSize, softLevelsSize));
        hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
    }

    @Override
    public BigDecimalWeightedScoreImpacter buildWeightedScoreImpacter(String constraintPackage, String constraintName,
            BendableBigDecimalScore constraintWeight) {
        assertNonZeroConstraintWeight(constraintWeight);
        Integer singleLevel = null;
        for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
            if (!constraintWeight.getHardOrSoftScore(i).equals(BigDecimal.ZERO)) {
                if (singleLevel != null) {
                    singleLevel = null;
                    break;
                }
                singleLevel = i;
            }
        }
        String constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName); // Cache.
        if (singleLevel != null) {
            BigDecimal levelWeight = constraintWeight.getHardOrSoftScore(singleLevel);
            if (singleLevel < constraintWeight.getHardLevelsSize()) {
                int level = singleLevel;
                return (BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    BigDecimal hardImpact = levelWeight.multiply(matchWeight);
                    this.hardScores[level] = this.hardScores[level].add(hardImpact);
                    UndoScoreImpacter undoScoreImpact =
                            () -> this.hardScores[level] = this.hardScores[level].subtract(hardImpact);
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                            constraintWeight,
                            BendableBigDecimalScore.ofHard(hardScores.length, softScores.length, level, hardImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                };
            } else {
                int level = singleLevel - constraintWeight.getHardLevelsSize();
                return (BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    BigDecimal softImpact = levelWeight.multiply(matchWeight);
                    this.softScores[level] = this.softScores[level].add(softImpact);
                    UndoScoreImpacter undoScoreImpact =
                            () -> this.softScores[level] = this.softScores[level].subtract(softImpact);
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                            constraintWeight,
                            BendableBigDecimalScore.ofSoft(hardScores.length, softScores.length, level, softImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                };
            }
        } else {
            return (BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal[] hardImpacts = new BigDecimal[hardScores.length];
                BigDecimal[] softImpacts = new BigDecimal[softScores.length];
                for (int i = 0; i < hardImpacts.length; i++) {
                    hardImpacts[i] = constraintWeight.getHardScore(i).multiply(matchWeight);
                    this.hardScores[i] = this.hardScores[i].add(hardImpacts[i]);
                }
                for (int i = 0; i < softImpacts.length; i++) {
                    softImpacts[i] = constraintWeight.getSoftScore(i).multiply(matchWeight);
                    this.softScores[i] = this.softScores[i].add(softImpacts[i]);
                }
                UndoScoreImpacter undoScoreImpact = () -> {
                    for (int i = 0; i < hardImpacts.length; i++) {
                        this.hardScores[i] = this.hardScores[i].subtract(hardImpacts[i]);
                    }
                    for (int i = 0; i < softImpacts.length; i++) {
                        this.softScores[i] = this.softScores[i].subtract(softImpacts[i]);
                    }
                };
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                        constraintWeight, BendableBigDecimalScore.of(hardImpacts, softImpacts),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            };
        }
    }

    @Override
    public BendableBigDecimalScore extractScore(int initScore) {
        return BendableBigDecimalScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

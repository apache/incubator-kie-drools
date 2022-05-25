/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common.inliner;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

public final class BendableLongScoreInliner extends AbstractScoreInliner<BendableLongScore> {

    private final long[] hardScores;
    private final long[] softScores;

    BendableLongScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, BendableLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        Integer singleLevel = null;
        for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
            if (constraintWeight.getHardOrSoftScore(i) != 0L) {
                if (singleLevel != null) {
                    singleLevel = null;
                    break;
                }
                singleLevel = i;
            }
        }
        if (singleLevel != null) {
            long levelWeight = constraintWeight.getHardOrSoftScore(singleLevel);
            if (singleLevel < constraintWeight.getHardLevelsSize()) {
                int level = singleLevel;
                return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    long hardImpact = levelWeight * matchWeight;
                    this.hardScores[level] += hardImpact;
                    UndoScoreImpacter undoScoreImpact = () -> this.hardScores[level] -= hardImpact;
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                            BendableLongScore.ofHard(hardScores.length, softScores.length, level, hardImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                });
            } else {
                int level = singleLevel - constraintWeight.getHardLevelsSize();
                return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    long softImpact = levelWeight * matchWeight;
                    this.softScores[level] += softImpact;
                    UndoScoreImpacter undoScoreImpact = () -> this.softScores[level] -= softImpact;
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                            BendableLongScore.ofSoft(hardScores.length, softScores.length, level, softImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                });
            }
        } else {
            return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                long[] hardImpacts = new long[hardScores.length];
                long[] softImpacts = new long[softScores.length];
                for (int i = 0; i < hardImpacts.length; i++) {
                    hardImpacts[i] = constraintWeight.getHardScore(i) * matchWeight;
                    this.hardScores[i] += hardImpacts[i];
                }
                for (int i = 0; i < softImpacts.length; i++) {
                    softImpacts[i] = constraintWeight.getSoftScore(i) * matchWeight;
                    this.softScores[i] += softImpacts[i];
                }
                UndoScoreImpacter undoScoreImpact = () -> {
                    for (int i = 0; i < hardImpacts.length; i++) {
                        this.hardScores[i] -= hardImpacts[i];
                    }
                    for (int i = 0; i < softImpacts.length; i++) {
                        this.softScores[i] -= softImpacts[i];
                    }
                };
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        BendableLongScore.of(hardImpacts, softImpacts), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        }
    }

    @Override
    public BendableLongScore extractScore(int initScore) {
        return BendableLongScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableLongScore.class.getSimpleName() + " inliner";
    }

}

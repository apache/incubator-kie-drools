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

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftLongScoreInliner extends AbstractScoreInliner<HardMediumSoftLongScore> {

    private long hardScore;
    private long mediumScore;
    private long softScore;

    HardMediumSoftLongScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, HardMediumSoftLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        long hardConstraintWeight = constraintWeight.getHardScore();
        long mediumConstraintWeight = constraintWeight.getMediumScore();
        long softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.hardScore -= hardImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftLongScore.ofHard(hardImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else if (hardConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                long mediumImpact = mediumConstraintWeight * matchWeight;
                this.mediumScore += mediumImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.mediumScore -= mediumImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftLongScore.ofMedium(mediumImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else if (hardConstraintWeight == 0L && mediumConstraintWeight == 0L) {
            return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                long softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.softScore -= softImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftLongScore.ofSoft(softImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else {
            return WeightedScoreImpacter.of((long matchWeight, JustificationsSupplier justificationsSupplier) -> {
                long hardImpact = hardConstraintWeight * matchWeight;
                long mediumImpact = mediumConstraintWeight * matchWeight;
                long softImpact = softConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                this.mediumScore += mediumImpact;
                this.softScore += softImpact;
                UndoScoreImpacter undoScoreImpact = () -> {
                    this.hardScore -= hardImpact;
                    this.mediumScore -= mediumImpact;
                    this.softScore -= softImpact;
                };
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftLongScore.of(hardImpact, mediumImpact, softImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
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

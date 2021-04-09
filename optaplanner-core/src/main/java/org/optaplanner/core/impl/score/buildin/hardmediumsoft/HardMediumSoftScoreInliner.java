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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.JustificationsSupplier;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public final class HardMediumSoftScoreInliner extends ScoreInliner<HardMediumSoftScore> {

    private int hardScore;
    private int mediumScore;
    private int softScore;

    protected HardMediumSoftScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardMediumSoftScore.ZERO);
    }

    @Override
    public IntWeightedScoreImpacter buildWeightedScoreImpacter(String constraintPackage, String constraintName,
            HardMediumSoftScore constraintWeight) {
        assertNonZeroConstraintWeight(constraintWeight);
        String constraintId = ConstraintMatchTotal.composeConstraintId(constraintPackage, constraintName); // Cache.
        int hardConstraintWeight = constraintWeight.getHardScore();
        int mediumConstraintWeight = constraintWeight.getMediumScore();
        int softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight == 0 && softConstraintWeight == 0) {
            return (int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                this.hardScore += hardImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.hardScore -= hardImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                        constraintWeight, HardMediumSoftScore.ofHard(hardImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            };
        } else if (hardConstraintWeight == 0 && softConstraintWeight == 0) {
            return (int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                int mediumImpact = mediumConstraintWeight * matchWeight;
                this.mediumScore += mediumImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.mediumScore -= mediumImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                        constraintWeight, HardMediumSoftScore.ofMedium(mediumImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            };
        } else if (hardConstraintWeight == 0 && mediumConstraintWeight == 0) {
            return (int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                int softImpact = softConstraintWeight * matchWeight;
                this.softScore += softImpact;
                UndoScoreImpacter undoScoreImpact = () -> this.softScore -= softImpact;
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                        constraintWeight, HardMediumSoftScore.ofSoft(softImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            };
        } else {
            return (int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                int hardImpact = hardConstraintWeight * matchWeight;
                int mediumImpact = mediumConstraintWeight * matchWeight;
                int softImpact = softConstraintWeight * matchWeight;
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
                Runnable undoConstraintMatch = addConstraintMatch(constraintId, constraintPackage, constraintName,
                        constraintWeight, HardMediumSoftScore.of(hardImpact, mediumImpact, softImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
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

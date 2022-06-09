package org.optaplanner.constraint.streams.common.inliner;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableScoreInliner extends AbstractScoreInliner<BendableScore> {

    private final int[] hardScores;
    private final int[] softScores;

    BendableScoreInliner(boolean constraintMatchEnabled,
            int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new int[hardLevelsSize];
        softScores = new int[softLevelsSize];
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, BendableScore constraintWeight) {
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
            int levelWeight = constraintWeight.getHardOrSoftScore(singleLevel);
            if (singleLevel < constraintWeight.getHardLevelsSize()) {
                int level = singleLevel;
                return WeightedScoreImpacter.of((int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    int hardImpact = levelWeight * matchWeight;
                    this.hardScores[level] += hardImpact;
                    UndoScoreImpacter undoScoreImpact = () -> this.hardScores[level] -= hardImpact;
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                            BendableScore.ofHard(hardScores.length, softScores.length, level, hardImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                });
            } else {
                int level = singleLevel - constraintWeight.getHardLevelsSize();
                return WeightedScoreImpacter.of((int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                    int softImpact = levelWeight * matchWeight;
                    this.softScores[level] += softImpact;
                    UndoScoreImpacter undoScoreImpact = () -> this.softScores[level] -= softImpact;
                    if (!constraintMatchEnabled) {
                        return undoScoreImpact;
                    }
                    Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                            BendableScore.ofSoft(hardScores.length, softScores.length, level, softImpact),
                            justificationsSupplier.get());
                    return () -> {
                        undoScoreImpact.run();
                        undoConstraintMatch.run();
                    };
                });
            }
        } else {
            return WeightedScoreImpacter.of((int matchWeight, JustificationsSupplier justificationsSupplier) -> {
                int[] hardImpacts = new int[hardScores.length];
                int[] softImpacts = new int[softScores.length];
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
                        BendableScore.of(hardImpacts, softImpacts), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        }
    }

    @Override
    public BendableScore extractScore(int initScore) {
        return BendableScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableScore.class.getSimpleName() + " inliner";
    }

}

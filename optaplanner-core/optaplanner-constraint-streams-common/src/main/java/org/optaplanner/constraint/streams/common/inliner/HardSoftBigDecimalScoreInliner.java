package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardSoftBigDecimalScoreInliner extends AbstractScoreInliner<HardSoftBigDecimalScore> {

    private BigDecimal hardScore = BigDecimal.ZERO;
    private BigDecimal softScore = BigDecimal.ZERO;

    HardSoftBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, HardSoftBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        BigDecimal hardConstraintWeight = constraintWeight.getHardScore();
        BigDecimal softConstraintWeight = constraintWeight.getSoftScore();
        if (softConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                UndoScoreImpacter undoScoreImpact = () -> this.hardScore = this.hardScore.subtract(hardImpact);
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardSoftBigDecimalScore.ofHard(hardImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.softScore = this.softScore.add(softImpact);
                UndoScoreImpacter undoScoreImpact = () -> this.softScore = this.softScore.subtract(softImpact);
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardSoftBigDecimalScore.ofSoft(softImpact), justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                this.softScore = this.softScore.add(softImpact);
                UndoScoreImpacter undoScoreImpact = () -> {
                    this.hardScore = this.hardScore.subtract(hardImpact);
                    this.softScore = this.softScore.subtract(softImpact);
                };
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardSoftBigDecimalScore.of(hardImpact, softImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        }
    }

    @Override
    public HardSoftBigDecimalScore extractScore(int initScore) {
        return HardSoftBigDecimalScore.ofUninitialized(initScore, hardScore, softScore);
    }

    @Override
    public String toString() {
        return HardSoftBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

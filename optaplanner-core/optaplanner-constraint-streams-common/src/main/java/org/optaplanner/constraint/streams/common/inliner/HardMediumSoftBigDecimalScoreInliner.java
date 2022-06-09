package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftBigDecimalScoreInliner extends AbstractScoreInliner<HardMediumSoftBigDecimalScore> {

    private BigDecimal hardScore = BigDecimal.ZERO;
    private BigDecimal mediumScore = BigDecimal.ZERO;
    private BigDecimal softScore = BigDecimal.ZERO;

    HardMediumSoftBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint,
            HardMediumSoftBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        BigDecimal hardConstraintWeight = constraintWeight.getHardScore();
        BigDecimal mediumConstraintWeight = constraintWeight.getMediumScore();
        BigDecimal softConstraintWeight = constraintWeight.getSoftScore();
        if (mediumConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                UndoScoreImpacter undoScoreImpact = () -> this.hardScore = this.hardScore.subtract(hardImpact);
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftBigDecimalScore.ofHard(hardImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && softConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal mediumImpact = mediumConstraintWeight.multiply(matchWeight);
                this.mediumScore = this.mediumScore.add(mediumImpact);
                UndoScoreImpacter undoScoreImpact = () -> this.mediumScore = this.mediumScore.subtract(mediumImpact);
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftBigDecimalScore.ofMedium(mediumImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else if (hardConstraintWeight.equals(BigDecimal.ZERO) && mediumConstraintWeight.equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.softScore = this.softScore.add(softImpact);
                UndoScoreImpacter undoScoreImpact = () -> this.softScore = this.softScore.subtract(softImpact);
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftBigDecimalScore.ofSoft(softImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
        } else {
            return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
                BigDecimal hardImpact = hardConstraintWeight.multiply(matchWeight);
                BigDecimal mediumImpact = mediumConstraintWeight.multiply(matchWeight);
                BigDecimal softImpact = softConstraintWeight.multiply(matchWeight);
                this.hardScore = this.hardScore.add(hardImpact);
                this.mediumScore = this.mediumScore.add(mediumImpact);
                this.softScore = this.softScore.add(softImpact);
                UndoScoreImpacter undoScoreImpact = () -> {
                    this.hardScore = this.hardScore.subtract(hardImpact);
                    this.mediumScore = this.mediumScore.subtract(mediumImpact);
                    this.softScore = this.softScore.subtract(softImpact);
                };
                if (!constraintMatchEnabled) {
                    return undoScoreImpact;
                }
                Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                        HardMediumSoftBigDecimalScore.of(hardImpact, mediumImpact, softImpact),
                        justificationsSupplier.get());
                return () -> {
                    undoScoreImpact.run();
                    undoConstraintMatch.run();
                };
            });
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

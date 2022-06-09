package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleBigDecimalScoreInliner extends AbstractScoreInliner<SimpleBigDecimalScore> {

    private BigDecimal score = BigDecimal.ZERO;

    SimpleBigDecimalScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter buildWeightedScoreImpacter(Constraint constraint, SimpleBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        BigDecimal simpleConstraintWeight = constraintWeight.getScore();
        return WeightedScoreImpacter.of((BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) -> {
            BigDecimal impact = simpleConstraintWeight.multiply(matchWeight);
            this.score = this.score.add(impact);
            UndoScoreImpacter undoScoreImpact = () -> this.score = this.score.subtract(impact);
            if (!constraintMatchEnabled) {
                return undoScoreImpact;
            }
            Runnable undoConstraintMatch = addConstraintMatch(constraint, constraintWeight,
                    SimpleBigDecimalScore.of(impact), justificationsSupplier.get());
            return () -> {
                undoScoreImpact.run();
                undoConstraintMatch.run();
            };
        });
    }

    @Override
    public SimpleBigDecimalScore extractScore(int initScore) {
        return SimpleBigDecimalScore.ofUninitialized(initScore, score);
    }

    @Override
    public String toString() {
        return SimpleBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

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
    public WeightedScoreImpacter<SimpleBigDecimalScore, SimpleBigDecimalScoreContext> buildWeightedScoreImpacter(
            Constraint constraint, SimpleBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        SimpleBigDecimalScoreContext context = new SimpleBigDecimalScoreContext(this, constraint, constraintWeight,
                impact -> this.score = this.score.add(impact));
        return WeightedScoreImpacter.of(context, SimpleBigDecimalScoreContext::changeScoreBy);
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

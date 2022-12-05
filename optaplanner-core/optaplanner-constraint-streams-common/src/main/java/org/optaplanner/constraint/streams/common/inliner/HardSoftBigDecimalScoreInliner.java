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
    public WeightedScoreImpacter<HardSoftBigDecimalScore, HardSoftBigDecimalScoreContext> buildWeightedScoreImpacter(
            Constraint constraint, HardSoftBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        HardSoftBigDecimalScoreContext context =
                new HardSoftBigDecimalScoreContext(this, constraint, constraintWeight,
                        impact -> this.hardScore = this.hardScore.add(impact),
                        impact -> this.softScore = this.softScore.add(impact));
        if (constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of(context, HardSoftBigDecimalScoreContext::changeHardScoreBy);
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO)) {
            return WeightedScoreImpacter.of(context, HardSoftBigDecimalScoreContext::changeSoftScoreBy);
        } else {
            return WeightedScoreImpacter.of(context, HardSoftBigDecimalScoreContext::changeScoreBy);
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

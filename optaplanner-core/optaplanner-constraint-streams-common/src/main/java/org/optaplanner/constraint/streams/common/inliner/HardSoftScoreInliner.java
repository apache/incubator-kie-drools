package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardSoftScoreInliner extends AbstractScoreInliner<HardSoftScore> {

    private int hardScore;
    private int softScore;

    HardSoftScoreInliner(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    @Override
    public WeightedScoreImpacter<HardSoftScore, HardSoftScoreContext> buildWeightedScoreImpacter(Constraint constraint,
            HardSoftScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        HardSoftScoreContext context = new HardSoftScoreContext(this, constraint, constraintWeight,
                impact -> this.hardScore += impact, impact -> this.softScore += impact);
        if (constraintWeight.softScore() == 0) {
            return WeightedScoreImpacter.of(context, HardSoftScoreContext::changeHardScoreBy);
        } else if (constraintWeight.hardScore() == 0) {
            return WeightedScoreImpacter.of(context, HardSoftScoreContext::changeSoftScoreBy);
        } else {
            return WeightedScoreImpacter.of(context, HardSoftScoreContext::changeScoreBy);
        }
    }

    @Override
    public HardSoftScore extractScore(int initScore) {
        return HardSoftScore.ofUninitialized(initScore, hardScore, softScore);
    }

    @Override
    public String toString() {
        return HardSoftScore.class.getSimpleName() + " inliner";
    }

}

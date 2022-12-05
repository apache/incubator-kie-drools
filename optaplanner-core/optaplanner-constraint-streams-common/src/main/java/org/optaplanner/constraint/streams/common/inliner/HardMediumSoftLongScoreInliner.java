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
    public WeightedScoreImpacter<HardMediumSoftLongScore, HardMediumSoftLongScoreContext> buildWeightedScoreImpacter(
            Constraint constraint, HardMediumSoftLongScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        long hardConstraintWeight = constraintWeight.getHardScore();
        long mediumConstraintWeight = constraintWeight.getMediumScore();
        long softConstraintWeight = constraintWeight.getSoftScore();
        HardMediumSoftLongScoreContext context = new HardMediumSoftLongScoreContext(this, constraint, constraintWeight,
                impact -> this.hardScore += impact, impact -> this.mediumScore += impact,
                impact -> this.softScore += impact);
        if (mediumConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeHardScoreBy(matchWeight, justificationsSupplier));
        } else if (hardConstraintWeight == 0L && softConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeMediumScoreBy(matchWeight, justificationsSupplier));
        } else if (hardConstraintWeight == 0L && mediumConstraintWeight == 0L) {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeSoftScoreBy(matchWeight, justificationsSupplier));
        } else {
            return WeightedScoreImpacter.of(context,
                    (HardMediumSoftLongScoreContext ctx, long matchWeight, JustificationsSupplier justificationsSupplier) -> ctx
                            .changeScoreBy(matchWeight, justificationsSupplier));
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

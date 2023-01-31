package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaplanner.constraint.streams.common.inliner.BendableBigDecimalScoreContext.IntBigDecimalConsumer;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableBigDecimalScoreInliner extends AbstractScoreInliner<BendableBigDecimalScore> {

    private final BigDecimal[] hardScores;
    private final BigDecimal[] softScores;

    BendableBigDecimalScoreInliner(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
    }

    @Override
    public WeightedScoreImpacter<BendableBigDecimalScore, BendableBigDecimalScoreContext> buildWeightedScoreImpacter(
            Constraint constraint, BendableBigDecimalScore constraintWeight) {
        validateConstraintWeight(constraint, constraintWeight);
        Integer singleLevel = null;
        for (int i = 0; i < constraintWeight.levelsSize(); i++) {
            if (!constraintWeight.hardOrSoftScore(i).equals(BigDecimal.ZERO)) {
                if (singleLevel != null) {
                    singleLevel = null;
                    break;
                }
                singleLevel = i;
            }
        }
        IntBigDecimalConsumer hardScoreUpdater =
                (scoreLevel, impact) -> this.hardScores[scoreLevel] = this.hardScores[scoreLevel].add(impact);
        IntBigDecimalConsumer softScoreUpdater =
                (scoreLevel, impact) -> this.softScores[scoreLevel] = this.softScores[scoreLevel].add(impact);
        if (singleLevel != null) {
            boolean isHardScore = singleLevel < constraintWeight.hardLevelsSize();
            int level = isHardScore ? singleLevel : singleLevel - constraintWeight.hardLevelsSize();
            BendableBigDecimalScoreContext context = new BendableBigDecimalScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, level, constraintWeight.hardOrSoftScore(singleLevel),
                    hardScoreUpdater, softScoreUpdater);
            if (isHardScore) {
                return WeightedScoreImpacter.of(context, BendableBigDecimalScoreContext::changeHardScoreBy);
            } else {
                return WeightedScoreImpacter.of(context, BendableBigDecimalScoreContext::changeSoftScoreBy);
            }
        } else {
            BendableBigDecimalScoreContext context = new BendableBigDecimalScoreContext(this, constraint, constraintWeight,
                    hardScores.length, softScores.length, hardScoreUpdater, softScoreUpdater);
            return WeightedScoreImpacter.of(context, BendableBigDecimalScoreContext::changeScoreBy);
        }
    }

    @Override
    public BendableBigDecimalScore extractScore(int initScore) {
        return BendableBigDecimalScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

    @Override
    public String toString() {
        return BendableBigDecimalScore.class.getSimpleName() + " inliner";
    }

}

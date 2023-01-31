package org.optaplanner.constraint.streams.common.inliner;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableBigDecimalScoreContext extends ScoreContext<BendableBigDecimalScore> {

    private final int hardScoreLevelCount;
    private final int softScoreLevelCount;
    private final int scoreLevel;
    private final BigDecimal scoreLevelWeight;
    private final IntBigDecimalConsumer softScoreLevelUpdater;
    private final IntBigDecimalConsumer hardScoreLevelUpdater;

    public BendableBigDecimalScoreContext(AbstractScoreInliner<BendableBigDecimalScore> parent, Constraint constraint,
            BendableBigDecimalScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount, int scoreLevel,
            BigDecimal scoreLevelWeight, IntBigDecimalConsumer hardScoreLevelUpdater,
            IntBigDecimalConsumer softScoreLevelUpdater) {
        super(parent, constraint, constraintWeight);
        this.hardScoreLevelCount = hardScoreLevelCount;
        this.softScoreLevelCount = softScoreLevelCount;
        this.scoreLevel = scoreLevel;
        this.scoreLevelWeight = scoreLevelWeight;
        this.softScoreLevelUpdater = softScoreLevelUpdater;
        this.hardScoreLevelUpdater = hardScoreLevelUpdater;
    }

    public BendableBigDecimalScoreContext(AbstractScoreInliner<BendableBigDecimalScore> parent, Constraint constraint,
            BendableBigDecimalScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount,
            IntBigDecimalConsumer hardScoreLevelUpdater, IntBigDecimalConsumer softScoreLevelUpdater) {
        this(parent, constraint, constraintWeight, hardScoreLevelCount, softScoreLevelCount, -1, BigDecimal.ZERO,
                hardScoreLevelUpdater,
                softScoreLevelUpdater);
    }

    public UndoScoreImpacter changeSoftScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal softImpact = scoreLevelWeight.multiply(matchWeight);
        softScoreLevelUpdater.accept(scoreLevel, softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreLevelUpdater.accept(scoreLevel, softImpact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableBigDecimalScore.ofSoft(hardScoreLevelCount, softScoreLevelCount, scoreLevel, softImpact),
                justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal hardImpact = scoreLevelWeight.multiply(matchWeight);
        hardScoreLevelUpdater.accept(scoreLevel, hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreLevelUpdater.accept(scoreLevel, hardImpact.negate());
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableBigDecimalScore.ofHard(hardScoreLevelCount, softScoreLevelCount, scoreLevel, hardImpact),
                justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(BigDecimal matchWeight, JustificationsSupplier justificationsSupplier) {
        BigDecimal[] hardImpacts = new BigDecimal[hardScoreLevelCount];
        BigDecimal[] softImpacts = new BigDecimal[softScoreLevelCount];
        for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
            BigDecimal hardImpact = constraintWeight.hardScore(hardScoreLevel).multiply(matchWeight);
            hardImpacts[hardScoreLevel] = hardImpact;
            hardScoreLevelUpdater.accept(hardScoreLevel, hardImpact);
        }
        for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
            BigDecimal softImpact = constraintWeight.softScore(softScoreLevel).multiply(matchWeight);
            softImpacts[softScoreLevel] = softImpact;
            softScoreLevelUpdater.accept(softScoreLevel, softImpact);
        }
        UndoScoreImpacter undoScoreImpact = () -> {
            for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
                hardScoreLevelUpdater.accept(hardScoreLevel, hardImpacts[hardScoreLevel].negate());
            }
            for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
                softScoreLevelUpdater.accept(softScoreLevel, softImpacts[softScoreLevel].negate());
            }
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, BendableBigDecimalScore.of(hardImpacts, softImpacts),
                justificationsSupplier);
    }

    public interface IntBigDecimalConsumer {

        void accept(int value1, BigDecimal value2);

    }

}

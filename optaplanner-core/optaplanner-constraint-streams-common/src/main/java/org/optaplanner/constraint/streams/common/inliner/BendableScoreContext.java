package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class BendableScoreContext extends ScoreContext<BendableScore> {

    private final int hardScoreLevelCount;
    private final int softScoreLevelCount;
    private final int scoreLevel;
    private final int scoreLevelWeight;
    private final IntBiConsumer softScoreLevelUpdater;
    private final IntBiConsumer hardScoreLevelUpdater;

    public BendableScoreContext(AbstractScoreInliner<BendableScore> parent, Constraint constraint,
            BendableScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount, int scoreLevel,
            int scoreLevelWeight, IntBiConsumer hardScoreLevelUpdater, IntBiConsumer softScoreLevelUpdater) {
        super(parent, constraint, constraintWeight);
        this.hardScoreLevelCount = hardScoreLevelCount;
        this.softScoreLevelCount = softScoreLevelCount;
        this.scoreLevel = scoreLevel;
        this.scoreLevelWeight = scoreLevelWeight;
        this.softScoreLevelUpdater = softScoreLevelUpdater;
        this.hardScoreLevelUpdater = hardScoreLevelUpdater;
    }

    public BendableScoreContext(AbstractScoreInliner<BendableScore> parent, Constraint constraint,
            BendableScore constraintWeight, int hardScoreLevelCount, int softScoreLevelCount,
            IntBiConsumer hardScoreLevelUpdater, IntBiConsumer softScoreLevelUpdater) {
        this(parent, constraint, constraintWeight, hardScoreLevelCount, softScoreLevelCount, -1, -1, hardScoreLevelUpdater,
                softScoreLevelUpdater);
    }

    public UndoScoreImpacter changeSoftScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int softImpact = scoreLevelWeight * matchWeight;
        softScoreLevelUpdater.accept(scoreLevel, softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreLevelUpdater.accept(scoreLevel, -softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableScore.ofSoft(hardScoreLevelCount, softScoreLevelCount, scoreLevel, softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int hardImpact = scoreLevelWeight * matchWeight;
        hardScoreLevelUpdater.accept(scoreLevel, hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreLevelUpdater.accept(scoreLevel, -hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact,
                BendableScore.ofHard(hardScoreLevelCount, softScoreLevelCount, scoreLevel, hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int[] hardImpacts = new int[hardScoreLevelCount];
        int[] softImpacts = new int[softScoreLevelCount];
        for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
            int hardImpact = constraintWeight.hardScore(hardScoreLevel) * matchWeight;
            hardImpacts[hardScoreLevel] = hardImpact;
            hardScoreLevelUpdater.accept(hardScoreLevel, hardImpact);
        }
        for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
            int softImpact = constraintWeight.softScore(softScoreLevel) * matchWeight;
            softImpacts[softScoreLevel] = softImpact;
            softScoreLevelUpdater.accept(softScoreLevel, softImpact);
        }
        UndoScoreImpacter undoScoreImpact = () -> {
            for (int hardScoreLevel = 0; hardScoreLevel < hardScoreLevelCount; hardScoreLevel++) {
                hardScoreLevelUpdater.accept(hardScoreLevel, -hardImpacts[hardScoreLevel]);
            }
            for (int softScoreLevel = 0; softScoreLevel < softScoreLevelCount; softScoreLevel++) {
                softScoreLevelUpdater.accept(softScoreLevel, -softImpacts[softScoreLevel]);
            }
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, BendableScore.of(hardImpacts, softImpacts), justificationsSupplier);
    }

    public interface IntBiConsumer {

        void accept(int value1, int value2);

    }

}

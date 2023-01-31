package org.optaplanner.constraint.streams.common.inliner;

import java.util.function.LongConsumer;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardSoftLongScoreContext extends ScoreContext<HardSoftLongScore> {

    private final LongConsumer softScoreUpdater;
    private final LongConsumer hardScoreUpdater;

    public HardSoftLongScoreContext(AbstractScoreInliner<HardSoftLongScore> parent, Constraint constraint,
            HardSoftLongScore constraintWeight, LongConsumer hardScoreUpdater, LongConsumer softScoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.softScoreUpdater = softScoreUpdater;
        this.hardScoreUpdater = hardScoreUpdater;
    }

    public UndoScoreImpacter changeSoftScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long softImpact = constraintWeight.softScore() * matchWeight;
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreUpdater.accept(-softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftLongScore.ofSoft(softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long hardImpact = constraintWeight.hardScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreUpdater.accept(-hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftLongScore.ofHard(hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long hardImpact = constraintWeight.hardScore() * matchWeight;
        long softImpact = constraintWeight.softScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> {
            hardScoreUpdater.accept(-hardImpact);
            softScoreUpdater.accept(-softImpact);
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardSoftLongScore.of(hardImpact, softImpact), justificationsSupplier);
    }

}

package org.optaplanner.constraint.streams.common.inliner;

import java.util.function.LongConsumer;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftLongScoreContext extends ScoreContext<HardMediumSoftLongScore> {

    private final LongConsumer softScoreUpdater;
    private final LongConsumer mediumScoreUpdater;
    private final LongConsumer hardScoreUpdater;

    public HardMediumSoftLongScoreContext(AbstractScoreInliner<HardMediumSoftLongScore> parent, Constraint constraint,
            HardMediumSoftLongScore constraintWeight, LongConsumer hardScoreUpdater, LongConsumer mediumScoreUpdater,
            LongConsumer softScoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.softScoreUpdater = softScoreUpdater;
        this.mediumScoreUpdater = mediumScoreUpdater;
        this.hardScoreUpdater = hardScoreUpdater;
    }

    public UndoScoreImpacter changeSoftScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long softImpact = constraintWeight.getSoftScore() * matchWeight;
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreUpdater.accept(-softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftLongScore.ofSoft(softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeMediumScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long mediumImpact = constraintWeight.mediumScore() * matchWeight;
        mediumScoreUpdater.accept(mediumImpact);
        UndoScoreImpacter undoScoreImpact = () -> mediumScoreUpdater.accept(-mediumImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftLongScore.ofMedium(mediumImpact),
                justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long hardImpact = constraintWeight.hardScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreUpdater.accept(-hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftLongScore.ofHard(hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long hardImpact = constraintWeight.hardScore() * matchWeight;
        long mediumImpact = constraintWeight.mediumScore() * matchWeight;
        long softImpact = constraintWeight.getSoftScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        mediumScoreUpdater.accept(mediumImpact);
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> {
            hardScoreUpdater.accept(-hardImpact);
            mediumScoreUpdater.accept(-mediumImpact);
            softScoreUpdater.accept(-softImpact);
        };
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftLongScore.of(hardImpact, mediumImpact, softImpact),
                justificationsSupplier);
    }

}

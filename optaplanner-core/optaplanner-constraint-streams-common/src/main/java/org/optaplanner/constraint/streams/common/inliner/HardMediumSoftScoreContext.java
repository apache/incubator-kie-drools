package org.optaplanner.constraint.streams.common.inliner;

import java.util.function.IntConsumer;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class HardMediumSoftScoreContext extends ScoreContext<HardMediumSoftScore> {

    private final IntConsumer softScoreUpdater;
    private final IntConsumer mediumScoreUpdater;
    private final IntConsumer hardScoreUpdater;

    public HardMediumSoftScoreContext(AbstractScoreInliner<HardMediumSoftScore> parent, Constraint constraint,
            HardMediumSoftScore constraintWeight, IntConsumer hardScoreUpdater, IntConsumer mediumScoreUpdater,
            IntConsumer softScoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.softScoreUpdater = softScoreUpdater;
        this.mediumScoreUpdater = mediumScoreUpdater;
        this.hardScoreUpdater = hardScoreUpdater;
    }

    public UndoScoreImpacter changeSoftScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int softImpact = constraintWeight.softScore() * matchWeight;
        softScoreUpdater.accept(softImpact);
        UndoScoreImpacter undoScoreImpact = () -> softScoreUpdater.accept(-softImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofSoft(softImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeMediumScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int mediumImpact = constraintWeight.mediumScore() * matchWeight;
        mediumScoreUpdater.accept(mediumImpact);
        UndoScoreImpacter undoScoreImpact = () -> mediumScoreUpdater.accept(-mediumImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofMedium(mediumImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeHardScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int hardImpact = constraintWeight.hardScore() * matchWeight;
        hardScoreUpdater.accept(hardImpact);
        UndoScoreImpacter undoScoreImpact = () -> hardScoreUpdater.accept(-hardImpact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.ofHard(hardImpact), justificationsSupplier);
    }

    public UndoScoreImpacter changeScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int hardImpact = constraintWeight.hardScore() * matchWeight;
        int mediumImpact = constraintWeight.mediumScore() * matchWeight;
        int softImpact = constraintWeight.softScore() * matchWeight;
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
        return impactWithConstraintMatch(undoScoreImpact, HardMediumSoftScore.of(hardImpact, mediumImpact, softImpact),
                justificationsSupplier);
    }

}

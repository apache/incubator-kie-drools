package org.optaplanner.constraint.streams.common.inliner;

import java.util.function.LongConsumer;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleLongScoreContext extends ScoreContext<SimpleLongScore> {

    private final LongConsumer scoreUpdater;

    public SimpleLongScoreContext(AbstractScoreInliner<SimpleLongScore> parent, Constraint constraint,
            SimpleLongScore constraintWeight, LongConsumer scoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.scoreUpdater = scoreUpdater;
    }

    public UndoScoreImpacter changeScoreBy(long matchWeight, JustificationsSupplier justificationsSupplier) {
        long impact = constraintWeight.score() * matchWeight;
        scoreUpdater.accept(impact);
        UndoScoreImpacter undoScoreImpact = () -> scoreUpdater.accept(-impact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, SimpleLongScore.of(impact), justificationsSupplier);
    }

}

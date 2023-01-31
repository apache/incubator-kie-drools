package org.optaplanner.constraint.streams.common.inliner;

import java.util.function.IntConsumer;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;

final class SimpleScoreContext extends ScoreContext<SimpleScore> {

    private final IntConsumer scoreUpdater;

    public SimpleScoreContext(AbstractScoreInliner<SimpleScore> parent, Constraint constraint, SimpleScore constraintWeight,
            IntConsumer scoreUpdater) {
        super(parent, constraint, constraintWeight);
        this.scoreUpdater = scoreUpdater;
    }

    public UndoScoreImpacter changeScoreBy(int matchWeight, JustificationsSupplier justificationsSupplier) {
        int impact = constraintWeight.score() * matchWeight;
        scoreUpdater.accept(impact);
        UndoScoreImpacter undoScoreImpact = () -> scoreUpdater.accept(-impact);
        if (!constraintMatchEnabled) {
            return undoScoreImpact;
        }
        return impactWithConstraintMatch(undoScoreImpact, SimpleScore.of(impact), justificationsSupplier);
    }

}

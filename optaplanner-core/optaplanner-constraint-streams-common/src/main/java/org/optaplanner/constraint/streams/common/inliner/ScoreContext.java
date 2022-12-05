package org.optaplanner.constraint.streams.common.inliner;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;

public abstract class ScoreContext<Score_ extends Score<Score_>> {

    private final AbstractScoreInliner<Score_> parent;
    protected final Constraint constraint;
    protected final Score_ constraintWeight;
    protected final boolean constraintMatchEnabled;

    protected ScoreContext(AbstractScoreInliner<Score_> parent, Constraint constraint, Score_ constraintWeight) {
        this.parent = parent;
        this.constraint = constraint;
        this.constraintWeight = constraintWeight;
        this.constraintMatchEnabled = parent.constraintMatchEnabled;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Score_ getConstraintWeight() {
        return constraintWeight;
    }

    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabled;
    }

    protected UndoScoreImpacter impactWithConstraintMatch(UndoScoreImpacter undoScoreImpact, Score_ score,
            JustificationsSupplier justificationsSupplier) {
        Runnable undoConstraintMatch = parent.addConstraintMatch(constraint, constraintWeight, score, justificationsSupplier);
        return () -> {
            undoScoreImpact.run();
            undoConstraintMatch.run();
        };
    }

}

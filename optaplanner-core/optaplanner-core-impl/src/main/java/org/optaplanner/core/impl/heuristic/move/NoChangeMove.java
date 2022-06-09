package org.optaplanner.core.impl.heuristic.move;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Makes no changes.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class NoChangeMove<Solution_> extends AbstractMove<Solution_> {

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return true;
    }

    @Override
    public NoChangeMove<Solution_> createUndoMove(ScoreDirector<Solution_> scoreDirector) {
        return new NoChangeMove<>();
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        // do nothing
    }

    @Override
    public NoChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new NoChangeMove<>();
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "No change";
    }

}

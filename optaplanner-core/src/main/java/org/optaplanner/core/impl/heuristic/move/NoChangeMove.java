package org.optaplanner.core.impl.heuristic.move;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Makes no changes.
 */
public class NoChangeMove extends AbstractMove {

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new NoChangeMove();
    }

    public void doMove(ScoreDirector scoreDirector) {
        // do nothing
    }

    // ************************************************************************
    // Introspection methods
    // ************************************************************************

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.<Object>emptyList();
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.<Object>emptyList();
    }

    @Override
    public String toString() {
        return "No change";
    }

}

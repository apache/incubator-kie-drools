package org.optaplanner.core.impl.move;

import java.util.Collection;
import java.util.Collections;

import org.optaplanner.core.impl.score.director.ScoreDirector;

/**
 * Makes no changes.
 */
public class NothingMove implements Move {

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return true;
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new NothingMove();
    }

    public void doMove(ScoreDirector scoreDirector) {
        // do nothing
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.<Object>emptyList();
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.<Object>emptyList();
    }

}

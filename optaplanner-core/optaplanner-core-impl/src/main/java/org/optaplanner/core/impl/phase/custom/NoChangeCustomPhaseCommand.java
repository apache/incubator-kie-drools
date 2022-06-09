package org.optaplanner.core.impl.phase.custom;

import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Makes no changes.
 */
public class NoChangeCustomPhaseCommand implements CustomPhaseCommand<Object> {

    @Override
    public void changeWorkingSolution(ScoreDirector<Object> scoreDirector) {
        // Do nothing
    }

}

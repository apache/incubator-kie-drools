package org.optaplanner.core.impl.score.director;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Builds a {@link ScoreDirector}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface ScoreDirectorFactory<Solution_> {

    /**
     * Creates a new {@link ScoreDirector} instance.
     *
     * @return never null
     */
    ScoreDirector<Solution_> buildScoreDirector();

}

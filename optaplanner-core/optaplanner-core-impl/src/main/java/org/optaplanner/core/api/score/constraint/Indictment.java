package org.optaplanner.core.api.score.constraint;

import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;

/**
 * Explains the {@link Score} of a {@link PlanningSolution}, from the opposite side than {@link ConstraintMatchTotal}.
 * Retrievable from {@link ScoreExplanation#getIndictmentMap()}.
 * 
 * @param <Score_> the actual score type
 */
public interface Indictment<Score_ extends Score<Score_>> {

    /**
     * @return never null
     */
    Object getJustification();

    /**
     * @return never null
     */
    Set<ConstraintMatch<Score_>> getConstraintMatchSet();

    /**
     * @return {@code >= 0}
     */
    default int getConstraintMatchCount() {
        return getConstraintMatchSet().size();
    }

    /**
     * Sum of the {@link #getConstraintMatchSet()}'s {@link ConstraintMatch#getScore()}.
     *
     * @return never null
     */
    Score_ getScore();

}

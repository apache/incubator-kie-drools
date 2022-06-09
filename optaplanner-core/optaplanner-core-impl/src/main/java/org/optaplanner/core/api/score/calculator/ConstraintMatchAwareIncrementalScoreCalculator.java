package org.optaplanner.core.api.score.calculator;

import java.util.Collection;
import java.util.Map;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;

/**
 * Allows a {@link IncrementalScoreCalculator} to report {@link ConstraintMatchTotal}s
 * for explaining a score (= which score constraints match for how much)
 * and also for score corruption analysis.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the {@link Score} type
 */
public interface ConstraintMatchAwareIncrementalScoreCalculator<Solution_, Score_ extends Score<Score_>>
        extends IncrementalScoreCalculator<Solution_, Score_> {

    /**
     * Allows for increased performance because it only tracks if constraintMatchEnabled is true.
     * <p>
     * Every implementation should call {@link #resetWorkingSolution}
     * and only handle the constraintMatchEnabled parameter specifically (or ignore it).
     *
     * @param workingSolution never null, to pass to {@link #resetWorkingSolution}.
     * @param constraintMatchEnabled true if {@link #getConstraintMatchTotals()} or {@link #getIndictmentMap()} might be called.
     */
    void resetWorkingSolution(Solution_ workingSolution, boolean constraintMatchEnabled);

    /**
     * @return never null
     * @throws IllegalStateException if {@link #resetWorkingSolution}'s constraintMatchEnabled parameter was false
     * @see ScoreExplanation#getConstraintMatchTotalMap()
     */
    Collection<ConstraintMatchTotal<Score_>> getConstraintMatchTotals();

    /**
     * @return null if it should to be calculated non-incrementally from {@link #getConstraintMatchTotals()}
     * @throws IllegalStateException if {@link #resetWorkingSolution}'s constraintMatchEnabled parameter was false
     * @see ScoreExplanation#getIndictmentMap()
     */
    Map<Object, Indictment<Score_>> getIndictmentMap();

}

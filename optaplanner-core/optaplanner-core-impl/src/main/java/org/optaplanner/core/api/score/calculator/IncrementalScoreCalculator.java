package org.optaplanner.core.api.score.calculator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.score.Score;

/**
 * Used for incremental java {@link Score} calculation.
 * This is much faster than {@link EasyScoreCalculator} but requires much more code to implement too.
 * <p>
 * Any implementation is naturally stateful.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the score type to go with the solution
 */
public interface IncrementalScoreCalculator<Solution_, Score_ extends Score<Score_>> {

    /**
     * There are no {@link #beforeEntityAdded(Object)} and {@link #afterEntityAdded(Object)} calls
     * for entities that are already present in the workingSolution.
     *
     * @param workingSolution never null
     */
    void resetWorkingSolution(Solution_ workingSolution);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void beforeEntityAdded(Object entity);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void afterEntityAdded(Object entity);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     * @param variableName never null, either a genuine or shadow {@link PlanningVariable}
     */
    void beforeVariableChanged(Object entity, String variableName);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     * @param variableName never null, either a genuine or shadow {@link PlanningVariable}
     */
    void afterVariableChanged(Object entity, String variableName);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void beforeEntityRemoved(Object entity);

    /**
     * @param entity never null, an instance of a {@link PlanningEntity} class
     */
    void afterEntityRemoved(Object entity);

    /**
     * This method is only called if the {@link Score} cannot be predicted.
     * The {@link Score} can be predicted for example after an undo move.
     *
     * @return never null
     */
    Score_ calculateScore();

}

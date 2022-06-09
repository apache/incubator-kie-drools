package org.optaplanner.core.api.domain.variable;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * A listener sourced on a basic {@link PlanningVariable}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> @{@link PlanningEntity} on which the source variable is declared
 */
public interface VariableListener<Solution_, Entity_> extends AbstractVariableListener<Solution_, Entity_> {

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void beforeVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity);

    /**
     * @param scoreDirector never null
     * @param entity never null
     */
    void afterVariableChanged(ScoreDirector<Solution_> scoreDirector, Entity_ entity);
}

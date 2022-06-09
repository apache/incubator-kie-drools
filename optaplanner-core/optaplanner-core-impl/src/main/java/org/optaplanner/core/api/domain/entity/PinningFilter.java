package org.optaplanner.core.api.domain.entity;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * Decides on accepting or discarding a {@link PlanningEntity}.
 * A pinned {@link PlanningEntity}'s planning variables are never changed.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Entity_> the entity type, the class with the {@link PlanningEntity} annotation
 */
public interface PinningFilter<Solution_, Entity_> {

    /**
     * @param solution working solution to which the entity belongs
     * @param entity never null, a {@link PlanningEntity}
     * @return true if the entity it is pinned, false if the entity is movable.
     */
    boolean accept(Solution_ solution, Entity_ entity);

}

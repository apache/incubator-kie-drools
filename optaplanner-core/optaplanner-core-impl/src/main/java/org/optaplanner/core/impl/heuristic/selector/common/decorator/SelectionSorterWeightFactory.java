package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;

/**
 * Creates a weight to decide the order of a collections of selections
 * (a selection is a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}).
 * The selections are then sorted by their weight,
 * normally ascending unless it's configured descending.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public interface SelectionSorterWeightFactory<Solution_, T> {

    /**
     * @param solution never null, the {@link PlanningSolution} to which the selection belongs or applies to
     * @param selection never null, a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}
     * @return never null, for example a {@link Integer}, {@link Double} or a more complex {@link Comparable}
     */
    Comparable createSorterWeight(Solution_ solution, T selection);

}

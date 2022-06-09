package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;

/**
 * Create a probabilityWeight for a selection
 * (which is a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}).
 * A probabilityWeight represents the random chance that a selection will be selected.
 * Some use cases benefit from focusing moves more actively on specific selections.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public interface SelectionProbabilityWeightFactory<Solution_, T> {

    /**
     * @param scoreDirector never null, the {@link ScoreDirector}
     *        which has the {@link ScoreDirector#getWorkingSolution()} to which the selection belongs or applies to
     * @param selection never null, a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}
     *        to create the probabilityWeight for
     * @return {@code 0.0 <= returnValue <} {@link Double#POSITIVE_INFINITY}
     */
    double createProbabilityWeight(ScoreDirector<Solution_> scoreDirector, T selection);

}

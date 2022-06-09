package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;

/**
 * Decides on accepting or discarding a selection
 * (which is a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}).
 * For example, a pinned {@link PlanningEntity} is rejected and therefore never used in a {@link Move}.
 * <p>
 * A filtered selection is considered as not selected, it does not count as an unaccepted selection.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public interface SelectionFilter<Solution_, T> {

    /**
     * @param scoreDirector never null, the {@link ScoreDirector}
     *        which has the {@link ScoreDirector#getWorkingSolution()} to which the selection belongs or applies to
     * @param selection never null, a {@link PlanningEntity}, a planningValue, a {@link Move} or a {@link Selector}
     * @return true if the selection is accepted (for example it is movable),
     *         false if the selection will be discarded (for example it is pinned)
     */
    boolean accept(ScoreDirector<Solution_> scoreDirector, T selection);

}

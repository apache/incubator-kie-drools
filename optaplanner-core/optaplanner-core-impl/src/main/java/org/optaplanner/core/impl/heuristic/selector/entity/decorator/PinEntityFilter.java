package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;

/**
 * Filters out entities that return true for the {@link PlanningPin} annotated boolean member.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class PinEntityFilter<Solution_> implements SelectionFilter<Solution_, Object> {

    private final MemberAccessor memberAccessor;

    public PinEntityFilter(MemberAccessor memberAccessor) {
        this.memberAccessor = memberAccessor;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, Object entity) {
        Boolean pinned = (Boolean) memberAccessor.executeGetter(entity);
        if (pinned == null) {
            throw new IllegalStateException("The entity (" + entity + ") has a @" + PlanningPin.class.getSimpleName()
                    + " annotated property (" + memberAccessor.getName() + ") that returns null.");
        }
        return !pinned;
    }

}

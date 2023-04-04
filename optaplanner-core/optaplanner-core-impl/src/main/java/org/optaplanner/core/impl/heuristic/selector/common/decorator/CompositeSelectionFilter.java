package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Arrays;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Combines several {@link SelectionFilter}s into one.
 * Does a logical AND over the accept status of its filters.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
final class CompositeSelectionFilter<Solution_, T> implements SelectionFilter<Solution_, T> {

    static final SelectionFilter NOOP = (scoreDirector, selection) -> true;

    final SelectionFilter<Solution_, T>[] selectionFilterArray;

    CompositeSelectionFilter(SelectionFilter<Solution_, T>[] selectionFilterArray) {
        this.selectionFilterArray = selectionFilterArray;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, T selection) {
        for (SelectionFilter<Solution_, T> selectionFilter : selectionFilterArray) {
            if (!selectionFilter.accept(scoreDirector, selection)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        CompositeSelectionFilter<?, ?> that = (CompositeSelectionFilter<?, ?>) other;
        return Arrays.equals(selectionFilterArray, that.selectionFilterArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(selectionFilterArray);
    }

}

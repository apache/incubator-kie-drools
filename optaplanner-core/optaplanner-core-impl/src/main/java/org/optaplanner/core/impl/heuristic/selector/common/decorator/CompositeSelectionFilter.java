package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;

/**
 * Combines several {@link SelectionFilter}s into one.
 * Does a logical AND over the accept status of its filters.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public final class CompositeSelectionFilter<Solution_, T> implements SelectionFilter<Solution_, T> {

    private final List<SelectionFilter<Solution_, T>> selectionFilterList;

    public CompositeSelectionFilter(List<SelectionFilter<Solution_, T>> selectionFilterList) {
        this.selectionFilterList = selectionFilterList;
    }

    @Override
    public boolean accept(ScoreDirector<Solution_> scoreDirector, T selection) {
        for (SelectionFilter<Solution_, T> selectionFilter : selectionFilterList) {
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
        return Objects.equals(selectionFilterList, that.selectionFilterList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectionFilterList);
    }

}

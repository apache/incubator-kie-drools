package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;

/**
 * Sorts a selection {@link List} based on a {@link Comparator}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public class ComparatorSelectionSorter<Solution_, T> implements SelectionSorter<Solution_, T> {

    private final Comparator<T> appliedComparator;

    public ComparatorSelectionSorter(Comparator<T> comparator) {
        this(comparator, SelectionSorterOrder.ASCENDING);
    }

    public ComparatorSelectionSorter(Comparator<T> comparator, SelectionSorterOrder selectionSorterOrder) {
        switch (selectionSorterOrder) {
            case ASCENDING:
                this.appliedComparator = comparator;
                break;
            case DESCENDING:
                this.appliedComparator = Collections.reverseOrder(comparator);
                break;
            default:
                throw new IllegalStateException("The selectionSorterOrder (" + selectionSorterOrder
                        + ") is not implemented.");
        }
    }

    @Override
    public void sort(ScoreDirector<Solution_> scoreDirector, List<T> selectionList) {
        selectionList.sort(appliedComparator);
    }

}

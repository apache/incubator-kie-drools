package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ComparatorSelectionSorter implements SelectionSorter<Object> {

    private final Comparator<Object> appliedComparator;

    public ComparatorSelectionSorter(Comparator<Object> comparator, SelectionSorterOrder selectionSorterOrder) {
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

    public void sort(ScoreDirector scoreDirector, List<Object> selectionList) {
        Collections.sort(selectionList, appliedComparator);
    }

}

package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

public class WeightFactorySelectionSorter implements SelectionSorter<Object> {

    private final SelectionSorterWeightFactory<Solution, Object> selectionSorterWeightFactory;
    private final Comparator<Object> appliedWeightComparator;

    public WeightFactorySelectionSorter(SelectionSorterWeightFactory<Solution, Object> selectionSorterWeightFactory,
            SelectionSorterOrder selectionSorterOrder) {
        this.selectionSorterWeightFactory = selectionSorterWeightFactory;
        switch (selectionSorterOrder) {
            case ASCENDING:
                this.appliedWeightComparator = new ComparableComparator();
                break;
            case DESCENDING:
                this.appliedWeightComparator = Collections.reverseOrder();
                break;
            default:
                throw new IllegalStateException("The selectionSorterOrder (" + selectionSorterOrder
                        + ") is not implemented.");
        }
    }

    public void sort(ScoreDirector scoreDirector, List<Object> selectionList) {
        Solution solution = scoreDirector.getWorkingSolution();
        SortedMap<Comparable, Object> selectionMap = new TreeMap<Comparable, Object>(appliedWeightComparator);
        for (Object selection : selectionList) {
            Comparable difficultyWeight = selectionSorterWeightFactory.createSorterWeight(solution, selection);
            Object previous = selectionMap.put(difficultyWeight, selection);
            if (previous != null) {
                throw new IllegalStateException("The selectionList contains 2 times the same selection ("
                        + previous + ") and (" + selection + ").");
            }
        }
        selectionList.clear();
        selectionList.addAll(selectionMap.values());
    }

}

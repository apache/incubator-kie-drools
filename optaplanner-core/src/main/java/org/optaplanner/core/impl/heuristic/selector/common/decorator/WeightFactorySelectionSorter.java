package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Ordering;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class WeightFactorySelectionSorter<T> implements SelectionSorter<T> {

    private final SelectionSorterWeightFactory<Solution, T> selectionSorterWeightFactory;
    private final Comparator<Comparable> appliedWeightComparator;

    public WeightFactorySelectionSorter(SelectionSorterWeightFactory<Solution, T> selectionSorterWeightFactory,
            SelectionSorterOrder selectionSorterOrder) {
        this.selectionSorterWeightFactory = selectionSorterWeightFactory;
        switch (selectionSorterOrder) {
            case ASCENDING:
                this.appliedWeightComparator = Ordering.natural();
                break;
            case DESCENDING:
                this.appliedWeightComparator = Collections.reverseOrder();
                break;
            default:
                throw new IllegalStateException("The selectionSorterOrder (" + selectionSorterOrder
                        + ") is not implemented.");
        }
    }

    public void sort(ScoreDirector scoreDirector, List<T> selectionList) {
        Solution solution = scoreDirector.getWorkingSolution();
        SortedMap<Comparable, T> selectionMap = new TreeMap<Comparable, T>(appliedWeightComparator);
        for (T selection : selectionList) {
            Comparable difficultyWeight = selectionSorterWeightFactory.createSorterWeight(solution, selection);
            T previous = selectionMap.put(difficultyWeight, selection);
            if (previous != null) {
                throw new IllegalStateException("The selectionList contains 2 times the same selection ("
                        + previous + ") and (" + selection + ").");
            }
        }
        selectionList.clear();
        selectionList.addAll(selectionMap.values());
    }

}

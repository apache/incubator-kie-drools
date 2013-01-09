package org.drools.planner.core.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;

public class WeightFactorySelectionSorter implements SelectionSorter<Object> {

    private final SelectionSorterWeightFactory<Solution, Object> selectionSorterWeightFactory;

    public WeightFactorySelectionSorter(SelectionSorterWeightFactory<Solution, Object> selectionSorterWeightFactory) {
        this.selectionSorterWeightFactory = selectionSorterWeightFactory;
    }

    public void sort(ScoreDirector scoreDirector, List<Object> selectionList) {
        Solution solution = scoreDirector.getWorkingSolution();
        SortedMap<Comparable, Object> selectionMap = new TreeMap<Comparable, Object>();
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

package org.drools.planner.core.heuristic.selector.common.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.director.ScoreDirector;

public class ComparatorSelectionSorter implements SelectionSorter<Object> {

    private final Comparator<Object> comparator;

    public ComparatorSelectionSorter(Comparator<Object> comparator) {
        this.comparator = comparator;
    }

    public void sort(ScoreDirector scoreDirector, List<Object> selectionList) {
        Collections.sort(selectionList, comparator);
    }

}

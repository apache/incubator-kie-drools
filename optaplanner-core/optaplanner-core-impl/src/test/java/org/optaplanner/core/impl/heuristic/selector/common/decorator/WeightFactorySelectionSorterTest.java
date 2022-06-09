package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import static org.mockito.Mockito.mock;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCodesOfIterator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class WeightFactorySelectionSorterTest {

    @Test
    void sortAscending() {
        SelectionSorterWeightFactory<TestdataSolution, TestdataEntity> weightFactory = (solution, selection) -> Integer
                .valueOf(selection.getCode().charAt(0));
        WeightFactorySelectionSorter<TestdataSolution, TestdataEntity> selectionSorter = new WeightFactorySelectionSorter<>(
                weightFactory, SelectionSorterOrder.ASCENDING);
        ScoreDirector<TestdataSolution> scoreDirector = mock(ScoreDirector.class);
        List<TestdataEntity> selectionList = new ArrayList<>();
        selectionList.add(new TestdataEntity("C"));
        selectionList.add(new TestdataEntity("A"));
        selectionList.add(new TestdataEntity("D"));
        selectionList.add(new TestdataEntity("B"));
        selectionSorter.sort(scoreDirector, selectionList);
        assertCodesOfIterator(selectionList.iterator(), "A", "B", "C", "D");
    }

    @Test
    void sortDescending() {
        SelectionSorterWeightFactory<TestdataSolution, TestdataEntity> weightFactory = (solution, selection) -> Integer
                .valueOf(selection.getCode().charAt(0));
        WeightFactorySelectionSorter<TestdataSolution, TestdataEntity> selectionSorter = new WeightFactorySelectionSorter<>(
                weightFactory, SelectionSorterOrder.DESCENDING);
        ScoreDirector<TestdataSolution> scoreDirector = mock(ScoreDirector.class);
        List<TestdataEntity> selectionList = new ArrayList<>();
        selectionList.add(new TestdataEntity("C"));
        selectionList.add(new TestdataEntity("A"));
        selectionList.add(new TestdataEntity("D"));
        selectionList.add(new TestdataEntity("B"));
        selectionSorter.sort(scoreDirector, selectionList);
        assertCodesOfIterator(selectionList.iterator(), "D", "C", "B", "A");
    }

}

package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ComparatorSelectionSorterTest {

    @Test
    void sort() {
        Integer[] baseArray = new Integer[] { 3, 4, 3, 5, 1 };
        List<Integer> arrayToSort = new ArrayList<>();
        Collections.addAll(arrayToSort, baseArray);
        ComparatorSelectionSorter<TestdataSolution, Integer> selectionSorter = new ComparatorSelectionSorter<>(
                new TestComparator(), SelectionSorterOrder.ASCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertThat(ascendingSort(arrayToSort)).isTrue();

        arrayToSort = new ArrayList<>();
        Collections.addAll(arrayToSort, baseArray);
        selectionSorter = new ComparatorSelectionSorter<>(new TestComparator(), SelectionSorterOrder.DESCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertThat(descendingSort(arrayToSort)).isTrue();
    }

    private boolean ascendingSort(List<Integer> list) {
        Integer tmp = list.get(0);
        for (Integer aList : list) {
            if (tmp <= aList) {
                tmp = aList;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean descendingSort(List<Integer> list) {
        Integer tmp = list.get(0);
        for (Integer aList : list) {
            if (tmp >= aList) {
                tmp = aList;
            } else {
                return false;
            }
        }
        return true;
    }

    private class TestComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer a, Integer b) {
            return a.compareTo(b);
        }

    }

}

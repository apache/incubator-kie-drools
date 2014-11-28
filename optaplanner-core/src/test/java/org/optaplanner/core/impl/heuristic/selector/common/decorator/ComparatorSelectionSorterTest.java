package org.optaplanner.core.impl.heuristic.selector.common.decorator;

import org.junit.Test;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ComparatorSelectionSorterTest {

    @Test
    public void sort() {
        Object [] baseArray = new Object [] {3, 4, 3, 5, 1};
        List<Object> arrayToSort = new ArrayList<Object>();
        Collections.addAll(arrayToSort, baseArray);
        ComparatorSelectionSorter selectionSorter = new ComparatorSelectionSorter(new TestComparator(), SelectionSorterOrder.ASCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertTrue(ascendingSort(arrayToSort));
        
        arrayToSort = new ArrayList<Object>();
        Collections.addAll(arrayToSort, baseArray);
        selectionSorter = new ComparatorSelectionSorter(new TestComparator(), SelectionSorterOrder.DESCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertTrue(descandingSort(arrayToSort));
    }

    private boolean ascendingSort(List<Object> list) {
        Integer tmp = (Integer) list.get(0);
        for (Object aList : list) {
            if (tmp <= (Integer) aList) {
                tmp = (Integer) aList;
            } else return false;
        }
        return true;
    }

    private boolean descandingSort(List<Object> list) {
        Integer tmp = (Integer) list.get(0);
        for (Object aList : list) {
            if (tmp >= (Integer) aList) {
                tmp = (Integer) aList;
            } else return false;
        }
        return true;
    }

    private class TestComparator implements Comparator<Object> {

        @Override
        public int compare(Object o, Object o2) {
            Integer first = (Integer) o;
            Integer second = (Integer) o2;
            if(first.intValue() == second.intValue()) {
                return 0;
            }
            return first > second ? 1 : -1;
        }
    }

}

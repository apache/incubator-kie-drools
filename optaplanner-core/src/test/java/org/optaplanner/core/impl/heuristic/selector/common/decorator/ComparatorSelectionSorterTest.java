/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        Object[] baseArray = new Object[] {3, 4, 3, 5, 1};
        List<Object> arrayToSort = new ArrayList<Object>();
        Collections.addAll(arrayToSort, baseArray);
        ComparatorSelectionSorter selectionSorter = new ComparatorSelectionSorter(new TestComparator(), SelectionSorterOrder.ASCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertTrue(ascendingSort(arrayToSort));

        arrayToSort = new ArrayList<Object>();
        Collections.addAll(arrayToSort, baseArray);
        selectionSorter = new ComparatorSelectionSorter(new TestComparator(), SelectionSorterOrder.DESCENDING);
        selectionSorter.sort(null, arrayToSort);
        assertTrue(descendingSort(arrayToSort));
    }

    private boolean ascendingSort(List<Object> list) {
        Integer tmp = (Integer) list.get(0);
        for (Object aList : list) {
            if (tmp <= (Integer) aList) {
                tmp = (Integer) aList;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean descendingSort(List<Object> list) {
        Integer tmp = (Integer) list.get(0);
        for (Object aList : list) {
            if (tmp >= (Integer) aList) {
                tmp = (Integer) aList;
            } else {
                return false;
            }
        }
        return true;
    }

    private class TestComparator implements Comparator<Object> {

        @Override
        public int compare(Object o, Object o2) {
            Integer first = (Integer) o;
            Integer second = (Integer) o2;
            if (first.intValue() == second.intValue()) {
                return 0;
            }
            return first > second ? 1 : -1;
        }
    }

}

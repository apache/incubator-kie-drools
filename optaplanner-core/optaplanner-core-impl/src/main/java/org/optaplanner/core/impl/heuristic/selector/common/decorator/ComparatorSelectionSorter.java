/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

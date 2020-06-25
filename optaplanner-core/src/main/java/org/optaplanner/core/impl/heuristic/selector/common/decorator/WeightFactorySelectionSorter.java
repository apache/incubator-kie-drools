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
import java.util.SortedMap;
import java.util.TreeMap;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.Selector;

/**
 * Sorts a selection {@link List} based on a {@link SelectionSorterWeightFactory}.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <T> the selection type
 */
public class WeightFactorySelectionSorter<Solution_, T> implements SelectionSorter<Solution_, T> {

    private final SelectionSorterWeightFactory<Solution_, T> selectionSorterWeightFactory;
    private final Comparator<Comparable> appliedWeightComparator;

    public WeightFactorySelectionSorter(SelectionSorterWeightFactory<Solution_, T> selectionSorterWeightFactory,
            SelectionSorterOrder selectionSorterOrder) {
        this.selectionSorterWeightFactory = selectionSorterWeightFactory;
        switch (selectionSorterOrder) {
            case ASCENDING:
                this.appliedWeightComparator = Comparator.naturalOrder();
                break;
            case DESCENDING:
                this.appliedWeightComparator = Collections.reverseOrder();
                break;
            default:
                throw new IllegalStateException("The selectionSorterOrder (" + selectionSorterOrder
                        + ") is not implemented.");
        }
    }

    @Override
    public void sort(ScoreDirector<Solution_> scoreDirector, List<T> selectionList) {
        sort(scoreDirector.getWorkingSolution(), selectionList);
    }

    /**
     * @param solution never null, the {@link PlanningSolution} to which the selections belong or apply to
     * @param selectionList never null, a {@link List}
     *        of {@link PlanningEntity}, planningValue, {@link Move} or {@link Selector}
     */
    public void sort(Solution_ solution, List<T> selectionList) {
        SortedMap<Comparable, T> selectionMap = new TreeMap<>(appliedWeightComparator);
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

/*
 * Copyright 2015 JBoss Inc
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

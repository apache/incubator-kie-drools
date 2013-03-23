/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.solution.Solution;

@Deprecated
public class PlanningEntitySorter {

    // TODO also keep PlanningEntityDescriptor?

    private Comparator<Object> difficultyComparator = null;
    private SelectionSorterWeightFactory difficultyWeightFactory = null;

    public PlanningEntitySorter() {
    }

    public void setDifficultyComparator(Comparator<Object> difficultyComparator) {
        this.difficultyComparator = difficultyComparator;
    }

    public void setDifficultyWeightFactory(SelectionSorterWeightFactory difficultyWeightFactory) {
        this.difficultyWeightFactory = difficultyWeightFactory;
    }

    public boolean isSortDifficultySupported() {
        return difficultyComparator != null || difficultyWeightFactory != null;
    }

    public void sortDifficultyAscending(Solution solution, List<Object> planningEntityList) {
        if (difficultyComparator != null) {
            Collections.sort(planningEntityList, difficultyComparator);
        } else if (difficultyWeightFactory != null) {
            SortedMap<Comparable, Object> planningEntityMap = new TreeMap<Comparable, Object>();
            for (Object planningEntity : planningEntityList) {
                Comparable difficultyWeight = difficultyWeightFactory.createSorterWeight(solution, planningEntity);
                Object previous = planningEntityMap.put(difficultyWeight, planningEntity);
                if (previous != null) {
                    throw new IllegalStateException("The planningValueList has 2 planningEntities ("
                            + previous + ") and (" + planningEntity
                            + ") which result in the same difficultyWeight (" + difficultyWeight + ").");
                }
            }
            planningEntityList.clear();
            planningEntityList.addAll(planningEntityMap.values());
        } else {
            throw new IllegalStateException("Sorting on difficulty is impossible" +
                    " because difficultyComparator and difficultyWeightFactory are null.");
        }
    }

    public void sortDifficultyDescending(Solution solution, List<Object> planningEntityList) {
        sortDifficultyAscending(solution, planningEntityList);
        Collections.reverse(planningEntityList);
    }

}

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

package org.optaplanner.core.impl.domain.variable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.solution.Solution;

@Deprecated
public class PlanningValueSorter {

    // TODO also keep PlanningEntityDescriptor and the valueRange descriptor?

    private Comparator<Object> strengthComparator = null;
    private SelectionSorterWeightFactory strengthWeightFactory = null;

    public PlanningValueSorter() {
    }

    public void setStrengthComparator(Comparator<Object> strengthComparator) {
        this.strengthComparator = strengthComparator;
    }

    public void setStrengthWeightFactory(SelectionSorterWeightFactory strengthWeightFactory) {
        this.strengthWeightFactory = strengthWeightFactory;
    }

    public boolean isSortStrengthSupported() {
        return strengthComparator != null || strengthWeightFactory != null;
    }

    public void sortStrengthAscending(Solution solution, List<Object> planningValueList) {
        if (strengthComparator != null) {
            Collections.sort(planningValueList, strengthComparator);
        } else if (strengthWeightFactory != null) {
            SortedMap<Comparable, Object> planningValueMap = new TreeMap<Comparable, Object>();
            for (Object planningValue : planningValueList) {
                Comparable strengthWeight = strengthWeightFactory.createSorterWeight(solution, planningValue);
                Object previous = planningValueMap.put(strengthWeight, planningValue);
                if (previous != null) {
                    throw new IllegalStateException("The planningValueList has 2 planningValues ("
                            + previous + ") and (" + planningValue
                            + ") which result in the same strengthWeight (" + strengthWeight + ").");
                }
            }
            planningValueList.clear();
            planningValueList.addAll(planningValueMap.values());
        } else {
            throw new IllegalStateException("Sorting on strength is impossible" +
                    " because strengthComparator and strengthWeightFactory are null.");
        }
    }

    public void sortStrengthDescending(Solution solution, List<Object> planningEntityList) {
        sortStrengthAscending(solution, planningEntityList);
        Collections.reverse(planningEntityList);
    }

}

/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector.value.cached;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.value.EntityIgnoringValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueIterator;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.util.RandomUtils;

public class ProbabilityValueSelector extends CachingValueSelector {

    protected final PlanningValueSelectionProbabilityWeightFactory selectionProbabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityValueSelector(SelectionCacheType cacheType,
            PlanningValueSelectionProbabilityWeightFactory selectionProbabilityWeightFactory) {
        super(cacheType);
        this.selectionProbabilityWeightFactory = selectionProbabilityWeightFactory;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedEntityMap = new TreeMap<Double, Object>();
        Solution solution = solverScope.getWorkingSolution();
        double probabilityWeightOffset = 0L;
        for (Object value : childValueSelector) {
            double probabilityWeight = selectionProbabilityWeightFactory.createSelectionProbabilityWeight(
                    solution, value);
            cachedEntityMap.put(probabilityWeightOffset, value);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        probabilityWeightTotal = -1.0;
    }

    @Override
    public boolean isNeverEnding() {
        return true;
    }

    public long getSize() {
        return cachedEntityMap.size();
    }

    @Override
    public String toString() {
        return "Probability(" + childValueSelector + ")";
    }

    public ValueIterator iterator() {
        return new EntityIgnoringValueIterator() {
            public boolean hasNext() {
                return true;
            }

            public Object next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Object> entry = cachedEntityMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }
        };
    }

}

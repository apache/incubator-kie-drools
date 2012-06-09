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

package org.drools.planner.core.heuristic.selector.entity.cached;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.util.RandomUtils;

public class ProbabilityEntitySelector extends CachingEntitySelector {

    protected final PlanningEntitySelectionProbabilityWeightFactory selectionProbabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    protected Random workingRandom = null;

    public ProbabilityEntitySelector(SelectionCacheType cacheType,
            PlanningEntitySelectionProbabilityWeightFactory selectionProbabilityWeightFactory) {
        super(cacheType);
        this.selectionProbabilityWeightFactory = selectionProbabilityWeightFactory;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        workingRandom = solverPhaseScope.getWorkingRandom();
    }

    public void constructCache(DefaultSolverScope solverScope) {
        cachedEntityMap = new TreeMap<Double, Object>();
        Solution solution = solverScope.getWorkingSolution();
        double probabilityWeightOffset = 0L;
        for (Object entity : childEntitySelector) {
            double probabilityWeight = selectionProbabilityWeightFactory.createSelectionProbabilityWeight(
                    solution, entity);
            cachedEntityMap.put(probabilityWeightOffset, entity);
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

    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            public boolean hasNext() {
                return true;
            }

            public Object next() {
                double randomOffset = RandomUtils.nextDouble(workingRandom, probabilityWeightTotal);
                Map.Entry<Double, Object> entry = cachedEntityMap.floorEntry(randomOffset);
                // entry is never null because randomOffset < probabilityWeightTotal
                return entry.getValue();
            }

            public void remove() {
                throw new UnsupportedOperationException("Remove is not supported.");
            }
        };
    }

    @Override
    public String toString() {
        return "Probability(" + childEntitySelector + ")";
    }

}

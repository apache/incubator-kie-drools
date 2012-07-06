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

package org.drools.planner.core.heuristic.selector.value.decorator;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.cached.SelectionProbabilityWeightFactory;
import org.drools.planner.core.heuristic.selector.value.AbstractValueSelector;
import org.drools.planner.core.heuristic.selector.value.EntityIgnoringValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.util.RandomUtils;

public class ProbabilityValueSelector extends AbstractValueSelector implements SelectionCacheLifecycleListener {

    protected final ValueSelector childValueSelector;
    protected final SelectionCacheType cacheType;
    protected final SelectionProbabilityWeightFactory valueProbabilityWeightFactory;

    protected NavigableMap<Double, Object> cachedEntityMap = null;
    protected double probabilityWeightTotal = -1.0;

    public ProbabilityValueSelector(ValueSelector childValueSelector, SelectionCacheType cacheType,
            SelectionProbabilityWeightFactory valueProbabilityWeightFactory) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        this.valueProbabilityWeightFactory = valueProbabilityWeightFactory;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The childValueSelector (" + childValueSelector + ") has neverEnding ("
                    + childValueSelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
        solverPhaseLifecycleSupport.addEventListener(childValueSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedEntityMap = new TreeMap<Double, Object>();
        Solution solution = solverScope.getWorkingSolution();
        double probabilityWeightOffset = 0L;
        for (Object value : childValueSelector) {
            double probabilityWeight = valueProbabilityWeightFactory.createProbabilityWeight(
                    solution, value);
            cachedEntityMap.put(probabilityWeightOffset, value);
            probabilityWeightOffset += probabilityWeight;
        }
        probabilityWeightTotal = probabilityWeightOffset;
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        probabilityWeightTotal = -1.0;
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    public long getSize() {
        return cachedEntityMap.size();
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

    @Override
    public String toString() {
        return "Probability(" + childValueSelector + ")";
    }

}

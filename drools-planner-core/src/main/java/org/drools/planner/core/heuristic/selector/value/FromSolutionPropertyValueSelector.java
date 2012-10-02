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

package org.drools.planner.core.heuristic.selector.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.value.iterator.EntityIgnoringValueIterator;
import org.drools.planner.core.heuristic.selector.value.iterator.IteratorToValueIteratorBridge;
import org.drools.planner.core.heuristic.selector.value.iterator.ValueIterator;
import org.drools.planner.core.solver.scope.DefaultSolverScope;

/**
 * This is the common {@link ValueSelector} implementation.
 */
public class FromSolutionPropertyValueSelector extends AbstractValueSelector implements SelectionCacheLifecycleListener {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final SelectionCacheType cacheType;
    protected final boolean randomSelection;

    protected List<Object> cachedValueList = null;

    public FromSolutionPropertyValueSelector(PlanningVariableDescriptor variableDescriptor,
            SelectionCacheType cacheType, boolean randomSelection) {
        this.variableDescriptor = variableDescriptor;
        this.cacheType = cacheType;
        this.randomSelection = randomSelection;
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        Collection<?> planningValues = variableDescriptor.extractAllPlanningValues(solverScope.getWorkingSolution());
        cachedValueList = new ArrayList<Object>(planningValues.size() + 1);
        cachedValueList.addAll(planningValues);
        if (variableDescriptor.isNullable()) {
            cachedValueList.add(null);
        }
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedValueList = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return variableDescriptor.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || isContinuous();
    }

    public long getSize() {
        return (long) cachedValueList.size();
    }

    public ValueIterator iterator() {
        if (!randomSelection) {
            return new IteratorToValueIteratorBridge(cachedValueList.iterator());
        } else {
            return new EntityIgnoringValueIterator() {
                public boolean hasNext() {
                    return true;
                }

                public Object next() {
                    int index = workingRandom.nextInt(cachedValueList.size());
                    return cachedValueList.get(index);
                }
            };
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}

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

package org.optaplanner.core.impl.heuristic.selector.value;

import java.util.Iterator;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.impl.domain.valuerange.descriptor.EntityIndependentPlanningValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * This is the common {@link ValueSelector} implementation.
 */
public class FromSolutionPropertyValueSelector extends AbstractValueSelector
        implements EntityIndependentValueSelector, SelectionCacheLifecycleListener {

    protected final EntityIndependentPlanningValueRangeDescriptor valueRangeDescriptor;
    protected final SelectionCacheType cacheType;
    protected final boolean randomSelection;

    protected ValueRange<Object> cachedValueRange = null;

    public FromSolutionPropertyValueSelector(EntityIndependentPlanningValueRangeDescriptor valueRangeDescriptor,
            SelectionCacheType cacheType, boolean randomSelection) {
        this.valueRangeDescriptor = valueRangeDescriptor;
        this.cacheType = cacheType;
        this.randomSelection = randomSelection;
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return valueRangeDescriptor.getVariableDescriptor();
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedValueRange = (ValueRange<Object>)
                valueRangeDescriptor.extractValueRange(solverScope.getWorkingSolution());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedValueRange = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isCountable() {
        return valueRangeDescriptor.isCountable();
    }

    public boolean isNeverEnding() {
        return randomSelection || !isCountable();
    }

    public long getSize(Object entity) {
        return getSize();
    }

    public long getSize() {
        return ((CountableValueRange<?>) cachedValueRange).getSize();
    }

    public Iterator<Object> iterator(Object entity) {
        return iterator();
    }

    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return ((CountableValueRange<Object>) cachedValueRange).createOriginalIterator();
        } else {
            return cachedValueRange.createRandomIterator(workingRandom);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getVariableDescriptor().getVariableName() + ")";
    }

}

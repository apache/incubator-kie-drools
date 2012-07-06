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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.decorator.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.value.AbstractValueSelector;
import org.drools.planner.core.heuristic.selector.value.IteratorToValueIteratorBridge;
import org.drools.planner.core.heuristic.selector.value.ValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A {@link ValueSelector} that caches the result of its child {@link ValueSelector}.
 * <p/>
 * Keep this code in sync with {@link CachingMoveSelector}.
 */
public class CachingValueSelector extends AbstractValueSelector implements SelectionCacheLifecycleListener {

    protected final ValueSelector childValueSelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedValueList = null;

    public CachingValueSelector(ValueSelector childValueSelector, SelectionCacheType cacheType) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
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
        long childSize = childValueSelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The valueSelector (" + this + ") has a childValueSelector ("
                    + childValueSelector + ") with childSize (" + childSize
                    + ") which is higher then Integer.MAX_VALUE.");
        }
        cachedValueList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedValueList, childValueSelector.iterator());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedValueList = null;
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
        return cachedValueList.size();
    }

    public ValueIterator iterator() {
        return new IteratorToValueIteratorBridge(cachedValueList.iterator());
    }

    @Override
    public String toString() {
        return "Caching(" + childValueSelector + ")";
    }

}

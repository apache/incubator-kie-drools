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

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.move.cached.CachingMoveSelector;
import org.drools.planner.core.heuristic.selector.value.AbstractValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A {@link ValueSelector} that caches the result of its child {@link ValueSelector}.
 * <p/>
 * Keep this code in sync with {@link CachingMoveSelector}.
 */
public abstract class CachingValueSelector extends AbstractValueSelector implements SelectionCacheLifecycleListener {

    protected final SelectionCacheType cacheType;
    protected ValueSelector childValueSelector;

    public CachingValueSelector(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
        if (cacheType != SelectionCacheType.SOLVER && cacheType != SelectionCacheType.PHASE
                && cacheType != SelectionCacheType.STEP) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public ValueSelector getChildValueSelector() {
        return childValueSelector;
    }

    public void setChildValueSelector(ValueSelector childValueSelector) {
        this.childValueSelector = childValueSelector;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The childValueSelector (" + childValueSelector + ") has neverEnding ("
                    + childValueSelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
        solverPhaseLifecycleSupport.addEventListener(childValueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public String toString() {
        return "Caching(" + childValueSelector + ")";
    }

}

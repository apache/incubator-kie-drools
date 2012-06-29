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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.AbstractEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.cached.CachingMoveSelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A {@link EntitySelector} that caches the result of its child {@link EntitySelector}.
 * <p/>
 * Keep this code in sync with {@link CachingMoveSelector}.
 */
public class CachingEntitySelector extends AbstractEntitySelector implements SelectionCacheLifecycleListener {

    protected final EntitySelector childEntitySelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedEntityList = null;

    public CachingEntitySelector(EntitySelector childEntitySelector, SelectionCacheType cacheType) {
        this.childEntitySelector = childEntitySelector;
        this.cacheType = cacheType;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The childEntitySelector (" + childEntitySelector + ") has neverEnding ("
                    + childEntitySelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
        solverPhaseLifecycleSupport.addEventListener(childEntitySelector);
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
        long childSize = childEntitySelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The entitySelector (" + this + ") has a childEntitySelector ("
                    + childEntitySelector + ") with childSize (" + childSize
                    + ") which is higher then Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedEntityList, childEntitySelector.iterator());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedEntityList = null;
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    public long getSize() {
        return cachedEntityList.size();
    }

    public Iterator<Object> iterator() {
        return cachedEntityList.iterator();
    }

    @Override
    public String toString() {
        return "Caching(" + childEntitySelector + ")";
    }

}

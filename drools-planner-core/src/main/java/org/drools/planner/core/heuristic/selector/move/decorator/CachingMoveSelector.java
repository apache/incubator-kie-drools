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

package org.drools.planner.core.heuristic.selector.move.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.cached.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.drools.planner.core.heuristic.selector.move.AbstractMoveSelector;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A {@link MoveSelector} that caches the result of its child {@link MoveSelector}.
 * <p/>
 * Keep this code in sync with {@link CachingEntitySelector}.
 */
public class CachingMoveSelector extends AbstractMoveSelector implements SelectionCacheLifecycleListener {

    protected final MoveSelector childMoveSelector;
    protected final SelectionCacheType cacheType;

    protected List<Move> cachedMoveList = null;

    public CachingMoveSelector(MoveSelector childMoveSelector, SelectionCacheType cacheType) {
        this.childMoveSelector = childMoveSelector;
        this.cacheType = cacheType;
        if (childMoveSelector.isNeverEnding()) {
            throw new IllegalStateException("The childMoveSelector (" + childMoveSelector + ") has neverEnding ("
                    + childMoveSelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
        solverPhaseLifecycleSupport.addEventListener(childMoveSelector);
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
        long childSize = childMoveSelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The moveSelector (" + this + ") has a childMoveSelector ("
                    + childMoveSelector + ") with childSize (" + childSize
                    + ") which is higher then Integer.MAX_VALUE.");
        }
        cachedMoveList = new ArrayList<Move>((int)childSize);
        CollectionUtils.addAll(cachedMoveList, childMoveSelector.iterator());
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedMoveList = null;
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    public long getSize() {
        return cachedMoveList.size();
    }

    public Iterator<Move> iterator() {
        return cachedMoveList.iterator();
    }

    @Override
    public String toString() {
        return "Caching(" + childMoveSelector + ")";
    }

}

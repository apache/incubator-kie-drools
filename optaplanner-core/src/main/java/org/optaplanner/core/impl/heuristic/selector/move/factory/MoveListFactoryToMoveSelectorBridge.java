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

package org.optaplanner.core.impl.heuristic.selector.move.factory;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

/**
 * Bridges a {@link MoveListFactory} to a {@link MoveSelector}.
 */
public class MoveListFactoryToMoveSelectorBridge extends AbstractMoveSelector
        implements SelectionCacheLifecycleListener {

    protected final MoveListFactory moveListFactory;
    protected final SelectionCacheType cacheType;
    protected final boolean randomSelection;

    protected List<Move> cachedMoveList = null;

    public MoveListFactoryToMoveSelectorBridge(MoveListFactory moveListFactory,
            SelectionCacheType cacheType, boolean randomSelection) {
        this.moveListFactory = moveListFactory;
        this.cacheType = cacheType;
        this.randomSelection = randomSelection;
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    @Override
    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        cachedMoveList = moveListFactory.createMoveList(solverScope.getScoreDirector().getWorkingSolution());
        logger.trace("    Created cachedMoveList with size ({}) in moveSelector({}).",
                cachedMoveList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedMoveList = null;
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        // CachedListRandomIterator is neverEnding
        return randomSelection;
    }

    public long getSize() {
        return (long) cachedMoveList.size();
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return cachedMoveList.iterator();
        } else {
            return new CachedListRandomIterator<Move>(cachedMoveList, workingRandom);
        }
    }

    @Override
    public String toString() {
        return "MoveListFactory(" + moveListFactory.getClass() + ")";
    }

}

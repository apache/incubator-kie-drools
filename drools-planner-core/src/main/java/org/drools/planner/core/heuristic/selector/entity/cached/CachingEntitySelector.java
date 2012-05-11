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
import org.drools.planner.core.heuristic.selector.common.SelectorCacheType;
import org.drools.planner.core.heuristic.selector.entity.AbstractEntitySelector;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.cached.CachingMoveSelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * A {@link EntitySelector} that caches the result of its child {@link EntitySelector}.
 * <p/>
 * Keep this code in sync with {@link CachingMoveSelector}.
 */
public class CachingEntitySelector extends AbstractEntitySelector {

    protected final SelectorCacheType cacheType;
    protected EntitySelector childEntitySelector;

    protected long cachedSize = -1L;
    protected List<Object> cachedEntityList = null;
    protected long cachedRandomProbabilityWeight = -1L;

    public CachingEntitySelector(SelectorCacheType cacheType) {
        this.cacheType = cacheType;
        if (cacheType != SelectorCacheType.SOLVER && cacheType != SelectorCacheType.PHASE
                && cacheType != SelectorCacheType.STEP) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") is not supported on the class (" + getClass().getName() + ").");
        }
    }

    public EntitySelector getChildEntitySelector() {
        return childEntitySelector;
    }

    public void setChildEntitySelector(EntitySelector childEntitySelector) {
        this.childEntitySelector = childEntitySelector;
        if (childEntitySelector.isNeverEnding()) {
            throw new IllegalStateException("The childEntitySelector (" + childEntitySelector + ") has sizeInfinite ("
                    + childEntitySelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        if (cacheType == SelectorCacheType.SOLVER) {
            constructCache(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        if (cacheType == SelectorCacheType.PHASE) {
            constructCache(solverPhaseScope.getSolverScope());
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        if (cacheType == SelectorCacheType.STEP) {
            constructCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        if (cacheType == SelectorCacheType.STEP) {
            disposeCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        if (cacheType == SelectorCacheType.PHASE) {
            disposeCache(solverPhaseScope.getSolverScope());
        }
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        if (cacheType == SelectorCacheType.SOLVER) {
            disposeCache(solverScope);
        }
    }

    protected void constructCache(DefaultSolverScope solverScope) {
        cachedSize = childEntitySelector.getSize();
        if (cachedSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The entitySelector (" + this + ") has a childEntitySelector ("
                    + childEntitySelector + ") with cachedSize (" + cachedSize
                    + ") which is higher then Integer.MAX_VALUE.");
        }
        cachedEntityList = new ArrayList<Object>((int)cachedSize);
        CollectionUtils.addAll(cachedEntityList, childEntitySelector.iterator());
        cachedRandomProbabilityWeight = childEntitySelector.getRandomProbabilityWeight();
        orderCache(solverScope);
    }

    protected void orderCache(DefaultSolverScope solverScope) {
        // Hook method
    }

    protected void disposeCache(DefaultSolverScope solverScope) {
        cachedSize = -1L;
        cachedEntityList = null;
        cachedRandomProbabilityWeight = -1L;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Iterator<Object> iterator() {
        return cachedEntityList.iterator();
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    public long getSize() {
        return cachedSize;
    }

    public long getRandomProbabilityWeight() {
        return cachedRandomProbabilityWeight;
    }

    @Override
    public String toString() {
        return "Caching(" + childEntitySelector + ")";
    }

}

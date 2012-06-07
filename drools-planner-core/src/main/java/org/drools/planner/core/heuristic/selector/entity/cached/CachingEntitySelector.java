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
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
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
public abstract class CachingEntitySelector extends AbstractEntitySelector {

    protected final SelectionCacheType cacheType;
    protected EntitySelector childEntitySelector;

    public CachingEntitySelector(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
        if (cacheType != SelectionCacheType.SOLVER && cacheType != SelectionCacheType.PHASE
                && cacheType != SelectionCacheType.STEP) {
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
            throw new IllegalStateException("The childEntitySelector (" + childEntitySelector + ") has neverEnding ("
                    + childEntitySelector.isNeverEnding() + ") on a class (" + getClass().getName() + ") instance.");
        }
    }

    // ************************************************************************
    // Cache lifecycle methods
    // ************************************************************************

    @Override
    public void solvingStarted(DefaultSolverScope solverScope) {
        super.solvingStarted(solverScope);
        if (cacheType == SelectionCacheType.SOLVER) {
            constructCache(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        if (cacheType == SelectionCacheType.PHASE) {
            constructCache(solverPhaseScope.getSolverScope());
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        super.stepStarted(stepScope);
        if (cacheType == SelectionCacheType.STEP) {
            constructCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        super.stepEnded(stepScope);
        if (cacheType == SelectionCacheType.STEP) {
            disposeCache(stepScope.getSolverPhaseScope().getSolverScope());
        }
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        if (cacheType == SelectionCacheType.PHASE) {
            disposeCache(solverPhaseScope.getSolverScope());
        }
    }

    @Override
    public void solvingEnded(DefaultSolverScope solverScope) {
        super.solvingEnded(solverScope);
        if (cacheType == SelectionCacheType.SOLVER) {
            disposeCache(solverScope);
        }
    }

    protected abstract void constructCache(DefaultSolverScope solverScope);

    protected abstract void disposeCache(DefaultSolverScope solverScope);

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getEntityDescriptor() {
        return childEntitySelector.getEntityDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return false;
    }

    @Override
    public String toString() {
        return "Caching(" + childEntitySelector + ")";
    }

}

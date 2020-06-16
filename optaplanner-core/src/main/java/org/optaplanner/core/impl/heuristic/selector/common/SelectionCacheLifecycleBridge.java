/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.selector.common;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SelectionCacheLifecycleBridge implements PhaseLifecycleListener {

    protected final SelectionCacheType cacheType;
    protected final SelectionCacheLifecycleListener selectionCacheLifecycleListener;

    public SelectionCacheLifecycleBridge(SelectionCacheType cacheType,
            SelectionCacheLifecycleListener selectionCacheLifecycleListener) {
        this.cacheType = cacheType;
        this.selectionCacheLifecycleListener = selectionCacheLifecycleListener;
        if (cacheType == null) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") for selectionCacheLifecycleListener (" + selectionCacheLifecycleListener
                    + ") should have already been resolved.");
        }
    }

    @Override
    public void solvingStarted(SolverScope solverScope) {
        if (cacheType == SelectionCacheType.SOLVER) {
            selectionCacheLifecycleListener.constructCache(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractPhaseScope phaseScope) {
        if (cacheType == SelectionCacheType.PHASE) {
            selectionCacheLifecycleListener.constructCache(phaseScope.getSolverScope());
        }
    }

    @Override
    public void stepStarted(AbstractStepScope stepScope) {
        if (cacheType == SelectionCacheType.STEP) {
            selectionCacheLifecycleListener.constructCache(stepScope.getPhaseScope().getSolverScope());
        }
    }

    @Override
    public void stepEnded(AbstractStepScope stepScope) {
        if (cacheType == SelectionCacheType.STEP) {
            selectionCacheLifecycleListener.disposeCache(stepScope.getPhaseScope().getSolverScope());
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope phaseScope) {
        if (cacheType == SelectionCacheType.PHASE) {
            selectionCacheLifecycleListener.disposeCache(phaseScope.getSolverScope());
        }
    }

    @Override
    public void solvingEnded(SolverScope solverScope) {
        if (cacheType == SelectionCacheType.SOLVER) {
            selectionCacheLifecycleListener.disposeCache(solverScope);
        }
    }

    @Override
    public String toString() {
        return "Bridge(" + selectionCacheLifecycleListener + ")";
    }
}

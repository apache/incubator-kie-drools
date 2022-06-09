package org.optaplanner.core.impl.heuristic.selector.common;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class SelectionCacheLifecycleBridge<Solution_> implements PhaseLifecycleListener<Solution_> {

    protected final SelectionCacheType cacheType;
    protected final SelectionCacheLifecycleListener<Solution_> selectionCacheLifecycleListener;

    public SelectionCacheLifecycleBridge(SelectionCacheType cacheType,
            SelectionCacheLifecycleListener<Solution_> selectionCacheLifecycleListener) {
        this.cacheType = cacheType;
        this.selectionCacheLifecycleListener = selectionCacheLifecycleListener;
        if (cacheType == null) {
            throw new IllegalArgumentException("The cacheType (" + cacheType
                    + ") for selectionCacheLifecycleListener (" + selectionCacheLifecycleListener
                    + ") should have already been resolved.");
        }
    }

    @Override
    public void solvingStarted(SolverScope<Solution_> solverScope) {
        if (cacheType == SelectionCacheType.SOLVER) {
            selectionCacheLifecycleListener.constructCache(solverScope);
        }
    }

    @Override
    public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
        if (cacheType == SelectionCacheType.PHASE) {
            selectionCacheLifecycleListener.constructCache(phaseScope.getSolverScope());
        }
    }

    @Override
    public void stepStarted(AbstractStepScope<Solution_> stepScope) {
        if (cacheType == SelectionCacheType.STEP) {
            selectionCacheLifecycleListener.constructCache(stepScope.getPhaseScope().getSolverScope());
        }
    }

    @Override
    public void stepEnded(AbstractStepScope<Solution_> stepScope) {
        if (cacheType == SelectionCacheType.STEP) {
            selectionCacheLifecycleListener.disposeCache(stepScope.getPhaseScope().getSolverScope());
        }
    }

    @Override
    public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
        if (cacheType == SelectionCacheType.PHASE) {
            selectionCacheLifecycleListener.disposeCache(phaseScope.getSolverScope());
        }
    }

    @Override
    public void solvingEnded(SolverScope<Solution_> solverScope) {
        if (cacheType == SelectionCacheType.SOLVER) {
            selectionCacheLifecycleListener.disposeCache(solverScope);
        }
    }

    @Override
    public String toString() {
        return "Bridge(" + selectionCacheLifecycleListener + ")";
    }
}

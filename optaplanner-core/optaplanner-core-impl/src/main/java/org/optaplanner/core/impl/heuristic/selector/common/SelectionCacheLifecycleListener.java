package org.optaplanner.core.impl.heuristic.selector.common;

import org.optaplanner.core.impl.solver.scope.SolverScope;

public interface SelectionCacheLifecycleListener<Solution_> {

    void constructCache(SolverScope<Solution_> solverScope);

    void disposeCache(SolverScope<Solution_> solverScope);

}

package org.optaplanner.core.impl.statistic;

import org.optaplanner.core.api.solver.Solver;

public interface SolverStatistic<Solution_> {
    void unregister(Solver<Solution_> solver);

    void register(Solver<Solution_> solver);
}

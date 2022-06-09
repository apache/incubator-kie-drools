package org.optaplanner.core.impl.statistic;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.solver.DefaultSolver;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

public class MemoryUseStatistic<Solution_> implements SolverStatistic<Solution_> {

    @Override
    public void unregister(Solver<Solution_> solver) {
        // Intentionally Empty: JVM memory is not bound to a particular solver
    }

    @Override
    public void register(Solver<Solution_> solver) {
        DefaultSolver<Solution_> defaultSolver = (DefaultSolver<Solution_>) solver;
        new JvmMemoryMetrics(defaultSolver.getSolverScope().getMonitoringTags()).bindTo(Metrics.globalRegistry);
    }
}

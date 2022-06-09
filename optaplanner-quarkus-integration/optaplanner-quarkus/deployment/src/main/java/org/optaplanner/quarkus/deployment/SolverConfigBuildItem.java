package org.optaplanner.quarkus.deployment;

import org.optaplanner.core.config.solver.SolverConfig;

import io.quarkus.builder.item.SimpleBuildItem;

public final class SolverConfigBuildItem extends SimpleBuildItem {
    SolverConfig solverConfig;

    public SolverConfigBuildItem(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }
}

package org.optaplanner.quarkus.config;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "optaplanner", phase = ConfigPhase.RUN_TIME)
public class OptaPlannerRuntimeConfig {
    /**
     * During run time, this is translated into OptaPlanner's {@link SolverConfig}
     * runtime properties.
     */
    @ConfigItem
    public SolverRuntimeConfig solver;

    /**
     * Configuration properties that overwrite OptaPlanner's {@link SolverManagerConfig}.
     */
    @ConfigItem
    public SolverManagerRuntimeConfig solverManager;
}

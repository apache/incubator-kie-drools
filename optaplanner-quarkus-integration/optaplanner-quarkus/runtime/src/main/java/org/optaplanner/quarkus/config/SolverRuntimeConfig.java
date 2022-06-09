package org.optaplanner.quarkus.config;

import java.util.Optional;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * During run time, this overrides some of OptaPlanner's {@link SolverConfig}
 * properties.
 */
@ConfigGroup
public class SolverRuntimeConfig {
    /**
     * Enable multithreaded solving for a single problem, which increases CPU consumption.
     * Defaults to {@value SolverConfig#MOVE_THREAD_COUNT_NONE}.
     * Other options include {@value SolverConfig#MOVE_THREAD_COUNT_AUTO}, a number
     * or formula based on the available processor count.
     */
    @ConfigItem
    public Optional<String> moveThreadCount;

    /**
     * Configuration properties that overwrite OptaPlanner's {@link TerminationConfig}.
     */
    @ConfigItem
    public TerminationRuntimeConfig termination;
}

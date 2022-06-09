package org.optaplanner.quarkus.deployment.config;

import java.util.Optional;

import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.config.solver.SolverConfig;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * During build time, this is translated into OptaPlanner's Config classes.
 */
@ConfigRoot(name = "optaplanner")
public class OptaPlannerBuildTimeConfig {

    public static final String DEFAULT_SOLVER_CONFIG_URL = "solverConfig.xml";
    public static final String DEFAULT_CONSTRAINTS_DRL_URL = "constraints.drl";
    public static final String CONSTRAINTS_DRL_PROPERTY = "quarkus.optaplanner.score-drl";

    /**
     * A classpath resource to read the solver configuration XML.
     * Defaults to {@value DEFAULT_SOLVER_CONFIG_URL}.
     * If this property isn't specified, that solverConfig.xml is optional.
     */
    @ConfigItem
    public Optional<String> solverConfigXml;

    /**
     * A classpath resource to read the solver score DRL.
     * Defaults to "{@link #DEFAULT_CONSTRAINTS_DRL_URL}".
     * Do not define this property when a {@link ConstraintProvider}, {@link EasyScoreCalculator} or
     * {@link IncrementalScoreCalculator} class exists.
     */
    @ConfigItem
    public Optional<String> scoreDrl;

    /**
     * Configuration properties that overwrite OptaPlanner's {@link SolverConfig}.
     */
    @ConfigItem
    public SolverBuildTimeConfig solver;

}

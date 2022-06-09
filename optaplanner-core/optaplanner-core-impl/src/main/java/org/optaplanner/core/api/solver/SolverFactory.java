package org.optaplanner.core.api.solver;

import java.io.File;
import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;

/**
 * Creates {@link Solver} instances.
 * Most applications only need one SolverFactory.
 * <p>
 * To create a SolverFactory, use {@link #createFromXmlResource(String)}.
 * To change the configuration programmatically, create a {@link SolverConfig} first
 * and then use {@link #create(SolverConfig)}.
 * <p>
 * These methods are thread-safe unless explicitly stated otherwise.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public interface SolverFactory<Solution_> {

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    /**
     * Reads an XML solver configuration from the classpath
     * and uses that {@link SolverConfig} to build a {@link SolverFactory}.
     * The XML root element must be {@code <solver>}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @return never null, subsequent changes to the config have no effect on the returned instance
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null, subsequent changes to the config have no effect on the returned instance
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource,
            ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * Reads an XML solver configuration from the file system
     * and uses that {@link SolverConfig} to build a {@link SolverFactory}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     *
     * @param solverConfigFile never null
     * @return never null, subsequent changes to the config have no effect on the returned instance
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile) {
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(solverConfigFile);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     *
     * @param solverConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null, subsequent changes to the config have no effect on the returned instance
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(solverConfigFile, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    // ************************************************************************
    // Static creation methods: SolverConfig
    // ************************************************************************

    /**
     * Uses a {@link SolverConfig} to build a {@link SolverFactory}.
     * If you don't need to manipulate the {@link SolverConfig} programmatically,
     * use {@link #createFromXmlResource(String)} instead.
     *
     * @param solverConfig never null
     * @return never null, subsequent changes to the config have no effect on the returned instance
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    static <Solution_> SolverFactory<Solution_> create(SolverConfig solverConfig) {
        Objects.requireNonNull(solverConfig);
        // Defensive copy of solverConfig, because the DefaultSolverFactory doesn't internalize it yet
        solverConfig = new SolverConfig(solverConfig);
        return new DefaultSolverFactory<>(solverConfig);
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Creates a new {@link Solver} instance.
     *
     * @return never null
     */
    Solver<Solution_> buildSolver();

}

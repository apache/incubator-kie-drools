package org.optaplanner.benchmark.api;

import java.io.File;
import java.util.List;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmarkFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * Builds {@link PlannerBenchmark} instances.
 * <p>
 * Supports tweaking the configuration programmatically before a {@link PlannerBenchmark} instance is build.
 */
public abstract class PlannerBenchmarkFactory {

    // ************************************************************************
    // Static creation methods: SolverConfig
    // ************************************************************************

    /**
     * Reads an XML solver configuration from the classpath
     * and uses that {@link SolverConfig} to build a {@link PlannerBenchmarkConfig}
     * that in turn is used to build a {@link PlannerBenchmarkFactory}.
     * The XML root element must be {@code <solver>}.
     * <p>
     * To read an XML benchmark configuration instead, use {@link #createFromXmlResource(String)}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}.
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @param benchmarkDirectory never null
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            File benchmarkDirectory) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig,
                benchmarkDirectory);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     *
     * @param solverConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @param benchmarkDirectory never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            File benchmarkDirectory, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig,
                benchmarkDirectory);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    /**
     * Reads an XML benchmark configuration from the classpath
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * The XML root element must be {@code <plannerBenchmark>}.
     * <p>
     * To read an XML solver configuration instead, use {@link #createFromSolverConfigXmlResource(String)}.
     *
     * @param benchmarkConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     *
     * @param benchmarkConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource,
                classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * Reads an XML benchmark configuration from the file system
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     *
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlFile(benchmarkConfigFile);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     *
     * @param benchmarkConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlFile(benchmarkConfigFile, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: Freemarker XML
    // ************************************************************************

    /**
     * Reads an Freemarker template from the classpath that generates an XML benchmark configuration
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * The generated XML root element must be {@code <plannerBenchmark>}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     * @see #createFromFreemarkerXmlResource(String)
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource,
                classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource,
                model);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model,
            ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource, model,
                classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * Reads an Freemarker template rom the file system that generates an XML benchmark configuration
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * The generated XML root element must be {@code <plannerBenchmark>}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromFreemarkerXmlResource(String)} instead.
     *
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile, model);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model,
            ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile, model,
                classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: PlannerBenchmarkConfig
    // ************************************************************************

    /**
     * Uses a {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * If you don't need to manipulate the {@link PlannerBenchmarkConfig} programmatically,
     * use {@link #createFromXmlResource(String)} instead.
     *
     * @param benchmarkConfig never null
     * @return never null
     */
    public static PlannerBenchmarkFactory create(PlannerBenchmarkConfig benchmarkConfig) {
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Creates a new {@link PlannerBenchmark} instance.
     *
     * @return never null
     */
    public abstract PlannerBenchmark buildPlannerBenchmark();

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     *
     * @param problemList never null, can be empty
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @return never null
     */
    public <Solution_> PlannerBenchmark buildPlannerBenchmark(List<Solution_> problemList) {
        return buildPlannerBenchmark(problemList.toArray());
    }

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     *
     * @param problems never null, can be none
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @return never null
     */
    public abstract <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems);

}

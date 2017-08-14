/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.api;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.impl.EmptyPlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.FreemarkerXmlPlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.XStreamXmlPlannerBenchmarkFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.AbstractSolverFactory;

/**
 * Builds {@link PlannerBenchmark} instances.
 * <p>
 * Supports tweaking the configuration programmatically before a {@link PlannerBenchmark} instance is build.
 */
public abstract class PlannerBenchmarkFactory {

    // ************************************************************************
    // Static creation methods
    // ************************************************************************

    /**
     * @param solverFactory never null, also its {@link ClassLoader} is reused if any was configured during creation
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> PlannerBenchmarkFactory createFromSolverFactory(SolverFactory<Solution_> solverFactory) {
        return createFromSolverFactory(solverFactory, new File("local/benchmarkReport"));
    }

    /**
     * @param solverFactory never null, also its {@link ClassLoader} is reused if any was configured during creation
     * @param benchmarkDirectory never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> PlannerBenchmarkFactory createFromSolverFactory(SolverFactory<Solution_> solverFactory,
            File benchmarkDirectory) {
        SolverConfigContext solverConfigContext = ((AbstractSolverFactory) solverFactory).getSolverConfigContext();
        PlannerBenchmarkFactory plannerBenchmarkFactory = (solverConfigContext == null)
                ? new EmptyPlannerBenchmarkFactory() : new EmptyPlannerBenchmarkFactory(solverConfigContext);
        PlannerBenchmarkConfig plannerBenchmarkConfig = plannerBenchmarkFactory.getPlannerBenchmarkConfig();
        plannerBenchmarkConfig.setBenchmarkDirectory(benchmarkDirectory);
        SolverBenchmarkConfig solverBenchmarkConfig = new SolverBenchmarkConfig();
        SolverConfig solverConfig = new SolverConfig(solverFactory.getSolverConfig());
        solverBenchmarkConfig.setSolverConfig(solverConfig);
        plannerBenchmarkConfig.setInheritedSolverBenchmarkConfig(solverBenchmarkConfig);
        plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        return plannerBenchmarkFactory;
    }

    /**
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource) {
        return new XStreamXmlPlannerBenchmarkFactory()
                .configure(benchmarkConfigResource);
    }

    /**
     * See {@link #createFromXmlResource(String)}.
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource, ClassLoader classLoader) {
        return new XStreamXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(benchmarkConfigResource);
    }

    /**
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile) {
        return new XStreamXmlPlannerBenchmarkFactory()
                .configure(benchmarkConfigFile);
    }

    /**
     * @param benchmarkConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile, ClassLoader classLoader) {
        return new XStreamXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(benchmarkConfigFile);
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlInputStream(InputStream in) {
        return new XStreamXmlPlannerBenchmarkFactory()
                .configure(in);
    }

    /**
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        return new XStreamXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(in);
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlReader(Reader reader) {
        return new XStreamXmlPlannerBenchmarkFactory()
                .configure(reader);
    }

    /**
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlReader(Reader reader, ClassLoader classLoader) {
        return new XStreamXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(reader);
    }

    // ************************************************************************
    // Static creation methods with Freemarker
    // ************************************************************************

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource) {
        return createFromFreemarkerXmlResource(templateResource, null);
    }

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, ClassLoader classLoader) {
        return createFromFreemarkerXmlResource(templateResource, null, classLoader);
    }

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model) {
        return new FreemarkerXmlPlannerBenchmarkFactory()
                .configure(templateResource, model);
    }

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model, ClassLoader classLoader) {
        return new FreemarkerXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(templateResource, model);
    }

    /**
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile) {
        return createFromFreemarkerXmlFile(templateFile, null);
    }

    /**
     * @param templateFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, ClassLoader classLoader) {
        return createFromFreemarkerXmlFile(templateFile, null, classLoader);
    }

    /**
     * @param templateFile never null
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model) {
        return new FreemarkerXmlPlannerBenchmarkFactory()
                .configure(templateFile, model);
    }

    /**
     * @param templateFile never null
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model, ClassLoader classLoader) {
        return new FreemarkerXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(templateFile, model);
    }

    /**
     * @param templateIn never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn) {
        return createFromFreemarkerXmlInputStream(templateIn, null);
    }

    /**
     * @param templateIn never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, ClassLoader classLoader) {
        return createFromFreemarkerXmlInputStream(templateIn, null, classLoader);
    }

    /**
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, Object model) {
        return new FreemarkerXmlPlannerBenchmarkFactory()
                .configure(templateIn, model);
    }

    /**
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, Object model, ClassLoader classLoader) {
        return new FreemarkerXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(templateIn, model);
    }

    /**
     * @param templateReader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader) {
        return createFromFreemarkerXmlReader(templateReader, null);
    }

    /**
     * @param templateReader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, ClassLoader classLoader) {
        return createFromFreemarkerXmlReader(templateReader, null, classLoader);
    }

    /**
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, Object model) {
        return new FreemarkerXmlPlannerBenchmarkFactory()
                .configure(templateReader, model);
    }

    /**
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, Object model, ClassLoader classLoader) {
        return new FreemarkerXmlPlannerBenchmarkFactory(new SolverConfigContext(classLoader))
                .configure(templateReader, model);
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Allows you to programmatically change the {@link PlannerBenchmarkConfig} at runtime before building
     * the {@link PlannerBenchmark}.
     * <p>
     * This method is not thread-safe.
     * @return never null
     */
    public abstract PlannerBenchmarkConfig getPlannerBenchmarkConfig();

    /**
     * Creates a new {@link PlannerBenchmark} instance.
     * @return never null
     */
    public abstract PlannerBenchmark buildPlannerBenchmark();

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     * @param problems never null, can be none
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public abstract <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems);

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     * @param problemList never null, can be empty
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public abstract <Solution_> PlannerBenchmark buildPlannerBenchmark(List<Solution_> problemList);

}

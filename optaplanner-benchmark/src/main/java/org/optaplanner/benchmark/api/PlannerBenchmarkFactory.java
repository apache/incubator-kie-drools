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
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmarkFactory;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;

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
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}.
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param benchmarkDirectory never null
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            File benchmarkDirectory) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig, benchmarkDirectory);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromSolverConfigXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param benchmarkDirectory never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     */
    public static PlannerBenchmarkFactory createFromSolverConfigXmlResource(String solverConfigResource,
            File benchmarkDirectory, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig, benchmarkDirectory);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param solverFactory never null, also its {@link ClassLoader} is reused if any was configured during creation
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link #createFromSolverConfigXmlResource(String)}
     * or in complex cases {@link PlannerBenchmarkConfig#createFromSolverConfig(SolverConfig)}
     * and {@link #create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> PlannerBenchmarkFactory createFromSolverFactory(SolverFactory<Solution_> solverFactory) {
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        SolverConfigContext solverConfigContext = ((DefaultSolverFactory) solverFactory).getSolverConfigContext();
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig, solverConfigContext);
    }

    /**
     * @param solverFactory never null, also its {@link ClassLoader} is reused if any was configured during creation
     * @param benchmarkDirectory never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link #createFromSolverConfigXmlResource(String, File)}.
     * or in complex cases {@link PlannerBenchmarkConfig#createFromSolverConfig(SolverConfig, File)}
     * and {@link #create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> PlannerBenchmarkFactory createFromSolverFactory(SolverFactory<Solution_> solverFactory,
            File benchmarkDirectory) {
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig, benchmarkDirectory);
        SolverConfigContext solverConfigContext = ((DefaultSolverFactory) solverFactory).getSolverConfigContext();
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig, solverConfigContext);
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
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * Reads an XML benchmark configuration from the file system
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlFile(benchmarkConfigFile);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     * @param benchmarkConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlFile(benchmarkConfigFile, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param in never null, gets closed
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromXmlInputStream(InputStream)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromXmlInputStream(InputStream in) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlInputStream(in);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromXmlInputStream(InputStream, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlInputStream(in, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromXmlReader(Reader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromXmlReader(Reader reader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlReader(reader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromXmlReader(Reader, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromXmlReader(Reader reader, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlReader(reader, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: Freemarker XML
    // ************************************************************************

    /**
     * Reads an Freemarker template from the classpath that generates an XML benchmark configuration
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * The generated XML root element must be {@code <plannerBenchmark>}.
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
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource, model);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlResource(templateResource, model, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * Reads an Freemarker template rom the file system that generates an XML benchmark configuration
     * and uses that {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * The generated XML root element must be {@code <plannerBenchmark>}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromFreemarkerXmlResource(String)} instead.
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     * @param templateFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
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
     * @param templateFile never null
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlFile(templateFile, model, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateIn never null, gets closed
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlInputStream(InputStream)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlInputStream(templateIn);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateIn never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlInputStream(InputStream, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlInputStream(templateIn, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlInputStream(InputStream, Object)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, Object model) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlInputStream(templateIn, model);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlInputStream(InputStream, Object, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, Object model, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlInputStream(templateIn, model, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateReader never null, gets closed
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlReader(Reader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlReader(templateReader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateReader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlReader(Reader, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlReader(templateReader, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlReader(Reader, Object)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, Object model) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlReader(templateReader, model);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    /**
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig#createFromFreemarkerXmlReader(Reader, Object, ClassLoader)}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, Object model, ClassLoader classLoader) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromFreemarkerXmlReader(templateReader, model, classLoader);
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: PlannerBenchmarkConfig
    // ************************************************************************

    /**
     * Uses a {@link PlannerBenchmarkConfig} to build a {@link PlannerBenchmarkFactory}.
     * If you don't need to manipulate the {@link PlannerBenchmarkConfig} programmatically,
     * use {@link #createFromXmlResource(String)} instead.
     * @param benchmarkConfig never null
     * @return never null
     */
    public static PlannerBenchmarkFactory create(PlannerBenchmarkConfig benchmarkConfig) {
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig);
    }

    // ************************************************************************
    // Static creation methods: KieContainer
    // ************************************************************************

    // TODO Deprecate KieContainer methods in favor of Quarkus, Kogito and Spring Boot

    /**
     * Creates a new {@link PlannerBenchmarkFactory} that uses {@link KieServices#getKieClasspathContainer()}.
     * @param benchmarkConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromKieContainerXmlResource(String benchmarkConfigResource) {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        return createFromKieContainerXmlResource(kieContainer, benchmarkConfigResource);
    }

    /**
     * Creates a new {@link PlannerBenchmarkFactory} that uses a {@link KieModule} represented by its releaseId.
     * @param releaseId never null
     * @param benchmarkConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromKieContainerXmlResource(ReleaseId releaseId,
            String benchmarkConfigResource) {
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        return createFromKieContainerXmlResource(kieContainer, benchmarkConfigResource);
    }

    /**
     * Creates a new {@link PlannerBenchmarkFactory} that uses a {@link KieModule} wrapped by a {@link KieContainer}.
     * @param kieContainer never null
     * @param benchmarkConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromKieContainerXmlResource(KieContainer kieContainer,
            String benchmarkConfigResource) {
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(benchmarkConfigResource,
                kieContainer.getClassLoader());
        return new DefaultPlannerBenchmarkFactory(benchmarkConfig, new SolverConfigContext(kieContainer));
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Creates a new {@link PlannerBenchmark} instance.
     * @return never null
     */
    public abstract PlannerBenchmark buildPlannerBenchmark();

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     * @param problemList never null, can be empty
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @return never null
     */
    public <Solution_> PlannerBenchmark buildPlannerBenchmark(List<Solution_> problemList) {
        return buildPlannerBenchmark(problemList.toArray());
    }

    /**
     * Creates a new {@link PlannerBenchmark} instance for datasets that are already in memory.
     * @param problems never null, can be none
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @return never null
     */
    public abstract <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems);

    /**
     * Deprecated. To configure a {@link PlannerBenchmarkFactory} dynamically (without parsing XML each time),
     * use {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)} instead.
     * <p>
     * This method is not thread-safe.
     * @return never null
     * @deprecated in favor of {@link PlannerBenchmarkConfig()}
     * and {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public abstract PlannerBenchmarkConfig getPlannerBenchmarkConfig();

}

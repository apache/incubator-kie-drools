/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;

/**
 * Creates {@link Solver} instances.
 * Most applications only need one SolverFactory.
 * <p>
 * To build an instance, use {@link #createFromXmlResource(String)}.
 * To change the configuration programmatically, create a {@link SolverConfig}
 * and use {@link #create(SolverConfig)}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolverFactory<Solution_> {

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    /**
     * Reads an XML solver configuration from the classpath
     * and uses that {@link SolverConfig} to build a {@link SolverFactory}.
     * The XML root element must be {@code <solver>}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * Reads an XML solver configuration from the file system
     * and uses that {@link SolverConfig} to build a {@link SolverFactory}.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     * @param solverConfigFile never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile) {
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(solverConfigFile);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     * @param solverConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(solverConfigFile, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * @param in never null, gets closed
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig#createFromXmlInputStream(InputStream)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createFromXmlInputStream(InputStream in) {
        SolverConfig solverConfig = SolverConfig.createFromXmlInputStream(in);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig#createFromXmlInputStream(InputStream, ClassLoader)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlInputStream(in, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig#createFromXmlReader(Reader)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createFromXmlReader(Reader reader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlReader(reader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    /**
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig#createFromXmlReader(Reader, ClassLoader)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createFromXmlReader(Reader reader, ClassLoader classLoader) {
        SolverConfig solverConfig = SolverConfig.createFromXmlReader(reader, classLoader);
        return new DefaultSolverFactory<>(solverConfig);
    }

    // ************************************************************************
    // Static creation methods: SolverConfig
    // ************************************************************************

    /**
     * Uses a {@link SolverConfig} to build a {@link SolverFactory}.
     * If you don't need to manipulate the {@link SolverConfig} programmatically,
     * use {@link #createFromXmlResource(String)} instead.
     * @param solverConfig never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> create(SolverConfig solverConfig) {
        // Defensive copy of solverConfig, because the DefaultSolverFactory doesn't internalize it yet
        solverConfig = new SolverConfig(solverConfig);
        return new DefaultSolverFactory<>(solverConfig);
    }

    // ************************************************************************
    // Static creation methods: empty
    // ************************************************************************

    /**
     * To build configuration programmatically, use {@link SolverConfig()} instead,
     * although it's often recommended to instead load a partial configuration
     * with {@link SolverConfig#createFromXmlResource(String)}.
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig()} and {@link SolverFactory#create(SolverConfig)}.
     * Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createEmpty() {
        return new DefaultSolverFactory<>(new SolverConfig());
    }

    /**
     * As defined by {@link #createEmpty()}.
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     * @deprecated in favor of {@link SolverConfig(ClassLoader)} and {@link SolverFactory#create(SolverConfig)}.
     * Will be removed in 8.0.
     */
    @Deprecated
    public static <Solution_> SolverFactory<Solution_> createEmpty(ClassLoader classLoader) {
        return new DefaultSolverFactory<>(new SolverConfig(classLoader));
    }

    // ************************************************************************
    // Static creation methods: KieContainer
    // ************************************************************************

    // TODO Deprecate KieContainer methods in favor of Quarkus, Kogito and Spring Boot

    /**
     * Uses {@link KieServices#getKieClasspathContainer()}.
     * @param solverConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromKieContainerXmlResource(
            String solverConfigResource) {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        return createFromKieContainerXmlResource(kieContainer, solverConfigResource);
    }

    /**
     * @param releaseId never null
     * @param solverConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromKieContainerXmlResource(
            ReleaseId releaseId, String solverConfigResource) {
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        return createFromKieContainerXmlResource(kieContainer, solverConfigResource);
    }

    /**
     * @param kieContainer never null
     * @param solverConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createFromKieContainerXmlResource(
            KieContainer kieContainer, String solverConfigResource) {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource,
                kieContainer.getClassLoader());
        return new DefaultSolverFactory<>(solverConfig, new SolverConfigContext(kieContainer));
    }

    /**
     * @param releaseId never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createEmptyFromKieContainer(ReleaseId releaseId) {
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        return createEmptyFromKieContainer(kieContainer);
    }

    /**
     * @param kieContainer never null
     * @return never null
     * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
     */
    public static <Solution_> SolverFactory<Solution_> createEmptyFromKieContainer(KieContainer kieContainer) {
        SolverConfig solverConfig = new SolverConfig(kieContainer.getClassLoader());
        return new DefaultSolverFactory<>(solverConfig, new SolverConfigContext(kieContainer));
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Creates a new {@link Solver} instance.
     * @return never null
     */
    public abstract Solver<Solution_> buildSolver();

    /**
     * Useful to reuse the {@link Score} calculation (for example in a UI)
     * and to explain the {@link Score} to the user
     * with the {@link ConstraintMatchTotal} and {@link Indictment} API.
     * @return never null
     */
    public abstract ScoreDirectorFactory<Solution_> getScoreDirectorFactory();

    /**
     * Deprecated. To configure a {@link SolverFactory} dynamically (without parsing XML each time),
     * use {@link SolverFactory#create(SolverConfig)} instead.
     * <p>
     * This method is not thread-safe. To configure a {@link SolverConfig} differently for parallel requests,
     * build a template {@link SolverFactory} from XML
     * and clone it {@link SolverFactory#cloneSolverFactory()} for each request, before before calling this method.
     * @return never null
     * @deprecated in favor of {@link SolverConfig#SolverConfig(SolverConfig)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public abstract SolverConfig getSolverConfig();

    /**
     * Deprecated. To configure a {@link SolverFactory} dynamically (without parsing XML each time),
     * use {@link SolverConfig#SolverConfig(SolverConfig)} and {@link SolverFactory#create(SolverConfig)} instead.
     * <p>
     * Build a {@link SolverFactory} quickly (without parsing XML) that builds the exact same {@link Solver}
     * with {@link #buildSolver()}, but can also be modified with {@link #getSolverConfig()} to build a different
     * {@link Solver} without affecting the original {@link SolverFactory}.
     * @return never null, often a different {@link SolverFactory} subclass implementation than this instance
     * @deprecated in favor of {@link SolverConfig#SolverConfig(SolverConfig)}
     * and {@link SolverFactory#create(SolverConfig)}. Will be removed in 8.0.
     */
    @Deprecated
    public abstract SolverFactory<Solution_> cloneSolverFactory();

}

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
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.EmptySolverFactory;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

/**
 * Builds {@link Solver} instances.
 * <p>
 * To build an instance, use {@link #createFromXmlResource(String)} or any of the other creation methods.
 * <p>
 * Supports tweaking the configuration programmatically before a {@link Solver} instance is build.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class SolverFactory<Solution_> {

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    /**
     * Uses {@link KieServices#getKieClasspathContainer()}.
     * @param solverConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromKieContainerXmlResource(
            String solverConfigResource) {
        KieContainer kieContainer = KieServices.Factory.get().getKieClasspathContainer();
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(kieContainer))
                .configure(solverConfigResource);
    }

    /**
     * @param releaseId never null
     * @param solverConfigResource never null, a classpath resource in the {@link KieContainer}
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
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
     */
    public static <Solution_> SolverFactory<Solution_> createFromKieContainerXmlResource(
            KieContainer kieContainer, String solverConfigResource) {
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(kieContainer))
                .configure(solverConfigResource);
    }

    /**
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource) {
        return new XStreamXmlSolverFactory<Solution_>()
                .configure(solverConfigResource);
    }

    /**
     * See {@link #createFromXmlResource(String)}.
     * @param solverConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlResource(String solverConfigResource, ClassLoader classLoader) {
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(classLoader))
                .configure(solverConfigResource);
    }

    /**
     * @param solverConfigFile never null
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile) {
        return new XStreamXmlSolverFactory<Solution_>()
                .configure(solverConfigFile);
    }

    /**
     * @param solverConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlFile(File solverConfigFile, ClassLoader classLoader) {
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(classLoader))
                .configure(solverConfigFile);
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlInputStream(InputStream in) {
        return new XStreamXmlSolverFactory<Solution_>()
                .configure(in);
    }

    /**
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(classLoader))
                .configure(in);
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlReader(Reader reader) {
        return new XStreamXmlSolverFactory<Solution_>()
                .configure(reader);
    }

    /**
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createFromXmlReader(Reader reader, ClassLoader classLoader) {
        return new XStreamXmlSolverFactory<Solution_>(new SolverConfigContext(classLoader))
                .configure(reader);
    }

    // ************************************************************************
    // Static creation methods: empty
    // ************************************************************************

    /**
     * Useful to build configuration programmatically, although it's almost always recommended
     * to instead load a partial configuration with {@link #createFromXmlResource(String)}
     * and configure the remainder programmatically with {@link #getSolverConfig()}.
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createEmpty() {
        return new EmptySolverFactory<>();
    }

    /**
     * See {@link #createEmpty()}.
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createEmpty(ClassLoader classLoader) {
        return new EmptySolverFactory<>(new SolverConfigContext(classLoader));
    }

    /**
     * @param releaseId never null
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createEmptyFromKieContainer(ReleaseId releaseId) {
        KieContainer kieContainer = KieServices.Factory.get().newKieContainer(releaseId);
        return createEmptyFromKieContainer(kieContainer);
    }

    /**
     * @param kieContainer never null
     * @return never null
     */
    public static <Solution_> SolverFactory<Solution_> createEmptyFromKieContainer(KieContainer kieContainer) {
        return new EmptySolverFactory<>(new SolverConfigContext(kieContainer));
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Allows you to programmatically change the {@link SolverConfig} at runtime before building the {@link Solver}.
     * <p>
     * This method is not thread-safe. To configure a {@link SolverConfig} differently for parallel requests,
     * build a template {@link SolverFactory} from XML
     * and clone it {@link SolverFactory#cloneSolverFactory()} for each request, before before calling this method.
     * @return never null
     */
    public abstract SolverConfig getSolverConfig();

    /**
     * Build a {@link SolverFactory} quickly (without parsing XML) that builds the exact same {@link Solver}
     * with {@link #buildSolver()}, but can also be modified with {@link #getSolverConfig()} to build a different
     * {@link Solver} without affecting the original {@link SolverFactory}.
     * @return never null, often a different {@link SolverFactory} subclass implementation than this instance
     */
    public abstract SolverFactory<Solution_> cloneSolverFactory();

    /**
     * Creates a new {@link Solver} instance.
     * @return never null
     */
    public abstract Solver<Solution_> buildSolver();

}

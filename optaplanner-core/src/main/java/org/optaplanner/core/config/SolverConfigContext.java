/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config;

import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * Provided to a {@link SolverConfig#buildSolver(SolverConfigContext)}
 * to inject a reference to containers, .
 */
public class SolverConfigContext {

    private final ClassLoader classLoader;
    private final KieContainer kieContainer;

    /**
     * Vanilla context.
     */
    public SolverConfigContext() {
        classLoader = null;
        kieContainer = null;
    }

    /**
     * Useful for OSGi, JBoss modules, ... (without a kjar deployment).
     * @param classLoader if null behaves as {@link #SolverConfigContext()}
     */
    public SolverConfigContext(ClassLoader classLoader) {
        this.classLoader = classLoader;
        kieContainer = null;
    }

    /**
     * Useful for a kjar deployment. The {@link KieContainer} also defines the non-vanilla {@link ClassLoader}.
     * @param kieContainer if null behaves as {@link #SolverConfigContext()}
     */
    public SolverConfigContext(KieContainer kieContainer) {
        classLoader = null;
        this.kieContainer = kieContainer;
    }

    /**
     * Used to work with a {@link ClassLoader} provided by OSGi, JBoss modules, etc, but not {@link KieContainer}.
     * <p>
     * Must be null if {@link #getKieContainer()} is not null.
     * @return sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      if this is null and {@link #getKieContainer()} is also null, then the default {@link ClassLoader} is used
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Must be null if {@link #getClassLoader()} is not null.
     * @return sometimes null, affects the {@link ClassLoader} to use for loading all resources and {@link Class}es
     */
    public KieContainer getKieContainer() {
        return kieContainer;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public ClassLoader determineActualClassLoader() {
        if (classLoader != null) {
            return classLoader;
        } else if (kieContainer != null) {
            return kieContainer.getClassLoader();
        }
        return getClass().getClassLoader();
    }

    public void validate() {
        if (classLoader != null && kieContainer != null) {
            throw new IllegalStateException("The classLoader (" + classLoader + ") and kieContainer (" + kieContainer
                    + ") cannot both be configured because the " + KieContainer.class.getSimpleName()
                    + " already has a " + ClassLoader.class.getSimpleName() + ".");
        }
    }

}

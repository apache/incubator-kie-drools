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
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

/**
 * Provided to a {@link SolverConfig#buildSolver(SolverConfigContext)}
 * to inject a reference to containers, .
 */
public class SolverConfigContext {

    private ClassLoader classLoader;
    private KieContainer kieContainer;

    public SolverConfigContext() {
    }

    public SolverConfigContext(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public SolverConfigContext(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * @return sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *      null to use the default {@link ClassLoader}
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    public void setKieContainer(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public ClassLoader determineActualClassLoader() {
        return (classLoader != null) ? classLoader : getClass().getClassLoader();
    }

}

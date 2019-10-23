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

/**
 * Do not use this class, it is an internal class.
 * It should be in impl.
 * <p>
 * Will be removed in 8.0.
 */
public class SolverConfigContext {

    private final KieContainer kieContainer;

    /**
     * Vanilla context.
     */
    public SolverConfigContext() {
        kieContainer = null;
    }

    /**
     * Useful for a kjar deployment. The {@link KieContainer} also defines the non-vanilla {@link ClassLoader}.
     * @param kieContainer if null behaves as {@link #SolverConfigContext()}
     */
    public SolverConfigContext(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * @return sometimes null, affects the {@link ClassLoader} to use for loading all resources and {@link Class}es
     */
    public KieContainer getKieContainer() {
        return kieContainer;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public void validate() {
    }

}

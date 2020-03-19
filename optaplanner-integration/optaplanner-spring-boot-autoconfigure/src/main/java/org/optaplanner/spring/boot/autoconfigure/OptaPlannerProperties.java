/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(value = "optaplanner", ignoreUnknownFields = false)
public class OptaPlannerProperties {

    public static final String DEFAULT_SOLVER_CONFIG_URL = "solverConfig.xml";

    @NestedConfigurationProperty
    private SolverManagerProperties solverManager;

    /**
     * A classpath resource to read the solver configuration XML.
     * Defaults to "solverConfig.xml".
     * If this property isn't specified, that solverConfig.xml is optional.
     */
    private String solverConfigXml;

    @NestedConfigurationProperty
    private SolverProperties solver;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public SolverManagerProperties getSolverManager() {
        return solverManager;
    }

    public void setSolverManager(SolverManagerProperties solverManager) {
        this.solverManager = solverManager;
    }

    public String getSolverConfigXml() {
        return solverConfigXml;
    }

    public void setSolverConfigXml(String solverConfigXml) {
        this.solverConfigXml = solverConfigXml;
    }

    public SolverProperties getSolver() {
        return solver;
    }

    public void setSolver(SolverProperties solver) {
        this.solver = solver;
    }

}

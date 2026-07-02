/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.spring.boot.autoconfigure.config;

import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(value = "optaplanner", ignoreUnknownFields = false)
public class OptaPlannerProperties {

    public static final String DEFAULT_SOLVER_CONFIG_URL = "solverConfig.xml";
    public static final String DEFAULT_SOLVER_BENCHMARK_CONFIG_URL = "solverBenchmarkConfig.xml";
    public static final String DEFAULT_CONSTRAINTS_DRL_URL = "constraints.drl";
    public static final String SCORE_DRL_PROPERTY = "optaplanner.score-drl";

    @NestedConfigurationProperty
    private SolverManagerProperties solverManager;

    /**
     * A classpath resource to read the solver configuration XML.
     * Defaults to {@value #DEFAULT_SOLVER_CONFIG_URL}.
     * If this property isn't specified, that {@value #DEFAULT_SOLVER_CONFIG_URL} file is optional.
     */
    private String solverConfigXml;

    /**
     * A classpath resource to read the solver score DRL.
     * Defaults to "{@link #DEFAULT_CONSTRAINTS_DRL_URL}".
     * Do not define this property when a {@link ConstraintProvider}, {@link EasyScoreCalculator} or
     * {@link IncrementalScoreCalculator} class exists.
     */
    private String scoreDrl;

    @NestedConfigurationProperty
    private SolverProperties solver;

    @NestedConfigurationProperty
    private BenchmarkProperties benchmark;

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

    public String getScoreDrl() {
        return scoreDrl;
    }

    public void setScoreDrl(String scoreDrl) {
        this.scoreDrl = scoreDrl;
    }

    public SolverProperties getSolver() {
        return solver;
    }

    public void setSolver(SolverProperties solver) {
        this.solver = solver;
    }

    public BenchmarkProperties getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(BenchmarkProperties benchmark) {
        this.benchmark = benchmark;
    }
}

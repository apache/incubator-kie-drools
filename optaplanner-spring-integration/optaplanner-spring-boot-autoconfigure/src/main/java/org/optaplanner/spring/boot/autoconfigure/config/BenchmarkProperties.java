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

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class BenchmarkProperties {

    public static final String DEFAULT_SOLVER_BENCHMARK_CONFIG_URL = "solverBenchmarkConfig.xml";
    public static final String DEFAULT_BENCHMARK_RESULT_DIRECTORY = "target/benchmarks";

    /**
     * A classpath resource to read the benchmark configuration XML.
     * Defaults to {@value #DEFAULT_SOLVER_BENCHMARK_CONFIG_URL}.
     * If this property isn't specified, that {@value #DEFAULT_SOLVER_BENCHMARK_CONFIG_URL} file is optional.
     */
    private String solverBenchmarkConfigXml;

    /**
     * The directory to which to write the benchmark HTML report and graphs,
     * relative from the working directory.
     */
    private String resultDirectory;

    @NestedConfigurationProperty
    private BenchmarkSolverProperties solver;

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public String getSolverBenchmarkConfigXml() {
        return solverBenchmarkConfigXml;
    }

    public void setSolverBenchmarkConfigXml(String solverBenchmarkConfigXml) {
        this.solverBenchmarkConfigXml = solverBenchmarkConfigXml;
    }

    public String getResultDirectory() {
        return resultDirectory;
    }

    public void setResultDirectory(String resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public BenchmarkSolverProperties getSolver() {
        return solver;
    }

    public void setSolver(BenchmarkSolverProperties solver) {
        this.solver = solver;
    }

}

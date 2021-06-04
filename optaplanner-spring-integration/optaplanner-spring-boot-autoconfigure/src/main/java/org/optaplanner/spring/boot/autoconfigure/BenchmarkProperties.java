/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.springframework.boot.context.properties.NestedConfigurationProperty;

public class BenchmarkProperties {
    private String solverBenchmarkConfigXml;
    private String resultDirectory;

    @NestedConfigurationProperty
    private BenchmarkSolverProperties solver;

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

    public static class BenchmarkSolverProperties {
        private TerminationProperties termination;

        public TerminationProperties getTermination() {
            return termination;
        }

        public void setTermination(TerminationProperties termination) {
            this.termination = termination;
        }
    }
}

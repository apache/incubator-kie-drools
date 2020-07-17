/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.config.blueprint;

import java.util.List;

import org.optaplanner.benchmark.config.SolverBenchmarkConfig;

public class SolverBenchmarkBluePrintConfig {

    protected SolverBenchmarkBluePrintType solverBenchmarkBluePrintType = null;

    public SolverBenchmarkBluePrintType getSolverBenchmarkBluePrintType() {
        return solverBenchmarkBluePrintType;
    }

    public void setSolverBenchmarkBluePrintType(SolverBenchmarkBluePrintType solverBenchmarkBluePrintType) {
        this.solverBenchmarkBluePrintType = solverBenchmarkBluePrintType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public List<SolverBenchmarkConfig> buildSolverBenchmarkConfigList() {
        validate();
        return solverBenchmarkBluePrintType.buildSolverBenchmarkConfigList();
    }

    protected void validate() {
        if (solverBenchmarkBluePrintType == null) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkBluePrint must have"
                            + " a solverBenchmarkBluePrintType (" + solverBenchmarkBluePrintType + ").");
        }
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SolverBenchmarkBluePrintConfig withSolverBenchmarkBluePrintType(
            SolverBenchmarkBluePrintType solverBenchmarkBluePrintType) {
        this.solverBenchmarkBluePrintType = solverBenchmarkBluePrintType;
        return this;
    }

}

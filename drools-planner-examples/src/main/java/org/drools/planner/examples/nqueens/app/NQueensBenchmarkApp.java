/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.nqueens.app;

import java.io.File;

import org.drools.planner.examples.common.app.CommonBenchmarkApp;
import org.drools.planner.examples.nqueens.domain.NQueens;

public class NQueensBenchmarkApp extends CommonBenchmarkApp {

    public static final String SOLVER_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/nqueens/benchmark/nqueensSolverBenchmarkConfig.xml";

    public static void main(String[] args) {
        new NQueensBenchmarkApp(SOLVER_BENCHMARK_CONFIG).process();
    }

    public NQueensBenchmarkApp(String solverBenchmarkConfig) {
        super(solverBenchmarkConfig, NQueens.class);
    }

}

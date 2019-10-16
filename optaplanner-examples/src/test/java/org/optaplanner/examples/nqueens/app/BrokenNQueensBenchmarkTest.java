/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nqueens.app;

import java.io.File;

import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class BrokenNQueensBenchmarkTest extends PlannerBenchmarkTest {

    public BrokenNQueensBenchmarkTest() {
        super(NQueensApp.SOLVER_CONFIG);
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 100000, expected = PlannerBenchmarkException.class)
    public void benchmarkBroken8queens() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/8queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        benchmarkConfig.setWarmUpSecondsSpentLimit(0L);
        benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getTerminationConfig()
                .setStepCountLimit(-100); // Intentionally crash the solver
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        benchmark.benchmark();
    }

}

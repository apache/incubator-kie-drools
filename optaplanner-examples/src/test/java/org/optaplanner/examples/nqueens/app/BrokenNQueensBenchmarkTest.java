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
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.domain.Row;

public class BrokenNQueensBenchmarkTest extends PlannerBenchmarkTest {

    @Override
    protected String createBenchmarkConfigResource() {
        return "org/optaplanner/examples/nqueens/benchmark/nqueensBenchmarkConfig.xml";
    }

    @Override
    protected PlannerBenchmarkFactory buildPlannerBenchmarkFactory(File unsolvedDataFile) {
        PlannerBenchmarkFactory benchmarkFactory = super.buildPlannerBenchmarkFactory(unsolvedDataFile);
        PlannerBenchmarkConfig benchmarkConfig = benchmarkFactory.getPlannerBenchmarkConfig();
        benchmarkConfig.setWarmUpSecondsSpentLimit(0L);
        benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getEntityClassList()
                .add(Row.class); // Intentionally crash the solver
        return benchmarkFactory;
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 100000, expected = PlannerBenchmarkException.class)
    public void benchmarkBroken8queens() {
        runBenchmarkTest(new File("data/nqueens/unsolved/8queens.xml"));
    }

}

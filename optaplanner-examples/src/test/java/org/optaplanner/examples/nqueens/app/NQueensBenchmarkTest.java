/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NQueensBenchmarkTest extends PlannerBenchmarkTest {

    public NQueensBenchmarkTest() {
        super(NQueensApp.SOLVER_CONFIG);
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void benchmark64queens() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        addAllStatistics(benchmarkConfig);
        benchmarkConfig.setParallelBenchmarkCount("AUTO");
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        benchmark.benchmark();
    }

    @Test(timeout = 600000)
    public void benchmark64queensSingleThread() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig= buildPlannerBenchmarkConfig();
        addAllStatistics(benchmarkConfig);
        benchmarkConfig.setParallelBenchmarkCount("1");
        PlannerBenchmark benchmark = PlannerBenchmarkFactory.create(benchmarkConfig).buildPlannerBenchmark(problem);
        benchmark.benchmark();
    }

    protected void addAllStatistics(PlannerBenchmarkConfig benchmarkConfig) {
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        problemBenchmarksConfig.setSingleStatisticTypeList(Arrays.asList(SingleStatisticType.values()));
        problemBenchmarksConfig.setProblemStatisticTypeList(Arrays.asList(ProblemStatisticType.values()));
        benchmarkConfig.getInheritedSolverBenchmarkConfig().setProblemBenchmarksConfig(problemBenchmarksConfig);
    }

    @Test
    public void benchmarkDirectoryNameDuplication() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/4queens.xml"));
        PlannerBenchmarkConfig benchmarkConfig = buildPlannerBenchmarkConfig();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(benchmarkConfig);
        DefaultPlannerBenchmark plannerBenchmark = (DefaultPlannerBenchmark) benchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmarkingStarted();
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(benchmarkConfig.getBenchmarkDirectory());
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(benchmarkConfig.getBenchmarkDirectory());
    }

}

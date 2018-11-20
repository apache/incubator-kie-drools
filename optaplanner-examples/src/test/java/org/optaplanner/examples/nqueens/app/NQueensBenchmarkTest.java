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
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class NQueensBenchmarkTest extends PlannerBenchmarkTest {

    @Override
    protected String createSolverConfigResource() {
        return NQueensApp.SOLVER_CONFIG;
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void benchmark64queens() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        addAllStatistics(plannerBenchmarkFactory);
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("AUTO");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

    @Test(timeout = 600000)
    public void benchmark64queensSingleThread() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/64queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        addAllStatistics(plannerBenchmarkFactory);
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("1");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

    protected void addAllStatistics(PlannerBenchmarkFactory plannerBenchmarkFactory) {
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        problemBenchmarksConfig.setSingleStatisticTypeList(Arrays.asList(SingleStatisticType.values()));
        problemBenchmarksConfig.setProblemStatisticTypeList(Arrays.asList(ProblemStatisticType.values()));
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().getInheritedSolverBenchmarkConfig().setProblemBenchmarksConfig(problemBenchmarksConfig);
    }

    @Test
    public void benchmarkDirectoryNameDuplication() {
        NQueens problem = new XStreamSolutionFileIO<NQueens>(NQueens.class)
                .read(new File("data/nqueens/unsolved/4queens.xml"));
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory();
        DefaultPlannerBenchmark plannerBenchmark = (DefaultPlannerBenchmark) plannerBenchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmarkingStarted();
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
        plannerBenchmark.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
    }

}

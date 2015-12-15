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
import java.util.List;

import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.PlannerBenchmarkRunner;
import org.optaplanner.examples.common.app.PlannerBenchmarkTest;

public class NQueensBenchmarkTest extends PlannerBenchmarkTest {

    @Override
    protected String createBenchmarkConfigResource() {
        return "org/optaplanner/examples/nqueens/benchmark/nqueensBenchmarkConfig.xml";
    }

    public List<SingleStatisticType> buildAllSingleStatisticTypeList() {
        return Arrays.asList(SingleStatisticType.values());
    }

    public List<ProblemStatisticType> buildAllProblemStatisticTypeList() {
        return Arrays.asList(ProblemStatisticType.values());
    }

    // ************************************************************************
    // Tests
    // ************************************************************************

    @Test(timeout = 600000)
    public void benchmark64queens() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory(new File("data/nqueens/unsolved/64queens.xml"));
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().getInheritedSolverBenchmarkConfig().getProblemBenchmarksConfig().setSingleStatisticTypeList(buildAllSingleStatisticTypeList());
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().getInheritedSolverBenchmarkConfig().getProblemBenchmarksConfig().setProblemStatisticTypeList(buildAllProblemStatisticTypeList());
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("AUTO");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }

    @Test(timeout = 600000)
    public void benchmark64queensSingleThread() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory(new File("data/nqueens/unsolved/64queens.xml"));
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().getInheritedSolverBenchmarkConfig().getProblemBenchmarksConfig().setSingleStatisticTypeList(buildAllSingleStatisticTypeList());
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().getInheritedSolverBenchmarkConfig().getProblemBenchmarksConfig().setProblemStatisticTypeList(buildAllProblemStatisticTypeList());
        plannerBenchmarkFactory.getPlannerBenchmarkConfig().setParallelBenchmarkCount("1");
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }

    @Test
    public void benchmarkDirectoryNameDuplication() {
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory(new File("data/nqueens/unsolved/4queens.xml"));
        PlannerBenchmarkRunner plannerBenchmarkRunner = (PlannerBenchmarkRunner) plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmarkRunner.benchmarkingStarted();
        plannerBenchmarkRunner.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
        plannerBenchmarkRunner.getPlannerBenchmarkResult().initBenchmarkReportDirectory(plannerBenchmarkFactory.getPlannerBenchmarkConfig().getBenchmarkDirectory());
    }

}

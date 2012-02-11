/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.common.app;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.drools.planner.benchmark.api.PlannerBenchmark;
import org.drools.planner.benchmark.config.PlannerBenchmarkConfig;
import org.drools.planner.benchmark.config.ProblemBenchmarksConfig;
import org.drools.planner.benchmark.config.SolverBenchmarkConfig;
import org.drools.planner.benchmark.config.XmlPlannerBenchmarkFactory;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.XmlSolverFactory;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.SolutionDao;

import static org.junit.Assert.*;

/**
 * Runs an example solver.
 * All tests ending with the suffix <code>PerformanceTest</code> are reported on by hudson
 * in graphs which show the execution time over builds.
 * <p/>
 * Recommended courtesy notes: Always use a timeout value on @Test.
 * The timeout should be the triple of the timeout on a normal 3 year old desktop computer,
 * because some of the hudson machines are old.
 * For example, on a normal 3 year old desktop computer it always finishes in less than 1 minute,
 * then specify a timeout of 3 minutes.
 */
public abstract class PlannerBenchmarkTest extends LoggingTest {

    private static final int MAXIMUM_SOLVER_BENCHMARK_SIZE = 6;
    private static final long WARM_UP_SECONDS_SPEND = 5L;
    private static final long MAXIMUM_SECONDS_SPEND = 30L;

    protected abstract String createBenchmarkConfigResource();

    protected void runBenchmarkTest(File unsolvedDataFile) {
        XmlPlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory(unsolvedDataFile);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }
    
    private XmlPlannerBenchmarkFactory buildPlannerBenchmarkFactory(File unsolvedDataFile) {
        XmlPlannerBenchmarkFactory benchmarkFactory = new XmlPlannerBenchmarkFactory();
        benchmarkFactory.configure(createBenchmarkConfigResource());
        PlannerBenchmarkConfig plannerBenchmarkConfig = benchmarkFactory.getPlannerBenchmarkConfig();
        plannerBenchmarkConfig.setBenchmarkDirectory(new File("target/test/data/nqueens"));
        plannerBenchmarkConfig.setWarmUpHoursSpend(0L);
        plannerBenchmarkConfig.setWarmUpMinutesSpend(0L);
        plannerBenchmarkConfig.setWarmUpSecondsSpend(WARM_UP_SECONDS_SPEND);
        plannerBenchmarkConfig.setWarmUpTimeMillisSpend(0L);
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = plannerBenchmarkConfig.getSolverBenchmarkConfigList();
        if (solverBenchmarkConfigList.size() > MAXIMUM_SOLVER_BENCHMARK_SIZE) {
            solverBenchmarkConfigList = solverBenchmarkConfigList.subList(0, MAXIMUM_SOLVER_BENCHMARK_SIZE);
            plannerBenchmarkConfig.setSolverBenchmarkConfigList(solverBenchmarkConfigList);
        }
        long maximumSecondsSpendPerSolverBenchmark = MAXIMUM_SECONDS_SPEND / solverBenchmarkConfigList.size();
        SolverBenchmarkConfig inheritedSolverBenchmarkConfig = plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig();
        if (inheritedSolverBenchmarkConfig != null) {
            inheritedSolverBenchmarkConfig.getProblemBenchmarksConfig().setInputSolutionFileList(
                    Collections.singletonList(unsolvedDataFile));
            TerminationConfig terminationConfig = new TerminationConfig();
            terminationConfig.setMaximumSecondsSpend(maximumSecondsSpendPerSolverBenchmark);
            inheritedSolverBenchmarkConfig.getSolverConfig().setTerminationConfig(terminationConfig);
        }
        for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
            solverBenchmarkConfig.getProblemBenchmarksConfig().setInputSolutionFileList(null);
            solverBenchmarkConfig.getSolverConfig().setTerminationConfig(new TerminationConfig());
        }
        return benchmarkFactory;
    }

}

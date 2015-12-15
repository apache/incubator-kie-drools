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

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;

/**
 * Runs an example solver.
 * All tests ending with the suffix <code>PerformanceTest</code> are reported on by hudson
 * in graphs which show the execution time over builds.
 * <p>
 * Recommended courtesy notes: Always use a timeout value on @Test.
 * The timeout should be the triple of the timeout on a normal 3 year old desktop computer,
 * because some of the hudson machines are old.
 * For example, on a normal 3 year old desktop computer it always finishes in less than 1 minute,
 * then specify a timeout of 3 minutes.
 */
public abstract class PlannerBenchmarkTest extends LoggingTest {

    private static final int MAXIMUM_SOLVER_BENCHMARK_SIZE = 6;
    private static final long WARM_UP_SECONDS_SPENT = 5L;
    private static final long MAXIMUM_SECONDS_SPENT = 30L;

    protected abstract String createBenchmarkConfigResource();

    protected void runBenchmarkTest(File unsolvedDataFile) {
        PlannerBenchmarkFactory plannerBenchmarkFactory = buildPlannerBenchmarkFactory(unsolvedDataFile);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }

    protected PlannerBenchmarkFactory buildPlannerBenchmarkFactory(File unsolvedDataFile) {
        String benchmarkConfigResource = createBenchmarkConfigResource();
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(benchmarkConfigResource);
        PlannerBenchmarkConfig plannerBenchmarkConfig = benchmarkFactory.getPlannerBenchmarkConfig();
        String benchmarkDirectoryPath = plannerBenchmarkConfig.getBenchmarkDirectory().getPath();
        // On Windows, getPath() contains backslashes instead of normal slashes
        String prefix = "local" + File.separator + "data" + File.separator;
        if (!benchmarkDirectoryPath.startsWith(prefix)) {
            throw new IllegalStateException("The benchmarkDirectoryPath (" + benchmarkDirectoryPath
                    + ") should start with prefix (" + prefix + ")");
        }
        plannerBenchmarkConfig.setBenchmarkDirectory(new File(benchmarkDirectoryPath.replace(prefix,
                "target" + File.separator + "test" + File.separator + "data" + File.separator)));
        plannerBenchmarkConfig.setWarmUpHoursSpentLimit(0L);
        plannerBenchmarkConfig.setWarmUpMinutesSpentLimit(0L);
        plannerBenchmarkConfig.setWarmUpSecondsSpentLimit(WARM_UP_SECONDS_SPENT);
        plannerBenchmarkConfig.setWarmUpMillisecondsSpentLimit(0L);
        List<SolverBenchmarkConfig> solverBenchmarkConfigList = plannerBenchmarkConfig.getSolverBenchmarkConfigList();
        if (ConfigUtils.isEmptyCollection(solverBenchmarkConfigList)) {
            throw new IllegalStateException("The benchmarkConfigResource (" + benchmarkConfigResource
                    + ") should have at least 1 solverBenchmarkConfig.");
        }
        if (solverBenchmarkConfigList.size() > MAXIMUM_SOLVER_BENCHMARK_SIZE) {
            solverBenchmarkConfigList = solverBenchmarkConfigList.subList(0, MAXIMUM_SOLVER_BENCHMARK_SIZE);
            plannerBenchmarkConfig.setSolverBenchmarkConfigList(solverBenchmarkConfigList);
        }
        long maximumSecondsSpentPerSolverBenchmark = MAXIMUM_SECONDS_SPENT / solverBenchmarkConfigList.size();
        SolverBenchmarkConfig inheritedSolverBenchmarkConfig = plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig();
        if (inheritedSolverBenchmarkConfig != null) {
            ProblemBenchmarksConfig problemBenchmarksConfig = inheritedSolverBenchmarkConfig.getProblemBenchmarksConfig();
            if (problemBenchmarksConfig == null) {
                problemBenchmarksConfig = new ProblemBenchmarksConfig();
                inheritedSolverBenchmarkConfig.setProblemBenchmarksConfig(problemBenchmarksConfig);
            }
            problemBenchmarksConfig.setInputSolutionFileList(
                    Collections.singletonList(unsolvedDataFile));
            TerminationConfig terminationConfig = new TerminationConfig();
            terminationConfig.setSecondsSpentLimit(maximumSecondsSpentPerSolverBenchmark);
            inheritedSolverBenchmarkConfig.getSolverConfig().setTerminationConfig(terminationConfig);
        }
        for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
            ProblemBenchmarksConfig problemBenchmarksConfig = solverBenchmarkConfig.getProblemBenchmarksConfig();
            if (problemBenchmarksConfig != null) {
                problemBenchmarksConfig.setInputSolutionFileList(null);
            }
            solverBenchmarkConfig.getSolverConfig().setTerminationConfig(new TerminationConfig());
        }
        return benchmarkFactory;
    }

}

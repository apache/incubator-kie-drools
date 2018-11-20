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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintType;
import org.optaplanner.core.api.solver.SolverFactory;
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

    private static final long WARM_UP_SECONDS_SPENT = 2L;
    private static final long MAXIMUM_SECONDS_SPENT = 8L;

    protected abstract String createSolverConfigResource();

    protected PlannerBenchmarkFactory buildPlannerBenchmarkFactory() {
        SolverFactory<Object> solverFactory = SolverFactory.createFromXmlResource(createSolverConfigResource());
        File benchmarkDirectory = new File("target/test/data");
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverFactory(solverFactory, benchmarkDirectory);
        PlannerBenchmarkConfig plannerBenchmarkConfig = benchmarkFactory.getPlannerBenchmarkConfig();
        plannerBenchmarkConfig.setWarmUpSecondsSpentLimit(WARM_UP_SECONDS_SPENT);
        plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.emptyList());
        plannerBenchmarkConfig.setSolverBenchmarkBluePrintConfigList(Collections.singletonList(
                new SolverBenchmarkBluePrintConfig().withSolverBenchmarkBluePrintType(
                        SolverBenchmarkBluePrintType.CONSTRUCTION_HEURISTIC_WITH_AND_WITHOUT_LOCAL_SEARCH)));

        long maximumSecondsSpentPerSolverBenchmark = MAXIMUM_SECONDS_SPENT / 2;
        SolverBenchmarkConfig inheritedSolverBenchmarkConfig = plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig();
        inheritedSolverBenchmarkConfig.getSolverConfig().setTerminationConfig(
                new TerminationConfig().withSecondsSpentLimit(maximumSecondsSpentPerSolverBenchmark));
        return benchmarkFactory;
    }

}

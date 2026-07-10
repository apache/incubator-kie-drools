/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.common.app;

import java.io.File;
import java.util.Collections;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintType;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

/**
 * Runs an example solver.
 * <p>
 * Recommended courtesy notes: Always use a timeout value on @Test.
 * The timeout should be the triple of the timeout on a normal desktop computer,
 * as some CI nodes are slow.
 * For example, if on a normal desktop computer it always finishes in less than 1 minute,
 * then specify a timeout of 3 minutes.
 */
public abstract class PlannerBenchmarkTest extends LoggingTest {

    private static final long WARM_UP_SECONDS_SPENT = 2L;
    private static final long MAXIMUM_SECONDS_SPENT = 8L;

    private final String solverConfigResource;

    public PlannerBenchmarkTest(String solverConfigResource) {
        this.solverConfigResource = solverConfigResource;
    }

    protected PlannerBenchmarkConfig buildPlannerBenchmarkConfig() {
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(solverConfigResource);
        File benchmarkDirectory = new File("target/test/data");
        PlannerBenchmarkConfig benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig,
                benchmarkDirectory);
        benchmarkConfig.setWarmUpSecondsSpentLimit(WARM_UP_SECONDS_SPENT);

        /*
         * Benchmark blueprint will add two configs.
         * Therefore we need to ensure that no other configs will interfere.
         */
        benchmarkConfig.setSolverBenchmarkConfigList(Collections.emptyList());
        benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().setPhaseConfigList(Collections.emptyList());
        benchmarkConfig.setSolverBenchmarkBluePrintConfigList(Collections.singletonList(
                new SolverBenchmarkBluePrintConfig().withSolverBenchmarkBluePrintType(
                        SolverBenchmarkBluePrintType.CONSTRUCTION_HEURISTIC_WITH_AND_WITHOUT_LOCAL_SEARCH)));

        long maximumSecondsSpentPerSolverBenchmark = MAXIMUM_SECONDS_SPENT / 2;
        SolverBenchmarkConfig inheritedSolverBenchmarkConfig = benchmarkConfig.getInheritedSolverBenchmarkConfig();
        inheritedSolverBenchmarkConfig.getSolverConfig().setTerminationConfig(
                new TerminationConfig().withSecondsSpentLimit(maximumSecondsSpentPerSolverBenchmark));
        return benchmarkConfig;
    }

}

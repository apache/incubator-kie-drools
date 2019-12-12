/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class SolverManagerTest {

    @Test(timeout = 600_000)
    public void solveBatch_2InParallel() throws ExecutionException, InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(2);
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> {
                            try {
                                barrier.await();
                            } catch (InterruptedException | BrokenBarrierException e) {
                                fail("Cyclic barrier failed.");
                            }
                        }
                ), new ConstructionHeuristicPhaseConfig());
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig(solverConfig)
                .withParallelSolverCount("2");
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(solverManagerConfig);

        TestdataSolution solution1 = new TestdataSolution("s1");
        solution1.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution1.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solveBatch(1L, solution1);

        TestdataSolution solution2 = new TestdataSolution("s2");
        solution2.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution2.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));
        SolverJob<TestdataSolution, Long> solverJob2 = solverManager.solveBatch(2L, solution2);

        assertSolutionInitialized(solverJob1.getFinalBestSolution());
        assertSolutionInitialized(solverJob2.getFinalBestSolution());
    }

}

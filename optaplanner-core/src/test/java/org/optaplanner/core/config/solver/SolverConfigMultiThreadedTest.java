/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.testutil.MockThreadFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SolverConfigMultiThreadedTest {

    @Test
    public void moveThreadCountAutoIsCorrectlyResolvedWhenCpuCountIsPositive() {
        final int cpuCount = 16;
        assertEquals(Integer.valueOf(cpuCount - 2), mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountAutoIsResolvedToNullWhenCpuCountIsNegative() {
        final int cpuCount = -2;
        assertNull(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount());
    }

    private SolverConfig mockSolverConfigForMoveThreadCountAuto(int mockCpuCount) {
        SolverConfig solverConfig = spy(SolverConfig.class);
        when(solverConfig.getAvailableProcessors()).thenReturn(mockCpuCount);
        solverConfig.setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_AUTO);
        return solverConfig;
    }

    @Test
    public void moveThreadCountIsCorrectlyResolvedWhenValueIsPositive() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("2");
        assertEquals(Integer.valueOf(2), solverConfig.resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        try {
            solverConfig.resolveMoveThreadCount();
            fail("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException expectedException) {
            //expected
        }
    }

    @Test
    public void moveThreadCountIsResolvedToNullWhenValueIsNone() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
        assertNull(solverConfig.resolveMoveThreadCount());
    }

    @Test(timeout = 5000L)
    public void solvingWithTooHighThreadCountFinishes() {
        runSolvingAndVerifySolution(10, 20, "256");
    }

    @Ignore("PLANNER-1180")
    @Test(timeout = 5000L)
    public void solvingOfVerySmallProblemFinishes() {
        runSolvingAndVerifySolution(1, 1, "2");
    }

    private void runSolvingAndVerifySolution(final int entityCount, final int valueCount, final String moveThreadCount) {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setMoveThreadCount(moveThreadCount);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = createTestSolution(entityCount, valueCount);
        solution = solver.solve(solution);

        assertSolution(solver, solution);
    }

    private TestdataSolution createTestSolution(int entityCount, int valueCount) {
        TestdataSolution testdataSolution = new TestdataSolution();

        final List<TestdataValue> values = IntStream.range(0, valueCount)
                .mapToObj(number -> new TestdataValue("value" + number))
                .collect(Collectors.toList());
        final List<TestdataEntity> entities = IntStream.range(0, entityCount)
                .mapToObj(number -> new TestdataEntity("entity" + number))
                .collect(Collectors.toList());

        testdataSolution.setValueList(values);
        testdataSolution.setEntityList(entities);
        return testdataSolution;
    }

    private void assertSolution(final Solver<TestdataSolution> solver, final TestdataSolution solution) {
        assertNotNull(solver.getBestSolution());
        assertSame(solution, solver.getBestSolution());
        assertTrue(solution.getScore().isSolutionInitialized());
    }

    @Test(timeout = 5000L)
    public void customThreadFactoryClassIsUsed() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setThreadFactoryClass(MockThreadFactory.class);
        solverFactory.getSolverConfig().setMoveThreadCount("2");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = createTestSolution(3, 5);
        solution = solver.solve(solution);

        assertSolution(solver, solution);
        assertTrue(MockThreadFactory.hasBeenCalled());
    }
}

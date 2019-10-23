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
import org.optaplanner.core.config.solver.testutil.MockThreadFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        solverConfig.resolveMoveThreadCount();
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
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setMoveThreadCount(moveThreadCount);

        TestdataSolution solution = createTestSolution(entityCount, valueCount);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        assertTrue(solution.getScore().isSolutionInitialized());
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

    @Test(timeout = 5000L)
    public void customThreadFactoryClassIsUsed() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setThreadFactoryClass(MockThreadFactory.class);
        solverConfig.setMoveThreadCount("2");

        TestdataSolution solution = createTestSolution(3, 5);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertNotNull(solution);
        assertTrue(solution.getScore().isSolutionInitialized());
        assertTrue(MockThreadFactory.hasBeenCalled());
    }

}

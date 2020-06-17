/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.config.solver.testutil.MockThreadFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SolverConfigMultiThreadedTest {

    @Test
    public void moveThreadCountAutoIsCorrectlyResolvedWhenCpuCountIsPositive() {
        final int cpuCount = 16;
        assertThat(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount())
                .isEqualTo(Integer.valueOf(cpuCount - 2));
    }

    @Test
    public void moveThreadCountAutoIsResolvedToNullWhenCpuCountIsNegative() {
        final int cpuCount = -2;
        assertThat(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount()).isNull();
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
        assertThat(solverConfig.resolveMoveThreadCount()).isEqualTo(Integer.valueOf(2));
    }

    @Test
    public void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        assertThatIllegalArgumentException().isThrownBy(solverConfig::resolveMoveThreadCount);
    }

    @Test
    public void moveThreadCountIsResolvedToNullWhenValueIsNone() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
        assertThat(solverConfig.resolveMoveThreadCount()).isNull();
    }

    @Test
    @Timeout(5)
    public void solvingWithTooHighThreadCountFinishes() {
        runSolvingAndVerifySolution(10, 20, "256");
    }

    @Disabled("PLANNER-1180")
    @Test
    @Timeout(5)
    public void solvingOfVerySmallProblemFinishes() {
        runSolvingAndVerifySolution(1, 1, "2");
    }

    private void runSolvingAndVerifySolution(final int entityCount, final int valueCount, final String moveThreadCount) {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setMoveThreadCount(moveThreadCount);

        TestdataSolution solution = createTestSolution(entityCount, valueCount);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
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

    @Test
    @Timeout(5)
    public void customThreadFactoryClassIsUsed() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        solverConfig.setThreadFactoryClass(MockThreadFactory.class);
        solverConfig.setMoveThreadCount("2");

        TestdataSolution solution = createTestSolution(3, 5);

        solution = PlannerTestUtils.solve(solverConfig, solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
        assertThat(MockThreadFactory.hasBeenCalled()).isTrue();
    }

}

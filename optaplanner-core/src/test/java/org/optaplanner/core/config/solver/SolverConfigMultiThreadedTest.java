package org.optaplanner.core.config.solver;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.testutil.TestThreadFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SolverConfigMultiThreadedTest {

    @Test
    public void moveThreadCountAutoIsCorrectlyResolvedWhenCpuCountIsPositive() {
        final int cpuCount = 16;
        assertThat(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount()).isEqualTo(cpuCount - 2);
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
        assertThat(solverConfig.resolveMoveThreadCount()).isEqualTo(2);
    }

    @Test
    public void moveThreadCountThrowsExceptionWhenValueIsNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> solverConfig.resolveMoveThreadCount());
    }

    @Test
    public void moveThreadCountIsResolvedToNullWhenValueIsNone() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
        assertThat(solverConfig.resolveMoveThreadCount()).isNull();
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
        assertThat(solver.getBestSolution()).isNotNull().isSameAs(solution);
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test(timeout = 5000L)
    public void customThreadFactoryClassIsUsed() {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils.buildSolverFactory(
                TestdataSolution.class, TestdataEntity.class);
        solverFactory.getSolverConfig().setThreadFactoryClass(TestThreadFactory.class);
        solverFactory.getSolverConfig().setMoveThreadCount("2");
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = createTestSolution(3, 5);
        solution = solver.solve(solution);

        assertSolution(solver, solution);
        assertThat(TestThreadFactory.hasBeenCalled()).isTrue();
    }
}

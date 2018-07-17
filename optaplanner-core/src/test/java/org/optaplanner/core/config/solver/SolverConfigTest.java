package org.optaplanner.core.config.solver;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class SolverConfigTest {

    @Test
    public void testMoveThreadCountAutoPositive() {
        final int cpuCount = 16;
        assertThat(mockSolverConfigForMoveThreadCountAuto(cpuCount).resolveMoveThreadCount()).isEqualTo(cpuCount - 2);
    }

    @Test
    public void testMoveThreadCountAutoNegative() {
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
    public void testMoveThreadCountFixedPositive() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("2");
        assertThat(solverConfig.resolveMoveThreadCount()).isEqualTo(2);
    }

    @Test
    public void testMoveThreadCountFixedNegative() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount("-6");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> solverConfig.resolveMoveThreadCount());
    }

    @Test
    public void testMoveThreadCountNone() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setMoveThreadCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
        assertThat(solverConfig.resolveMoveThreadCount()).isNull();
    }
}

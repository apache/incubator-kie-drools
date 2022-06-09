package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class TimeMillisSpentTerminationTest {

    @Test
    void solveTermination() {
        Termination<TestdataSolution> termination = new TimeMillisSpentTermination<>(1000L);
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);

        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(0L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));
        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(100L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.1, offset(0.0));
        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(500L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, offset(0.0));
        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(700L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.7, offset(0.0));
        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(1000L);
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));
        when(solverScope.calculateTimeMillisSpentUpToNow()).thenReturn(1200L);
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));
    }

    @Test
    void phaseTermination() {
        Termination<TestdataSolution> termination = new TimeMillisSpentTermination<>(1000L);
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(AbstractPhaseScope.class);

        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(0L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));
        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(100L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.1, offset(0.0));
        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));
        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(700L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.7, offset(0.0));
        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(1000L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
        when(phaseScope.calculatePhaseTimeMillisSpentUpToNow()).thenReturn(1200L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
    }
}

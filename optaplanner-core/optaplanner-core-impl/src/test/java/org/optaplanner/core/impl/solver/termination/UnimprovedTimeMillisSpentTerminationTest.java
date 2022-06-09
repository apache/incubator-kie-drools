package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.*;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class UnimprovedTimeMillisSpentTerminationTest {

    @Test
    void forNegativeUnimprovedTimeMillis_exceptionIsThrown() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UnimprovedTimeMillisSpentTermination<>(-1L))
                .withMessageContaining("cannot be negative");
    }

    @Test
    void solverTermination() {
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        Clock clock = mock(Clock.class);

        Termination<TestdataSolution> termination = new UnimprovedTimeMillisSpentTermination<>(1000L, clock);

        when(clock.millis()).thenReturn(1000L);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(500L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, withPrecision(0.0));

        when(clock.millis()).thenReturn(2000L);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(1000L);
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, withPrecision(0.0));
    }

    @Test
    void phaseTermination() {
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(AbstractPhaseScope.class);
        Clock clock = mock(Clock.class);

        Termination<TestdataSolution> termination = new UnimprovedTimeMillisSpentTermination<>(1000L, clock);

        when(clock.millis()).thenReturn(1000L);
        when(phaseScope.getPhaseBestSolutionTimeMillis()).thenReturn(500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, withPrecision(0.0));

        when(clock.millis()).thenReturn(2000L);
        when(phaseScope.getPhaseBestSolutionTimeMillis()).thenReturn(1000L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, withPrecision(0.0));
    }
}

package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.*;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class UnimprovedTimeMillisSpentScoreDifferenceThresholdTerminationTest {

    private static final long START_TIME_MILLIS = 0L;

    @Test
    void forNegativeUnimprovedTimeMillis_exceptionIsThrown() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<>(
                        -1L,
                        SimpleScore.of(0)))
                .withMessageContaining("cannot be negative");
    }

    @Test
    void scoreImproves_terminationIsPostponed() {
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(LocalSearchPhaseScope.class);
        AbstractStepScope<TestdataSolution> stepScope = mock(LocalSearchStepScope.class);
        Clock clock = mock(Clock.class);

        Termination<TestdataSolution> termination = new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<>(
                1000L,
                SimpleScore.of(7),
                clock);
        doReturn(solverScope).when(phaseScope).getSolverScope();
        doReturn(phaseScope).when(stepScope).getPhaseScope();

        // first step
        when(clock.millis()).thenReturn(START_TIME_MILLIS);
        when(phaseScope.getStartingSystemTimeMillis()).thenReturn(START_TIME_MILLIS);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(START_TIME_MILLIS);
        when(stepScope.getBestScoreImproved()).thenReturn(Boolean.TRUE);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.of(0));

        termination.solvingStarted(solverScope);
        termination.phaseStarted(phaseScope);
        termination.stepEnded(stepScope);

        // time has not yet run out
        when(clock.millis()).thenReturn(START_TIME_MILLIS + 500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, withPrecision(0.0));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, withPrecision(0.0));

        // second step - score has improved beyond the threshold => termination is postponed by another second
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(START_TIME_MILLIS + 500L);
        when(stepScope.getBestScoreImproved()).thenReturn(Boolean.TRUE);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.of(10));

        termination.stepEnded(stepScope);

        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, withPrecision(0.0));
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, withPrecision(0.0));

        when(clock.millis()).thenReturn(START_TIME_MILLIS + 1500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, withPrecision(0.0));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, withPrecision(0.0));

        when(clock.millis()).thenReturn(START_TIME_MILLIS + 1501L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
    }

    @Test
    void scoreImprovesTooLate_terminates() {
        SolverScope<TestdataSolution> solverScope = mock(SolverScope.class);
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(LocalSearchPhaseScope.class);
        AbstractStepScope<TestdataSolution> stepScope = mock(LocalSearchStepScope.class);
        Clock clock = mock(Clock.class);

        Termination<TestdataSolution> termination = new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination<>(
                1000L,
                SimpleScore.of(7),
                clock);
        doReturn(solverScope).when(phaseScope).getSolverScope();
        doReturn(phaseScope).when(stepScope).getPhaseScope();

        // first step
        when(clock.millis()).thenReturn(START_TIME_MILLIS);
        when(phaseScope.getStartingSystemTimeMillis()).thenReturn(START_TIME_MILLIS);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(START_TIME_MILLIS);
        when(stepScope.getBestScoreImproved()).thenReturn(Boolean.TRUE);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.of(0));

        termination.solvingStarted(solverScope);
        termination.phaseStarted(phaseScope);
        termination.stepEnded(stepScope);

        // time has not yet run out
        when(clock.millis()).thenReturn(START_TIME_MILLIS + 500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, withPrecision(0.0));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, withPrecision(0.0));

        // second step - score has improved, but not beyond the threshold
        when(clock.millis()).thenReturn(START_TIME_MILLIS + 1000L);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(START_TIME_MILLIS + 1000L);
        when(stepScope.getBestScoreImproved()).thenReturn(Boolean.TRUE);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.of(5));
        termination.stepEnded(stepScope);

        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, withPrecision(0.0));
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, withPrecision(0.0));

        // third step - score has improved beyond the threshold, but too late
        when(clock.millis()).thenReturn(START_TIME_MILLIS + 1001L);
        when(solverScope.getBestSolutionTimeMillis()).thenReturn(START_TIME_MILLIS + 1001L);
        when(stepScope.getBestScoreImproved()).thenReturn(Boolean.TRUE);
        when(solverScope.getBestScore()).thenReturn(SimpleScore.of(10));
        termination.stepEnded(stepScope);

        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
    }
}

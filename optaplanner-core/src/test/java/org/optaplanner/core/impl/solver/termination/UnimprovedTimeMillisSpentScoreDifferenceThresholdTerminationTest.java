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

package org.optaplanner.core.impl.solver.termination;

import java.time.Clock;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnimprovedTimeMillisSpentScoreDifferenceThresholdTerminationTest {

    private static final long START_TIME_MILLIS = 0L;

    @Test
    public void forNegativeUnimprovedTimeMillis_exceptionIsThrown() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
                        -1L,
                        SimpleScore.of(0)))
                .withMessageContaining("cannot be negative");
    }

    @Test
    public void scoreImproves_terminationIsPostponed() {
        DefaultSolverScope<?> solverScope = mock(DefaultSolverScope.class);
        AbstractPhaseScope<?> phaseScope = mock(LocalSearchPhaseScope.class);
        AbstractStepScope<?> stepScope = mock(LocalSearchStepScope.class);
        Clock clock = mock(Clock.class);

        Termination termination = new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
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
    public void scoreImprovesTooLate_terminates() {
        DefaultSolverScope<?> solverScope = mock(DefaultSolverScope.class);
        AbstractPhaseScope<?> phaseScope = mock(LocalSearchPhaseScope.class);
        AbstractStepScope<?> stepScope = mock(LocalSearchStepScope.class);
        Clock clock = mock(Clock.class);

        Termination termination = new UnimprovedTimeMillisSpentScoreDifferenceThresholdTermination(
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

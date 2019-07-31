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
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.withPrecision;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnimprovedTimeMillisSpentTerminationTest {

    @Test
    public void forNegativeUnimprovedTimeMillis_exceptionIsThrown() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new UnimprovedTimeMillisSpentTermination(-1L))
                .withMessageContaining("cannot be negative");
    }

    @Test
    public void solverTermination() {
        DefaultSolverScope<?> solverScope = mock(DefaultSolverScope.class);
        Clock clock = mock(Clock.class);

        Termination termination = new UnimprovedTimeMillisSpentTermination(1000L, clock);

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
    public void phaseTermination() {
        AbstractPhaseScope<?> phaseScope = mock(AbstractPhaseScope.class);
        Clock clock = mock(Clock.class);

        Termination termination = new UnimprovedTimeMillisSpentTermination(1000L, clock);

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

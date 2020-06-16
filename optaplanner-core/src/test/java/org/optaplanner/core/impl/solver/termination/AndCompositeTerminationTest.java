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

package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class AndCompositeTerminationTest {

    @Test
    public void solveTermination() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(termination1, termination2);

        SolverScope solverScope = mock(SolverScope.class);

        when(termination1.isSolverTerminated(solverScope)).thenReturn(false);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(false);
        assertThat(compositeTermination.isSolverTerminated(solverScope)).isFalse();

        when(termination1.isSolverTerminated(solverScope)).thenReturn(true);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(false);
        assertThat(compositeTermination.isSolverTerminated(solverScope)).isFalse();

        when(termination1.isSolverTerminated(solverScope)).thenReturn(false);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(true);
        assertThat(compositeTermination.isSolverTerminated(solverScope)).isFalse();

        when(termination1.isSolverTerminated(solverScope)).thenReturn(true);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(true);
        assertThat(compositeTermination.isSolverTerminated(solverScope)).isTrue();
    }

    @Test
    public void phaseTermination() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(Arrays.asList(termination1, termination2));

        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(false);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(false);
        assertThat(compositeTermination.isPhaseTerminated(phaseScope)).isFalse();

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(true);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(false);
        assertThat(compositeTermination.isPhaseTerminated(phaseScope)).isFalse();

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(false);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(true);
        assertThat(compositeTermination.isPhaseTerminated(phaseScope)).isFalse();

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(true);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(true);
        assertThat(compositeTermination.isPhaseTerminated(phaseScope)).isTrue();
    }

    @Test
    public void calculateSolverTimeGradientTest() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(Arrays.asList(termination1, termination2));

        SolverScope solverScope = mock(SolverScope.class);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        // min(0.0,0.0) = 0.0
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        // min(0.5,0.0) = 0.0
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        // min(0.0,0.5) = 0.0
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(unsupported,unsupported) = 1.0 (default)
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(0.5,unsupported) = 0.5
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, offset(0.0));

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        // Negative time gradient values are unsupported and ignored, min(unsupported,0.5) = 0.5
        assertThat(compositeTermination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, offset(0.0));
    }

    @Test
    public void calculatePhaseTimeGradientTest() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(Arrays.asList(termination1, termination2));

        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        // min(0.0,0.0) = 0.0
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        // min(0.5,0.0) = 0.0
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        // min(0.0,0.5) = 0.0
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(unsupported,unsupported) = 1.0 (default)
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(0.5,unsupported) = 0.5
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        // Negative time gradient values are unsupported and ignored, min(unsupported,0.5) = 0.5
        assertThat(compositeTermination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));
    }
}

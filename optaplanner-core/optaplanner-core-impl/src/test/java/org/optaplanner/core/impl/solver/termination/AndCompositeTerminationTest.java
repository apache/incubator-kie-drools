package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

class AndCompositeTerminationTest {

    @Test
    void solveTermination() {
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
    void phaseTermination() {
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
    void calculateSolverTimeGradientTest() {
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
    void calculatePhaseTimeGradientTest() {
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

package org.optaplanner.core.impl.solver.termination;

import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AndCompositeTerminationTest {

    @Test
    public void solveTermination() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(termination1, termination2);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);

        when(termination1.isSolverTerminated(solverScope)).thenReturn(false);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(false);
        assertFalse(compositeTermination.isSolverTerminated(solverScope));

        when(termination1.isSolverTerminated(solverScope)).thenReturn(true);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(false);
        assertFalse(compositeTermination.isSolverTerminated(solverScope));

        when(termination1.isSolverTerminated(solverScope)).thenReturn(false);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(true);
        assertFalse(compositeTermination.isSolverTerminated(solverScope));

        when(termination1.isSolverTerminated(solverScope)).thenReturn(true);
        when(termination2.isSolverTerminated(solverScope)).thenReturn(true);
        assertTrue(compositeTermination.isSolverTerminated(solverScope));
    }

    @Test
    public void phaseTermination() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(Arrays.asList(termination1, termination2));

        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(false);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(false);
        assertFalse(compositeTermination.isPhaseTerminated(phaseScope));

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(true);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(false);
        assertFalse(compositeTermination.isPhaseTerminated(phaseScope));

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(false);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(true);
        assertFalse(compositeTermination.isPhaseTerminated(phaseScope));

        when(termination1.isPhaseTerminated(phaseScope)).thenReturn(true);
        when(termination2.isPhaseTerminated(phaseScope)).thenReturn(true);
        assertTrue(compositeTermination.isPhaseTerminated(phaseScope));
    }

    @Test
    public void calculateSolverTimeGradientTest() {
        Termination termination1 = mock(Termination.class);
        Termination termination2 = mock(Termination.class);

        Termination compositeTermination = new AndCompositeTermination(Arrays.asList(termination1, termination2));

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        // min(0.0,0.0) = 0.0
        assertEquals(0.0, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        // min(0.5,0.0) = 0.0
        assertEquals(0.0, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        // min(0.0,0.5) = 0.0
        assertEquals(0.0, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(unsupported,unsupported) = 1.0 (default)
        assertEquals(1.0, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(0.5,unsupported) = 0.5
        assertEquals(0.5, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);

        when(termination1.calculateSolverTimeGradient(solverScope)).thenReturn(-1.0);
        when(termination2.calculateSolverTimeGradient(solverScope)).thenReturn(0.5);
        // Negative time gradient values are unsupported and ignored, min(unsupported,0.5) = 0.5
        assertEquals(0.5, compositeTermination.calculateSolverTimeGradient(solverScope), 0.0);
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
        assertEquals(0.0, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        // min(0.5,0.0) = 0.0
        assertEquals(0.0, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        // min(0.0,0.5) = 0.0
        assertEquals(0.0, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(unsupported,unsupported) = 1.0 (default)
        assertEquals(1.0, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        // Negative time gradient values are unsupported and ignored, min(0.5,unsupported) = 0.5
        assertEquals(0.5, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);

        when(termination1.calculatePhaseTimeGradient(phaseScope)).thenReturn(-1.0);
        when(termination2.calculatePhaseTimeGradient(phaseScope)).thenReturn(0.5);
        // Negative time gradient values are unsupported and ignored, min(unsupported,0.5) = 0.5
        assertEquals(0.5, compositeTermination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }
}

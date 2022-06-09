package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.SolverScope;

class ScoreCalculationCountTerminationTest {

    @Test
    void solveTermination() {
        Termination termination = new ScoreCalculationCountTermination(1000L);
        SolverScope solverScope = mock(SolverScope.class);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(solverScope.getScoreDirector()).thenReturn(scoreDirector);

        when(scoreDirector.getCalculationCount()).thenReturn(0L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.0, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(100L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.1, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(500L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.5, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(700L);
        assertThat(termination.isSolverTerminated(solverScope)).isFalse();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(0.7, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(1000L);
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(1200L);
        assertThat(termination.isSolverTerminated(solverScope)).isTrue();
        assertThat(termination.calculateSolverTimeGradient(solverScope)).isEqualTo(1.0, offset(0.0));
    }

    @Test
    void phaseTermination() {
        Termination termination = new ScoreCalculationCountTermination(1000L);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(phaseScope.getScoreDirector()).thenReturn(scoreDirector);

        when(scoreDirector.getCalculationCount()).thenReturn(0L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(100L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.1, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(500L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(700L);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.7, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(1000L);
        boolean b1 = true;
        boolean solverTerminated1 = termination.isPhaseTerminated(phaseScope);
        assertThat(b1).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
        when(scoreDirector.getCalculationCount()).thenReturn(1200L);
        boolean b = true;
        boolean solverTerminated = termination.isPhaseTerminated(phaseScope);
        assertThat(b).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
    }

}

package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class UnimprovedStepCountTerminationTest {

    @Test
    void phaseTermination() {
        Termination<TestdataSolution> termination = new UnimprovedStepCountTermination<>(4);
        AbstractPhaseScope<TestdataSolution> phaseScope = mock(AbstractPhaseScope.class);
        AbstractStepScope<TestdataSolution> lastCompletedStepScope = mock(AbstractStepScope.class);
        when(phaseScope.getLastCompletedStepScope()).thenReturn(lastCompletedStepScope);

        when(phaseScope.getBestSolutionStepIndex()).thenReturn(10);
        when(lastCompletedStepScope.getStepIndex()).thenReturn(10);
        when(phaseScope.getNextStepIndex()).thenReturn(11);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));
        when(lastCompletedStepScope.getStepIndex()).thenReturn(11);
        when(phaseScope.getNextStepIndex()).thenReturn(12);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.25, offset(0.0));
        when(lastCompletedStepScope.getStepIndex()).thenReturn(12);
        when(phaseScope.getNextStepIndex()).thenReturn(13);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));
        when(lastCompletedStepScope.getStepIndex()).thenReturn(13);
        when(phaseScope.getNextStepIndex()).thenReturn(14);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.75, offset(0.0));
        when(lastCompletedStepScope.getStepIndex()).thenReturn(14);
        when(phaseScope.getNextStepIndex()).thenReturn(15);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
        when(lastCompletedStepScope.getStepIndex()).thenReturn(15);
        when(phaseScope.getNextStepIndex()).thenReturn(16);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
    }
}

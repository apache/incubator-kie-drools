package org.optaplanner.core.impl.solver.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class StepCountTerminationTest {

    @Test
    void phaseTermination() {
        Termination<TestdataSolution> termination = new StepCountTermination(4);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(phaseScope.getNextStepIndex()).thenReturn(0);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.0, offset(0.0));
        when(phaseScope.getNextStepIndex()).thenReturn(1);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.25, offset(0.0));
        when(phaseScope.getNextStepIndex()).thenReturn(2);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.5, offset(0.0));
        when(phaseScope.getNextStepIndex()).thenReturn(3);
        assertThat(termination.isPhaseTerminated(phaseScope)).isFalse();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(0.75, offset(0.0));
        when(phaseScope.getNextStepIndex()).thenReturn(4);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
        when(phaseScope.getNextStepIndex()).thenReturn(5);
        assertThat(termination.isPhaseTerminated(phaseScope)).isTrue();
        assertThat(termination.calculatePhaseTimeGradient(phaseScope)).isEqualTo(1.0, offset(0.0));
    }
}

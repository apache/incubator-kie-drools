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

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;

public class UnimprovedStepCountTerminationTest {

    @Test
    public void phaseTermination() {
        Termination termination = new UnimprovedStepCountTermination(4);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);
        AbstractStepScope lastCompletedStepScope = mock(AbstractStepScope.class);
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

/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StepCountTerminationTest {

    @Test
    public void phaseTermination() {
        Termination termination = new StepCountTermination(4);
        AbstractPhaseScope phaseScope = mock(AbstractPhaseScope.class);

        when(phaseScope.getNextStepIndex()).thenReturn(0);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getNextStepIndex()).thenReturn(1);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.25, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getNextStepIndex()).thenReturn(2);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.5, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getNextStepIndex()).thenReturn(3);
        assertEquals(false, termination.isPhaseTerminated(phaseScope));
        assertEquals(0.75, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getNextStepIndex()).thenReturn(4);
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
        when(phaseScope.getNextStepIndex()).thenReturn(5);
        assertEquals(true, termination.isPhaseTerminated(phaseScope));
        assertEquals(1.0, termination.calculatePhaseTimeGradient(phaseScope), 0.0);
    }

}

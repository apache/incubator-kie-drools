/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import org.junit.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ValueRatioTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(mock(DefaultSolverScope.class));
        when(phaseScope.getWorkingValueCount()).thenReturn(100);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertEquals(10, new ValueRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope));
        assertEquals(50, new ValueRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope));
        // Rounding
        assertEquals(11, new ValueRatioTabuSizeStrategy(0.1051).determineTabuSize(stepScope));
        assertEquals(10, new ValueRatioTabuSizeStrategy(0.1049).determineTabuSize(stepScope));
        // Corner cases
        assertEquals(1, new ValueRatioTabuSizeStrategy(0.0000001).determineTabuSize(stepScope));
        assertEquals(99, new ValueRatioTabuSizeStrategy(0.9999999).determineTabuSize(stepScope));
    }

}

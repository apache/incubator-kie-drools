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

package org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class EntityRatioTabuSizeStrategyTest {

    @Test
    public void tabuSize() {
        LocalSearchPhaseScope phaseScope = new LocalSearchPhaseScope(mock(SolverScope.class));
        when(phaseScope.getWorkingEntityCount()).thenReturn(100);
        LocalSearchStepScope stepScope = new LocalSearchStepScope(phaseScope);
        assertThat(new EntityRatioTabuSizeStrategy(0.1).determineTabuSize(stepScope)).isEqualTo(10);
        assertThat(new EntityRatioTabuSizeStrategy(0.5).determineTabuSize(stepScope)).isEqualTo(50);
        // Rounding
        assertThat(new EntityRatioTabuSizeStrategy(0.1051).determineTabuSize(stepScope)).isEqualTo(11);
        assertThat(new EntityRatioTabuSizeStrategy(0.1049).determineTabuSize(stepScope)).isEqualTo(10);
        // Corner cases
        assertThat(new EntityRatioTabuSizeStrategy(0.0000001).determineTabuSize(stepScope)).isEqualTo(1);
        assertThat(new EntityRatioTabuSizeStrategy(0.9999999).determineTabuSize(stepScope)).isEqualTo(99);
    }

}

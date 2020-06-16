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

package org.optaplanner.core.impl.heuristic.selector.move.decorator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfMoveSelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

public class ShufflingMoveSelectorTest {

    @Test
    public void cacheTypeSolver() {
        run(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void cacheTypePhase() {
        run(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void cacheTypeStep() {
        run(SelectionCacheType.STEP, 3);
    }

    public void run(SelectionCacheType cacheType, int timesCalled) {
        MoveSelector childMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3"));

        ShufflingMoveSelector moveSelector = new ShufflingMoveSelector(childMoveSelector, cacheType);
        verify(childMoveSelector, times(1)).isNeverEnding();

        Random workingRandom = mock(Random.class);

        SolverScope solverScope = mock(SolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(2);
        when(workingRandom.nextInt(2)).thenReturn(0);
        moveSelector.stepStarted(stepScopeA1);
        assertAllCodesOfMoveSelector(moveSelector, "a2", "a1", "a3");
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(0);
        when(workingRandom.nextInt(2)).thenReturn(1);
        moveSelector.stepStarted(stepScopeA2);
        if (cacheType.compareTo(SelectionCacheType.STEP) > 0) {
            // From a1, a2, a3
            assertAllCodesOfMoveSelector(moveSelector, "a3", "a1", "a2");
        } else {
            // Reset from a1, a2, a3
            assertAllCodesOfMoveSelector(moveSelector, "a3", "a2", "a1");
        }
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getWorkingRandom()).thenReturn(workingRandom);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getWorkingRandom()).thenReturn(workingRandom);
        when(workingRandom.nextInt(3)).thenReturn(1);
        when(workingRandom.nextInt(2)).thenReturn(0);
        moveSelector.stepStarted(stepScopeB1);
        if (cacheType.compareTo(SelectionCacheType.PHASE) > 0) {
            // From a3, a1, a2
            assertAllCodesOfMoveSelector(moveSelector, "a2", "a3", "a1");
        } else {
            // Reset from a1, a2, a3
            assertAllCodesOfMoveSelector(moveSelector, "a3", "a1", "a2");
        }
        moveSelector.stepEnded(stepScopeB1);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelector, 1, 2, 3);
        verify(childMoveSelector, times(timesCalled)).iterator();
        verify(childMoveSelector, times(timesCalled)).getSize();
    }

}

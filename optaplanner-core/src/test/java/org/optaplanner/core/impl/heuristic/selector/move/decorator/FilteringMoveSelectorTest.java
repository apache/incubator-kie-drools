/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class FilteringMoveSelectorTest {

    @Test
    public void filterCacheTypeSolver() {
        filter(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void filterCacheTypePhase() {
        filter(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void filterCacheTypeStep() {
        filter(SelectionCacheType.STEP, 5);
    }

    @Test
    public void filterCacheTypeJustInTime() {
        filter(SelectionCacheType.JUST_IN_TIME, 5);
    }

    public void filter(SelectionCacheType cacheType, int timesCalled) {
        MoveSelector childMoveSelector = SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3"), new DummyMove("a4"));

        SelectionFilter<TestdataSolution, DummyMove> filter = (scoreDirector, move) -> !move.getCode().equals("a3");
        List<SelectionFilter> filterList = Arrays.<SelectionFilter>asList(filter);
        MoveSelector moveSelector = new FilteringMoveSelector(childMoveSelector, filterList);
        if (cacheType.isCached()) {
            moveSelector = new CachingMoveSelector(moveSelector, cacheType, false);
        }

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);
        assertAllCodesOfMoveSelector(moveSelector, (cacheType.isNotCached() ? 4L : 3L), "a1", "a2", "a4");
        moveSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA2);
        assertAllCodesOfMoveSelector(moveSelector, (cacheType.isNotCached() ? 4L : 3L), "a1", "a2", "a4");
        moveSelector.stepEnded(stepScopeA2);

        moveSelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB1);
        assertAllCodesOfMoveSelector(moveSelector, (cacheType.isNotCached() ? 4L : 3L), "a1", "a2", "a4");
        moveSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB2);
        assertAllCodesOfMoveSelector(moveSelector, (cacheType.isNotCached() ? 4L : 3L), "a1", "a2", "a4");
        moveSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        moveSelector.stepStarted(stepScopeB3);
        assertAllCodesOfMoveSelector(moveSelector, (cacheType.isNotCached() ? 4L : 3L), "a1", "a2", "a4");
        moveSelector.stepEnded(stepScopeB3);

        moveSelector.phaseEnded(phaseScopeB);

        moveSelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childMoveSelector, 1, 2, 5);
        verify(childMoveSelector, times(timesCalled)).iterator();
        verify(childMoveSelector, times(timesCalled)).getSize();
    }

}

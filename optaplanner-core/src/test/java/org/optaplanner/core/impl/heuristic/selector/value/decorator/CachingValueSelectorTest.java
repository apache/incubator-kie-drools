/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Iterator;

import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.junit.Test;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CachingValueSelectorTest {

    @Test
    public void originalSelectionCacheTypeSolver() {
        runOriginalSelection(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void originalSelectionCacheTypePhase() {
        runOriginalSelection(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void originalSelectionCacheTypeStep() {
        runOriginalSelection(SelectionCacheType.STEP, 5);
    }

    public void runOriginalSelection(SelectionCacheType cacheType, int timesCalled) {
        EntityIndependentValueSelector childValueSelector = SelectorTestUtils.mockEntityIndependentValueSelector(
                TestdataEntity.class, "value",
                new TestdataValue("e1"), new TestdataValue("e2"), new TestdataValue("e3"));

        CachingValueSelector valueSelector = new CachingValueSelector(childValueSelector, cacheType, false);
        verify(childValueSelector, times(1)).isNeverEnding();

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        valueSelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA1);
        runAsserts(valueSelector);
        valueSelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        valueSelector.stepStarted(stepScopeA2);
        runAsserts(valueSelector);
        valueSelector.stepEnded(stepScopeA2);

        valueSelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        valueSelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB1);
        runAsserts(valueSelector);
        valueSelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB2);
        runAsserts(valueSelector);
        valueSelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        valueSelector.stepStarted(stepScopeB3);
        runAsserts(valueSelector);
        valueSelector.stepEnded(stepScopeB3);

        valueSelector.phaseEnded(phaseScopeB);

        valueSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childValueSelector, 1, 2, 5);
        verify(childValueSelector, times(timesCalled)).iterator();
        verify(childValueSelector, times(timesCalled)).getSize();
    }

    private void runAsserts(CachingValueSelector valueSelector) {
        Iterator<Object> iterator = valueSelector.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertCode("e1", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("e2", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("e3", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(false, valueSelector.isContinuous());
        assertEquals(false, valueSelector.isNeverEnding());
        assertEquals(3L, valueSelector.getSize());
    }

}

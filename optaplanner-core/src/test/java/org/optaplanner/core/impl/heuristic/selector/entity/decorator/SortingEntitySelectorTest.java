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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.junit.Test;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class SortingEntitySelectorTest {

    @Test
    public void cacheTypeSolver() {
        runCacheType(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void cacheTypePhase() {
        runCacheType(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void cacheTypeStep() {
        runCacheType(SelectionCacheType.STEP, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cacheTypeJustInTime() {
        runCacheType(SelectionCacheType.JUST_IN_TIME, 5);
    }

    public void runCacheType(SelectionCacheType cacheType, int timesCalled) {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("jan"), new TestdataEntity("feb"), new TestdataEntity("mar"),
                new TestdataEntity("apr"), new TestdataEntity("may"), new TestdataEntity("jun"));

        SelectionSorter<TestdataEntity> sorter = new SelectionSorter<TestdataEntity>() {
            public void sort(ScoreDirector scoreDirector, List<TestdataEntity> selectionList) {
                Collections.sort(selectionList, new Comparator<TestdataEntity>() {
                    public int compare(TestdataEntity a, TestdataEntity b) {
                        return a.getCode().compareTo(b.getCode());
                    }
                });
            }
        };
        EntitySelector entitySelector = new SortingEntitySelector(childEntitySelector, cacheType, sorter);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA1);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA2);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB1);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB2);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB3);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childEntitySelector, 1, 2, 5);
        verify(childEntitySelector, times(timesCalled)).iterator();
        verify(childEntitySelector, times(timesCalled)).getSize();
    }

    private void runAsserts(EntitySelector entitySelector, SelectionCacheType cacheType) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertCode("apr", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("feb", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("jan", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("jun", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("mar", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("may", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(false, entitySelector.isContinuous());
        assertEquals(false, entitySelector.isNeverEnding());
        assertEquals(6L, entitySelector.getSize());
    }

}

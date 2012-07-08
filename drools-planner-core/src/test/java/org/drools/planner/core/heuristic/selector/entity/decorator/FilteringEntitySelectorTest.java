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

package org.drools.planner.core.heuristic.selector.entity.decorator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solver.DefaultSolverScope;
import org.drools.planner.core.testdata.domain.TestdataEntity;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.drools.planner.core.testdata.util.PlannerAssert.assertCode;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilteringEntitySelectorTest {

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

    @Test
    public void cacheTypeJustInTime() {
        runCacheType(SelectionCacheType.JUST_IN_TIME, 5);
    }

    public void runCacheType(SelectionCacheType cacheType, int timesCalled) {
        EntitySelector childEntitySelector = mock(EntitySelector.class);
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"));
        when(childEntitySelector.iterator()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return entityList.iterator();
            }
        });
        when(childEntitySelector.isContinuous()).thenReturn(false);
        when(childEntitySelector.isNeverEnding()).thenReturn(false);
        when(childEntitySelector.getSize()).thenReturn((long) entityList.size());

        SelectionFilter<TestdataEntity> entityFilter = new SelectionFilter<TestdataEntity>() {
            public boolean accept(ScoreDirector scoreDirector, TestdataEntity entity) {
                return !entity.getCode().equals("e3");
            }
        };
        EntitySelector entitySelector = cacheType == SelectionCacheType.JUST_IN_TIME
                ? new JustInTimeFilteringEntitySelector(childEntitySelector, cacheType, entityFilter)
                : new CachingFilteringEntitySelector(childEntitySelector, cacheType, entityFilter);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getSolverPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA1);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getSolverPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA2);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getSolverPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB1);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getSolverPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB2);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getSolverPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB3);
        runAsserts(entitySelector, cacheType);
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(childEntitySelector, times(1)).solvingStarted(solverScope);
        verify(childEntitySelector, times(2)).phaseStarted(Matchers.<AbstractSolverPhaseScope>any());
        verify(childEntitySelector, times(5)).stepStarted(Matchers.<AbstractStepScope>any());
        verify(childEntitySelector, times(5)).stepEnded(Matchers.<AbstractStepScope>any());
        verify(childEntitySelector, times(2)).phaseEnded(Matchers.<AbstractSolverPhaseScope>any());
        verify(childEntitySelector, times(1)).solvingEnded(solverScope);
        verify(childEntitySelector, times(timesCalled)).iterator();
        verify(childEntitySelector, times(timesCalled)).getSize();
    }

    private void runAsserts(EntitySelector entitySelector, SelectionCacheType cacheType) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertCode("e1", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("e2", iterator.next());
        assertTrue(iterator.hasNext());
        assertCode("e4", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(false, entitySelector.isContinuous());
        assertEquals(false, entitySelector.isNeverEnding());
        assertEquals((cacheType == SelectionCacheType.JUST_IN_TIME ? 4L : 3L), entitySelector.getSize());
    }

}

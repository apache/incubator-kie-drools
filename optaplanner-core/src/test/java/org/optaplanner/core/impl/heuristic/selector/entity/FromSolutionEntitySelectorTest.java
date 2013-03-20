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

package org.optaplanner.core.impl.heuristic.selector.entity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.junit.Test;

import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class FromSolutionEntitySelectorTest {

    @Test
    public void originalAndCacheTypeSolver() {
        runOriginalAndCacheType(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void originalAndCacheTypePhase() {
        runOriginalAndCacheType(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void originalAndCacheTypeStep() {
        runOriginalAndCacheType(SelectionCacheType.STEP, 5);
    }

    public void runOriginalAndCacheType(SelectionCacheType cacheType, int timesCalled) {
        TestdataSolution workingSolution = new TestdataSolution();
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(entityList);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, cacheType, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingSolution()).thenReturn(workingSolution);
        entitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA1);
        runOriginalAsserts(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA2);
        runOriginalAsserts(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB1);
        runOriginalAsserts(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB2);
        runOriginalAsserts(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB3);
        runOriginalAsserts(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(timesCalled)).extractEntities(workingSolution);
    }

    private void runOriginalAsserts(FromSolutionEntitySelector entitySelector, String... codes) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertNotNull(iterator);
        for (String code : codes) {
            assertTrue(iterator.hasNext());
            assertCode(code, iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertEquals(false, entitySelector.isContinuous());
        assertEquals(false, entitySelector.isNeverEnding());
        assertEquals(3L, entitySelector.getSize());
    }

    @Test
    public void randomAndCacheTypeSolver() {
        runRandomAndCacheType(SelectionCacheType.SOLVER, 1);
    }

    @Test
    public void randomAndCacheTypePhase() {
        runRandomAndCacheType(SelectionCacheType.PHASE, 2);
    }

    @Test
    public void randomAndCacheTypeStep() {
        runRandomAndCacheType(SelectionCacheType.STEP, 5);
    }

    public void runRandomAndCacheType(SelectionCacheType cacheType, int timesCalled) {
        TestdataSolution workingSolution = new TestdataSolution();
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(entityList);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, cacheType, true);

        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(3)).thenReturn(1, 0, 0, 2, 1, 2, 2, 1, 0);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingSolution()).thenReturn(workingSolution);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA1);
        runRandomAsserts(entitySelector, "e2", "e1", "e1", "e3");
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA2);
        runRandomAsserts(entitySelector, "e2", "e3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB1);
        runRandomAsserts(entitySelector, "e3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB2);
        runRandomAsserts(entitySelector, "e2");
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB3);
        runRandomAsserts(entitySelector, "e1");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(timesCalled)).extractEntities(workingSolution);
    }

    private void runRandomAsserts(FromSolutionEntitySelector entitySelector, String... codes) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertNotNull(iterator);
        for (String code : codes) {
            assertTrue(iterator.hasNext());
            assertCode(code, iterator.next());
        }
        assertTrue(iterator.hasNext());
        assertEquals(false, entitySelector.isContinuous());
        assertEquals(true, entitySelector.isNeverEnding());
        assertEquals(3L, entitySelector.getSize());
    }

}

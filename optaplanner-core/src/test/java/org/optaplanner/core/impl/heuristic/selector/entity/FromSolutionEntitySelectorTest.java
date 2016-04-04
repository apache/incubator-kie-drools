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

package org.optaplanner.core.impl.heuristic.selector.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class FromSolutionEntitySelectorTest {

    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypePhase() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.PHASE);
    }

    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypeStep() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.STEP);
    }

    @Test
    public void originalWithoutEntityListDirtyAndMinimumCacheTypeJustInTime() {
        runOriginalWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.JUST_IN_TIME);
    }

    public void runOriginalWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType cacheType) {
        TestdataSolution workingSolution = new TestdataSolution();
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(entityList);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, cacheType, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(2)).extractEntities(workingSolution);
    }

    @Test
    public void originalWithEntityListDirty() {
        TestdataSolution workingSolution = new TestdataSolution();
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeA1);

        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("f1"), new TestdataEntity("f2"), new TestdataEntity("f3")));
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(8L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(true);
        when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(false);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        assertAllCodesOfEntitySelector(entitySelector, "f1", "f2", "f3");
        entitySelector.stepEnded(stepScopeB2);

        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(9L);
        when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(true);
        when(scoreDirector.isWorkingEntityListDirty(9L)).thenReturn(false);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        assertAllCodesOfEntitySelector(entitySelector, "e1", "e2", "e3");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(4)).extractEntities(workingSolution);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypePhase() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.PHASE);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypeStep() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.STEP);
    }

    @Test
    public void randomWithoutEntityListDirtyAndMinimumCacheTypeJustInTime() {
        runRandomWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType.JUST_IN_TIME);
    }

    public void runRandomWithoutEntityListDirtyAndMinimumCacheType(SelectionCacheType cacheType) {
        TestdataSolution workingSolution = new TestdataSolution();
        final List<Object> entityList = Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"));
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(entityList);
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, cacheType, true);

        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(3)).thenReturn(1, 0, 0, 2, 1, 2, 2, 1, 0);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e2", "e1", "e1", "e3");
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e2", "e3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e2");
        entitySelector.stepEnded(stepScopeB2);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e1");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(2)).extractEntities(workingSolution);
    }

    @Test
    public void randomWithEntityListDirty() {
        TestdataSolution workingSolution = new TestdataSolution();
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);
        when(scoreDirector.getWorkingSolution()).thenReturn(workingSolution);
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(7L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(false);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);

        Random workingRandom = mock(Random.class);
        when(workingRandom.nextInt(3)).thenReturn(1, 0, 0, 2, 1, 2, 2, 1, 0);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        when(solverScope.getWorkingRandom()).thenReturn(workingRandom);
        entitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeA.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA1);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e2", "e1", "e1", "e3");
        entitySelector.stepEnded(stepScopeA1);

        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("f1"), new TestdataEntity("f2"), new TestdataEntity("f3")));
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(8L);
        when(scoreDirector.isWorkingEntityListDirty(7L)).thenReturn(true);
        when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(false);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        when(stepScopeA2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeA2);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f2", "f3");
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        when(phaseScopeB.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB1.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB1);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f3");
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB2.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB2);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "f2");
        entitySelector.stepEnded(stepScopeB2);

        when(entityDescriptor.extractEntities(workingSolution)).thenReturn(Arrays.<Object>asList(
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3")));
        when(scoreDirector.getWorkingEntityListRevision()).thenReturn(9L);
        when(scoreDirector.isWorkingEntityListDirty(8L)).thenReturn(true);
        when(scoreDirector.isWorkingEntityListDirty(9L)).thenReturn(false);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        when(stepScopeB3.getPhaseScope()).thenReturn(phaseScopeB);
        when(stepScopeB3.getScoreDirector()).thenReturn(scoreDirector);
        entitySelector.stepStarted(stepScopeB3);
        assertCodesOfNeverEndingOfEntitySelector(entitySelector, 3L, "e1");
        entitySelector.stepEnded(stepScopeB3);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verify(entityDescriptor, times(4)).extractEntities(workingSolution);
    }

    @Test(expected = IllegalStateException.class)
    public void listIteratorWithRandomSelection() {
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityClass()).thenReturn((Class) TestdataEntity.class);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        entitySelector.listIterator();
    }

    @Test(expected = IllegalStateException.class)
    public void indexedListIteratorWithRandomSelection() {
        EntityDescriptor entityDescriptor = mock(EntityDescriptor.class);
        when(entityDescriptor.getEntityClass()).thenReturn((Class) TestdataEntity.class);
        FromSolutionEntitySelector entitySelector = new FromSolutionEntitySelector(entityDescriptor, SelectionCacheType.JUST_IN_TIME, true);
        entitySelector.listIterator(0);
    }

}

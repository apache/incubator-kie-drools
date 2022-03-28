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

package org.optaplanner.core.impl.heuristic.selector.entity.decorator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfOrderedEntitySelector;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.verifyPhaseLifecycle;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class FilteringEntitySelectorTest {

    @Test
    void filterCacheTypeSolver() {
        filter(SelectionCacheType.SOLVER, 1, SelectionOrder.RANDOM);
    }

    @Test
    void filterCacheTypePhase() {
        filter(SelectionCacheType.PHASE, 2, SelectionOrder.RANDOM);
    }

    @Test
    void filterCacheTypeStep() {
        filter(SelectionCacheType.STEP, 5, SelectionOrder.RANDOM);
    }

    @Test
    void filterCacheTypeJustInTime() {
        filter(SelectionCacheType.JUST_IN_TIME, 5, SelectionOrder.RANDOM);
    }

    @Test
    void filterOrderedCacheTypeSolver() {
        filter(SelectionCacheType.JUST_IN_TIME, 5, SelectionOrder.ORIGINAL);
    }

    private void verifyStep(EntitySelector entitySelector, SelectionCacheType cacheType,
            AbstractPhaseScope phaseScope, AbstractStepScope stepScope, SelectionOrder selectionOrder) {
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        entitySelector.stepStarted(stepScope);
        if (selectionOrder == SelectionOrder.RANDOM) {
            assertAllCodesOfEntitySelector(entitySelector, (cacheType.isNotCached() ? 4L : 3L), "e1", "e2", "e4");
        }
        if (selectionOrder == SelectionOrder.ORIGINAL) {
            assertAllCodesOfOrderedEntitySelector(entitySelector, (cacheType.isNotCached() ? 4L : 3L), "e1", "e2", "e4");
        }
        entitySelector.stepEnded(stepScope);
    }

    private void filter(SelectionCacheType cacheType, int timesCalled, SelectionOrder selectionOrder) {
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(TestdataEntity.class,
                new TestdataEntity("e1"), new TestdataEntity("e2"), new TestdataEntity("e3"), new TestdataEntity("e4"));

        SelectionFilter<TestdataSolution, TestdataEntity> filter = (scoreDirector, entity) -> !entity.getCode().equals("e3");
        List<SelectionFilter> filterList = Arrays.asList(filter);
        EntitySelector entitySelector = new FilteringEntitySelector(childEntitySelector, filterList);
        if (cacheType.isCached()) {
            entitySelector = new CachingEntitySelector(entitySelector, cacheType, false);
        }

        SolverScope solverScope = mock(SolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractPhaseScope phaseScopeA = mock(AbstractPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        verifyStep(entitySelector, cacheType, phaseScopeA, stepScopeA1, selectionOrder);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        verifyStep(entitySelector, cacheType, phaseScopeA, stepScopeA2, selectionOrder);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractPhaseScope phaseScopeB = mock(AbstractPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        verifyStep(entitySelector, cacheType, phaseScopeB, stepScopeB1, selectionOrder);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        verifyStep(entitySelector, cacheType, phaseScopeB, stepScopeB2, selectionOrder);

        AbstractStepScope stepScopeB3 = mock(AbstractStepScope.class);
        verifyStep(entitySelector, cacheType, phaseScopeB, stepScopeB3, selectionOrder);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verifyPhaseLifecycle(childEntitySelector, 1, 2, 5);
        if (selectionOrder == SelectionOrder.RANDOM) {
            verify(childEntitySelector, times(timesCalled)).iterator();
            verify(childEntitySelector, times(timesCalled)).getSize();
        }
        if (selectionOrder == SelectionOrder.ORIGINAL) {
            verify(childEntitySelector, times(timesCalled)).listIterator();
            verify(childEntitySelector, times(timesCalled)).getSize();
        }
    }

}

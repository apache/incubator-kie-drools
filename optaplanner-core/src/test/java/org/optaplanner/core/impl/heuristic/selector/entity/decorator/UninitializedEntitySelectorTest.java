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

import java.util.Iterator;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.phase.AbstractSolverPhaseScope;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class UninitializedEntitySelectorTest {

    @Test
    public void originalSelection() {
        PlanningEntityDescriptor entityDescriptor = TestdataEntity.buildEntityDescriptor();
        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3", v1);
        TestdataEntity e4 = new TestdataEntity("e4");
        EntitySelector childEntitySelector = SelectorTestUtils.mockEntitySelector(entityDescriptor,
                e1, e2, e3, e4);

        EntitySelector entitySelector = new UninitializedEntitySelector(childEntitySelector,
                entityDescriptor.getVariableDescriptor("value"));

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        entitySelector.solvingStarted(solverScope);

        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeA);

        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA1);
        runAsserts(entitySelector, "e1", "e2", "e4");
        e1.setValue(v2);
        entitySelector.stepEnded(stepScopeA1);

        AbstractStepScope stepScopeA2 = mock(AbstractStepScope.class);
        when(stepScopeA2.getPhaseScope()).thenReturn(phaseScopeA);
        entitySelector.stepStarted(stepScopeA2);
        runAsserts(entitySelector, "e2", "e4");
        e3.setValue(null);
        entitySelector.stepEnded(stepScopeA2);

        entitySelector.phaseEnded(phaseScopeA);

        AbstractSolverPhaseScope phaseScopeB = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeB.getSolverScope()).thenReturn(solverScope);
        entitySelector.phaseStarted(phaseScopeB);

        AbstractStepScope stepScopeB1 = mock(AbstractStepScope.class);
        when(stepScopeB1.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB1);
        runAsserts(entitySelector, "e2", "e3", "e4");
        e4.setValue(v1);
        entitySelector.stepEnded(stepScopeB1);

        AbstractStepScope stepScopeB2 = mock(AbstractStepScope.class);
        when(stepScopeB2.getPhaseScope()).thenReturn(phaseScopeB);
        entitySelector.stepStarted(stepScopeB2);
        runAsserts(entitySelector, "e2", "e3");
        entitySelector.stepEnded(stepScopeB2);

        entitySelector.phaseEnded(phaseScopeB);

        entitySelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childEntitySelector, 1, 2, 4);
        verify(childEntitySelector, times(4)).iterator();
    }

    private void runAsserts(EntitySelector entitySelector, String... valueCodes) {
        Iterator<Object> iterator = entitySelector.iterator();
        assertNotNull(iterator);
        for (String valueCode : valueCodes) {
            assertTrue(iterator.hasNext());
            assertCode(valueCode, iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertEquals(false, entitySelector.isContinuous());
        assertEquals(false, entitySelector.isNeverEnding());
    }

}

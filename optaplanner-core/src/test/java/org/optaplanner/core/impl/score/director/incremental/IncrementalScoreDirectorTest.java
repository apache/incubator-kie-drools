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

package org.optaplanner.core.impl.score.director.incremental;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.shadow.TestdataShadowingChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IncrementalScoreDirectorTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void variableListener() {
        TestdataShadowingChainedAnchor a0 = new TestdataShadowingChainedAnchor("a0");
        TestdataShadowingChainedEntity a1 = new TestdataShadowingChainedEntity("a1", a0);
        a0.setNextEntity(a1);
        TestdataShadowingChainedEntity a2 = new TestdataShadowingChainedEntity("a2", a1);
        a1.setNextEntity(a2);
        TestdataShadowingChainedEntity a3 = new TestdataShadowingChainedEntity("a3", a2);
        a2.setNextEntity(a3);

        TestdataShadowingChainedAnchor b0 = new TestdataShadowingChainedAnchor("b0");
        TestdataShadowingChainedEntity b1 = new TestdataShadowingChainedEntity("b1", b0);
        b0.setNextEntity(b1);

        TestdataShadowingChainedSolution solution = new TestdataShadowingChainedSolution("solution");
        List<TestdataShadowingChainedAnchor> anchorList = Arrays.asList(a0, b0);
        solution.setChainedAnchorList(anchorList);
        List<TestdataShadowingChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        solution.setChainedEntityList(originalEntityList);

        SolutionDescriptor<TestdataShadowingChainedSolution> solutionDescriptor = TestdataShadowingChainedSolution.buildSolutionDescriptor();
        IncrementalScoreDirectorFactory<TestdataShadowingChainedSolution> scoreDirectorFactory = mock(IncrementalScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        IncrementalScoreCalculator<TestdataShadowingChainedSolution> incrementalScoreCalculator = mock(IncrementalScoreCalculator.class);
        IncrementalScoreDirector<TestdataShadowingChainedSolution> scoreDirector = new IncrementalScoreDirector<TestdataShadowingChainedSolution>(
                scoreDirectorFactory, false, false, incrementalScoreCalculator) {
            @Override
            public Score calculateScore() {
                return SimpleScore.of(-100);
            }
        };
        scoreDirector.setWorkingSolution(solution);
        reset(incrementalScoreCalculator);

        assertEquals(null, b1.getNextEntity());

        scoreDirector.beforeVariableChanged(a3, "chainedObject");
        a3.setChainedObject(b1);
        scoreDirector.afterVariableChanged(a3, "chainedObject");
        scoreDirector.triggerVariableListeners();
        assertEquals(a3, b1.getNextEntity());

        InOrder inOrder = inOrder(incrementalScoreCalculator);
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(b1, "nextEntity");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(b1, "nextEntity");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void illegalStateExceptionThrownWhenConstraintMatchNotEnabled() {
        IncrementalScoreDirector<Object> director
                = new IncrementalScoreDirector<>(mockIncrementalScoreDirectorFactory(), false, false,
                mockIncrementalScoreCalculator(false));
        director.setWorkingSolution(new Object());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("constraintMatchEnabled");
        director.getConstraintMatchTotals();
    }

    @Test
    public void constraintMatchTotalsNeverNull() {
        IncrementalScoreDirector<Object> director
                = new IncrementalScoreDirector<>(mockIncrementalScoreDirectorFactory(), false, true,
                mockIncrementalScoreCalculator(true));
        director.setWorkingSolution(new Object());
        assertNotNull(director.getConstraintMatchTotals());
        assertNotNull(director.getConstraintMatchTotalMap());
    }

    @Test
    public void constraintMatchIsNotEnabledWhenScoreCalculatorNotConstraintMatchAware() {
        IncrementalScoreDirector<Object> director
                = new IncrementalScoreDirector<>(mockIncrementalScoreDirectorFactory(), false, true,
                mockIncrementalScoreCalculator(false));
        assertFalse(director.isConstraintMatchEnabled());
    }

    @SuppressWarnings("unchecked")
    private IncrementalScoreDirectorFactory<Object> mockIncrementalScoreDirectorFactory() {
        IncrementalScoreDirectorFactory<Object> factory = mock(IncrementalScoreDirectorFactory.class);
        when(factory.getScoreDefinition()).thenReturn(new SimpleScoreDefinition());
        when(factory.getSolutionDescriptor()).thenReturn(mock(SolutionDescriptor.class));
        return factory;
    }

    @SuppressWarnings("unchecked")
    private IncrementalScoreCalculator<Object> mockIncrementalScoreCalculator(boolean constraintMatchAware) {
        return constraintMatchAware
                ? mock(ConstraintMatchAwareIncrementalScoreCalculator.class)
                : mock(IncrementalScoreCalculator.class);
    }

}

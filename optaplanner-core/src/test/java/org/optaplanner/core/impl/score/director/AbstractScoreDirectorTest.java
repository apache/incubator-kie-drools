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

package org.optaplanner.core.impl.score.director;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.domain.variable.shadow.ChainedMappedByVariableListener;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirector;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedObject;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class AbstractScoreDirectorTest {

    @Test
    public void getTrailingEntityMap() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        TestdataChainedSolution solution = new TestdataChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        SolutionDescriptor solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        PlanningEntityDescriptor entityDescriptor = solutionDescriptor.getEntityDescriptor(
                TestdataChainedEntity.class);
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getVariableDescriptor("chainedObject");

        AbstractScoreDirectorFactory scoreDirectorFactory = mock(AbstractScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        AbstractScoreDirector scoreDirector = new AbstractScoreDirector<AbstractScoreDirectorFactory>(
                scoreDirectorFactory) {
            public Score calculateScore() {
                return SimpleScore.valueOf(-100);
            }
        };
        FieldAccessingSolutionCloner<TestdataChainedSolution> cloner
                = new FieldAccessingSolutionCloner<TestdataChainedSolution>(solutionDescriptor);
        TestdataChainedSolution clonedStartingSolution = cloner.cloneSolution(solution);

        scoreDirector.setWorkingSolution(solution);
        assertEquals(a1, scoreDirector.getTrailingEntity(variableDescriptor, a0));
        assertEquals(a2, scoreDirector.getTrailingEntity(variableDescriptor, a1));
        assertEquals(a3, scoreDirector.getTrailingEntity(variableDescriptor, a2));
        assertEquals(b1, scoreDirector.getTrailingEntity(variableDescriptor, b0));
        
        scoreDirector.beforeVariableChanged(a3, "chainedObject");
        a3.setChainedObject(b1);
        scoreDirector.afterVariableChanged(a3, "chainedObject");
        assertEquals(a1, scoreDirector.getTrailingEntity(variableDescriptor, a0));
        assertEquals(a2, scoreDirector.getTrailingEntity(variableDescriptor, a1));
        assertEquals(b1, scoreDirector.getTrailingEntity(variableDescriptor, b0));
        assertEquals(a3, scoreDirector.getTrailingEntity(variableDescriptor, b1));

        scoreDirector.setWorkingSolution(clonedStartingSolution);
        TestdataChainedEntity a1Clone = clonedStartingSolution.getChainedEntityList().get(0);
        assertCode("a1", a1Clone);
        TestdataChainedEntity a2Clone = clonedStartingSolution.getChainedEntityList().get(1);
        assertCode("a2", a2Clone);
        assertEquals("a1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a0)).getCode());
        assertEquals("a2", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a1Clone)).getCode());
        assertEquals("a3", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, a2Clone)).getCode());
        assertEquals("b1", ((TestdataObject) scoreDirector.getTrailingEntity(variableDescriptor, b0)).getCode());
    }

    @Test
    public void variableListener() {
        TestdataMappedByChainedAnchor a0 = new TestdataMappedByChainedAnchor("a0");
        TestdataMappedByChainedEntity a1 = new TestdataMappedByChainedEntity("a1", a0);
        a0.setNextEntity(a1);
        TestdataMappedByChainedEntity a2 = new TestdataMappedByChainedEntity("a2", a1);
        a1.setNextEntity(a2);
        TestdataMappedByChainedEntity a3 = new TestdataMappedByChainedEntity("a3", a2);
        a2.setNextEntity(a3);

        TestdataMappedByChainedAnchor b0 = new TestdataMappedByChainedAnchor("b0");
        TestdataMappedByChainedEntity b1 = new TestdataMappedByChainedEntity("b1", b0);
        b0.setNextEntity(b1);

        TestdataMappedByChainedSolution solution = new TestdataMappedByChainedSolution("solution");
        List<TestdataMappedByChainedAnchor> anchorList = Arrays.asList(a0, b0);
        solution.setChainedAnchorList(anchorList);
        List<TestdataMappedByChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        solution.setChainedEntityList(originalEntityList);

        SolutionDescriptor solutionDescriptor = TestdataMappedByChainedSolution.buildSolutionDescriptor();
        IncrementalScoreDirectorFactory scoreDirectorFactory = mock(IncrementalScoreDirectorFactory.class);
        when(scoreDirectorFactory.getSolutionDescriptor()).thenReturn(solutionDescriptor);
        IncrementalScoreCalculator incrementalScoreCalculator = mock(IncrementalScoreCalculator.class);
        IncrementalScoreDirector scoreDirector = new IncrementalScoreDirector(
                scoreDirectorFactory, incrementalScoreCalculator) {
            public Score calculateScore() {
                return SimpleScore.valueOf(-100);
            }
        };
        scoreDirector.setWorkingSolution(solution);
        reset(incrementalScoreCalculator);

        assertEquals(null, b1.getNextEntity());

        scoreDirector.beforeVariableChanged(a3, "chainedObject");
        a3.setChainedObject(b1);
        scoreDirector.afterVariableChanged(a3, "chainedObject");
        assertEquals(a3, b1.getNextEntity());

        InOrder inOrder = inOrder(incrementalScoreCalculator);
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(b1, "nextEntity");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(b1, "nextEntity");
        inOrder.verifyNoMoreInteractions();
    }

}

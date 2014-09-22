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

package org.optaplanner.core.impl.score.director.incremental;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IncrementalScoreDirectorTest {

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
                scoreDirectorFactory, false, incrementalScoreCalculator) {
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

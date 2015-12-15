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

import org.junit.Test;
import org.mockito.InOrder;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IncrementalScoreDirectorTest {

    @Test
    public void variableListener() {
        TestdataRichChainedAnchor a0 = new TestdataRichChainedAnchor("a0");
        TestdataRichChainedEntity a1 = new TestdataRichChainedEntity("a1", a0);
        a0.setNextEntity(a1);
        TestdataRichChainedEntity a2 = new TestdataRichChainedEntity("a2", a1);
        a1.setNextEntity(a2);
        TestdataRichChainedEntity a3 = new TestdataRichChainedEntity("a3", a2);
        a2.setNextEntity(a3);

        TestdataRichChainedAnchor b0 = new TestdataRichChainedAnchor("b0");
        TestdataRichChainedEntity b1 = new TestdataRichChainedEntity("b1", b0);
        b0.setNextEntity(b1);

        TestdataRichChainedSolution solution = new TestdataRichChainedSolution("solution");
        List<TestdataRichChainedAnchor> anchorList = Arrays.asList(a0, b0);
        solution.setChainedAnchorList(anchorList);
        List<TestdataRichChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        solution.setChainedEntityList(originalEntityList);

        SolutionDescriptor solutionDescriptor = TestdataRichChainedSolution.buildSolutionDescriptor();
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
        scoreDirector.triggerVariableListeners();
        assertEquals(a3, b1.getNextEntity());

        InOrder inOrder = inOrder(incrementalScoreCalculator);
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(a3, "chainedObject");
        inOrder.verify(incrementalScoreCalculator, times(1)).beforeVariableChanged(b1, "nextEntity");
        inOrder.verify(incrementalScoreCalculator, times(1)).afterVariableChanged(b1, "nextEntity");
        inOrder.verifyNoMoreInteractions();
    }

}

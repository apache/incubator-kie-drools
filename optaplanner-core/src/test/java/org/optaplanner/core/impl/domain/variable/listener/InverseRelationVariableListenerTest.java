/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.listener;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedObject;
import org.optaplanner.core.impl.testdata.domain.chained.mappedby.TestdataMappedByChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InverseRelationVariableListenerTest {

    @Test
    public void chained() {
        SolutionDescriptor solutionDescriptor = TestdataMappedByChainedSolution.buildSolutionDescriptor();
        ShadowVariableDescriptor nextEntityVariableDescriptor
                = solutionDescriptor.findEntityDescriptorOrFail(TestdataMappedByChainedObject.class)
                .getShadowVariableDescriptor("nextEntity");
        InverseRelationVariableListener variableListener = new InverseRelationVariableListener(
                nextEntityVariableDescriptor,
                solutionDescriptor.findEntityDescriptorOrFail(TestdataMappedByChainedEntity.class)
                        .getGenuineVariableDescriptor("chainedObject"));
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

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

        assertEquals(null, b1.getNextEntity());

        variableListener.beforeVariableChanged(scoreDirector, a3);
        a3.setChainedObject(b1);
        variableListener.afterVariableChanged(scoreDirector, a3);
        assertEquals(a3, b1.getNextEntity());

        InOrder inOrder = inOrder(scoreDirector);
        inOrder.verify(scoreDirector).beforeVariableChanged(nextEntityVariableDescriptor, b1);
        inOrder.verify(scoreDirector).afterVariableChanged(nextEntityVariableDescriptor, b1);
        inOrder.verifyNoMoreInteractions();
    }

}

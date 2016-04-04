/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.anchor;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.InOrder;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.rich.TestdataRichChainedSolution;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AnchorVariableListenerTest {

    @Test
    public void chained() {
        SolutionDescriptor solutionDescriptor = TestdataRichChainedSolution.buildSolutionDescriptor();
        EntityDescriptor entityDescriptor = solutionDescriptor.findEntityDescriptorOrFail(TestdataRichChainedEntity.class);
        GenuineVariableDescriptor chainedObjectVariableDescriptor
                = entityDescriptor.getGenuineVariableDescriptor("chainedObject");
        ShadowVariableDescriptor nextEntityVariableDescriptor = entityDescriptor.getShadowVariableDescriptor("nextEntity");
        SingletonInverseVariableListener inverseVariableListener = new SingletonInverseVariableListener(
                (InverseRelationShadowVariableDescriptor) nextEntityVariableDescriptor,
                entityDescriptor.getGenuineVariableDescriptor("chainedObject"));
        ShadowVariableDescriptor anchorVariableDescriptor = entityDescriptor.getShadowVariableDescriptor("anchor");
        AnchorVariableListener variableListener = new AnchorVariableListener(
                (AnchorShadowVariableDescriptor) anchorVariableDescriptor,
                chainedObjectVariableDescriptor, inverseVariableListener);
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataRichChainedAnchor a0 = new TestdataRichChainedAnchor("a0");
        TestdataRichChainedEntity a1 = new TestdataRichChainedEntity("a1", a0);
        a1.setAnchor(a0);
        a0.setNextEntity(a1);
        TestdataRichChainedEntity a2 = new TestdataRichChainedEntity("a2", a1);
        a2.setAnchor(a0);
        a1.setNextEntity(a2);
        TestdataRichChainedEntity a3 = new TestdataRichChainedEntity("a3", a2);
        a3.setAnchor(a0);
        a2.setNextEntity(a3);

        TestdataRichChainedAnchor b0 = new TestdataRichChainedAnchor("b0");
        TestdataRichChainedEntity b1 = new TestdataRichChainedEntity("b1", b0);
        b1.setAnchor(b0);
        b0.setNextEntity(b1);

        TestdataRichChainedSolution solution = new TestdataRichChainedSolution("solution");
        solution.setChainedAnchorList(Arrays.asList(a0, b0));
        solution.setChainedEntityList(Arrays.asList(a1, a2, a3, b1));

        assertSame(a0, a1.getAnchor());
        assertSame(a0, a2.getAnchor());
        assertSame(a0, a3.getAnchor());
        assertSame(b0, b1.getAnchor());

        inverseVariableListener.beforeVariableChanged(scoreDirector, a3);
        variableListener.beforeVariableChanged(scoreDirector, a3);
        a3.setChainedObject(b1);
        inverseVariableListener.afterVariableChanged(scoreDirector, a3);
        variableListener.afterVariableChanged(scoreDirector, a3);

        assertSame(a0, a1.getAnchor());
        assertSame(a0, a2.getAnchor());
        assertSame(b0, a3.getAnchor());
        assertSame(b0, b1.getAnchor());

        InOrder inOrder = inOrder(scoreDirector);
        inOrder.verify(scoreDirector).beforeVariableChanged(anchorVariableDescriptor, a3);
        inOrder.verify(scoreDirector).afterVariableChanged(anchorVariableDescriptor, a3);
        inOrder.verifyNoMoreInteractions();
    }

}

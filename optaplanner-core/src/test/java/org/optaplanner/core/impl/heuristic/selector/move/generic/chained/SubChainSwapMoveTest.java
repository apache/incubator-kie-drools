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

package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;

import static org.mockito.Mockito.*;

public class SubChainSwapMoveTest {

    @Test
    public void noTrailing() {
        EntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("chainedObject");
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5, b1, b2, b3});

        SubChainSwapMove move = new SubChainSwapMove(variableDescriptor, new SubChain(Arrays.<Object>asList(a3, a4, a5)),
                new SubChain(Arrays.<Object>asList(b2, b3)));
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2, b2, b3);
        SelectorTestUtils.assertChain(b0, b1, a3, a4, a5);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector, atLeastOnce()).beforeVariableChanged(variableDescriptor, b2);
        verify(scoreDirector, atLeastOnce()).afterVariableChanged(variableDescriptor, b2);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, b3);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, b3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);
    }

    @Test
    public void oldAndNewTrailing() {
        EntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("chainedObject");
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedEntity b2 = new TestdataChainedEntity("b2", b1);
        TestdataChainedEntity b3 = new TestdataChainedEntity("b3", b2);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5, b1, b2, b3});

        SubChainSwapMove move = new SubChainSwapMove(variableDescriptor, new SubChain(Arrays.<Object>asList(a2, a3, a4)),
                new SubChain(Arrays.<Object>asList(b1, b2)));
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, b1, b2, a5);
        SelectorTestUtils.assertChain(b0, a2, a3, a4, b3);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a2);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, a3);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector, atLeastOnce()).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector, atLeastOnce()).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector, atLeastOnce()).beforeVariableChanged(variableDescriptor, b1);
        verify(scoreDirector, atLeastOnce()).afterVariableChanged(variableDescriptor, b1);
        verify(scoreDirector, never()).beforeVariableChanged(variableDescriptor, b2);
        verify(scoreDirector, never()).afterVariableChanged(variableDescriptor, b2);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, b3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, b3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1, b2, b3);
    }

}

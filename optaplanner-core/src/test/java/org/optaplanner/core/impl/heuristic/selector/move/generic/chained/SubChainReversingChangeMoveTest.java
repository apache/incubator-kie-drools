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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SubChainReversingChangeMoveTest {

    @Test
    public void noTrailing() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5, b1});

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a3, a4, a5)),
                variableDescriptor, b1);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2);
        SelectorTestUtils.assertChain(b0, b1, a5, a4, a3);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void noTrailingInPlace() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5});

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a3, a4, a5)),
                variableDescriptor, a2);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2, a5, a4, a3);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
    }

    @Test
    public void oldAndNewTrailing() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5, b1});

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a2, a3, a4)),
                variableDescriptor, b0);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a5);
        SelectorTestUtils.assertChain(b0, a4, a3, a2, b1);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, b1);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, b1);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void oldAndNewTrailingInPlace() {
        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, a4, a5});

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a2, a3, a4)),
                variableDescriptor, a1);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a4, a3, a2, a5);

        verify(scoreDirector, atLeast(1)).beforeVariableChanged(variableDescriptor, a5);
        verify(scoreDirector, atLeast(1)).afterVariableChanged(variableDescriptor, a5);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a4);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a2);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
    }

    @Test
    public void toStringTest() {
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

        GenuineVariableDescriptor variableDescriptor = TestdataChainedEntity.buildVariableDescriptorForChainedObject();

        assertEquals("[a2..a4] {a1 -reversing-> b0}", new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a2, a3, a4)), variableDescriptor, b0).toString());
        assertEquals("[a1..a5] {a0 -reversing-> b3}", new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a1, a2, a3, a4, a5)), variableDescriptor, b3).toString());
        assertEquals("[a1..a3] {a0 -reversing-> a5}", new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a1, a2, a3)), variableDescriptor, a5).toString());
        assertEquals("[a3..a3] {a2 -reversing-> b2}", new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a3)), variableDescriptor, b2).toString());
    }

}

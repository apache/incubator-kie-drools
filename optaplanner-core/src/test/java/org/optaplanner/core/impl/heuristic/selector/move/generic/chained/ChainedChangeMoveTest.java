package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import org.junit.Test;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;

import static org.mockito.Mockito.*;

public class ChainedChangeMoveTest {

    @Test
    public void noTrailing() {
        EntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor("chainedObject");
        InnerScoreDirector scoreDirector = mock(InnerScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, b1});

        ChainedChangeMove move = new ChainedChangeMove(a3, variableDescriptor, b1);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2);
        SelectorTestUtils.assertChain(b0, b1, a3);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
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

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, b1});

        ChainedChangeMove move = new ChainedChangeMove(a2, variableDescriptor, b0);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a3);
        SelectorTestUtils.assertChain(b0, a2, b1);

        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a2);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, a3);
        verify(scoreDirector).beforeVariableChanged(variableDescriptor, b1);
        verify(scoreDirector).afterVariableChanged(variableDescriptor, b1);

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

}

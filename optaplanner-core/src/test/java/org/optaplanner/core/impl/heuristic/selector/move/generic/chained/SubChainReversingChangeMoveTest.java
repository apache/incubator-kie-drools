package org.optaplanner.core.impl.heuristic.selector.move.generic.chained;

import java.util.Arrays;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.SelectorTestUtils;
import org.optaplanner.core.impl.heuristic.selector.value.chained.SubChain;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SubChainReversingChangeMoveTest {

    @Test
    public void noTrailing() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

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

        verify(scoreDirector).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void noTrailingInPlace() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

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

        verify(scoreDirector).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
    }

    @Test
    public void oldAndNewTrailing() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

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

        verify(scoreDirector).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a2, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a2, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(b1, "chainedObject");
        verify(scoreDirector).afterVariableChanged(b1, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void oldAndNewTrailingInPlace() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

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

        verify(scoreDirector, atLeast(1)).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector, atLeast(1)).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a2, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a2, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3, a4, a5);
    }

}

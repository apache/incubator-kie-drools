package org.drools.planner.core.heuristic.selector.move.generic.chained;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.value.chained.SubChain;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SubChainReversingChangeMoveTest {

    @Test
    public void noTrailing() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a0)).thenReturn(a1);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a1)).thenReturn(a2);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a3);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(a4);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a4)).thenReturn(a5);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b0)).thenReturn(b1);

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a3, a4, a5)),
                variableDescriptor, b1);
        move.doMove(scoreDirector);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a2.getChainedObject());

        assertEquals(b0, b1.getChainedObject());
        assertEquals(b1, a5.getChainedObject());
        assertEquals(a5, a4.getChainedObject());
        assertEquals(a4, a3.getChainedObject());

        verify(scoreDirector).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
    }

    @Test
    public void noTrailingInPlace() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a0)).thenReturn(a1);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a1)).thenReturn(a2);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a3);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(a4);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a4)).thenReturn(a5);

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a3, a4, a5)),
                variableDescriptor, a2);
        move.doMove(scoreDirector);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a2.getChainedObject());
        assertEquals(a2, a5.getChainedObject());
        assertEquals(a5, a4.getChainedObject());
        assertEquals(a4, a3.getChainedObject());

        verify(scoreDirector).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
    }

    @Test
    public void oldAndNewTrailing() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a0)).thenReturn(a1);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a1)).thenReturn(a2);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a3);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(a4);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a4)).thenReturn(a5);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, b0)).thenReturn(b1);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a2);
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a2, a3, a4)),
                variableDescriptor, b0);
        move.doMove(scoreDirector);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a5.getChainedObject());

        assertEquals(b0, a4.getChainedObject());
        assertEquals(a4, a3.getChainedObject());
        assertEquals(a3, a2.getChainedObject());
        assertEquals(a2, b1.getChainedObject());

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
    }

    @Test
    public void oldAndNewTrailingInPlace() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a0)).thenReturn(a1);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a1)).thenReturn(a2);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a2)).thenReturn(a3);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4", a3);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a3)).thenReturn(a4);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5", a4);
        when(scoreDirector.getTrailingEntity(variableDescriptor, a4)).thenReturn(a5);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a2);
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);

        SubChainReversingChangeMove move = new SubChainReversingChangeMove(
                new SubChain(Arrays.<Object>asList(a2, a3, a4)),
                variableDescriptor, a1);
        move.doMove(scoreDirector);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a4.getChainedObject());
        assertEquals(a4, a3.getChainedObject());
        assertEquals(a3, a2.getChainedObject());
        assertEquals(a2, a5.getChainedObject());

        verify(scoreDirector, atLeast(1)).beforeVariableChanged(a5, "chainedObject");
        verify(scoreDirector, atLeast(1)).afterVariableChanged(a5, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a4, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a4, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a2, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a2, "chainedObject");
    }

}

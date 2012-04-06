package org.drools.planner.core.move.generic;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GenericReverseChainedChangePartMoveTest {

    @Test
    public void noTrailing() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        a1.setChainedObject(a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        a2.setChainedObject(a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3");
        a3.setChainedObject(a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4");
        a4.setChainedObject(a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5");
        a5.setChainedObject(a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);
        entitiesSubChain.add(a5);

        GenericReverseChainedChangePartMove move = new GenericReverseChainedChangePartMove(entitiesSubChain,
                variableDescriptor, b1, null, null);
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
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        a1.setChainedObject(a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        a2.setChainedObject(a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3");
        a3.setChainedObject(a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4");
        a4.setChainedObject(a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5");
        a5.setChainedObject(a4);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);
        entitiesSubChain.add(a5);

        GenericReverseChainedChangePartMove move = new GenericReverseChainedChangePartMove(entitiesSubChain,
                variableDescriptor, a2, null, null);
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
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        a1.setChainedObject(a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        a2.setChainedObject(a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3");
        a3.setChainedObject(a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4");
        a4.setChainedObject(a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5");
        a5.setChainedObject(a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a2);
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);

        GenericReverseChainedChangePartMove move = new GenericReverseChainedChangePartMove(entitiesSubChain,
                variableDescriptor, b0, a5, b1);
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
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        a1.setChainedObject(a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        a2.setChainedObject(a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3");
        a3.setChainedObject(a2);
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4");
        a4.setChainedObject(a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5");
        a5.setChainedObject(a4);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a2);
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);

        GenericReverseChainedChangePartMove move = new GenericReverseChainedChangePartMove(entitiesSubChain,
                variableDescriptor, a1, a5, a2);
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

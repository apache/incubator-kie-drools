package org.drools.planner.core.move.generic;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GenericChainedChangeMoveTest {

    @Test
    public void noTrailing() {
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1");
        a1.setChainedObject(a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2");
        a2.setChainedObject(a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3");
        a3.setChainedObject(a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        WorkingMemory workingMemory = mock(WorkingMemory.class);
        FactHandle a3FactHandle = mock(FactHandle.class);

        GenericChainedChangeMove move = new GenericChainedChangeMove(a3, a3FactHandle,
                variableDescriptor, b1, null, null, null, null);
        move.doMove(workingMemory);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a2.getChainedObject());

        assertEquals(b0, b1.getChainedObject());
        assertEquals(b1, a3.getChainedObject());

        verify(workingMemory).update(a3FactHandle, a3);
        verifyNoMoreInteractions(workingMemory);
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

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        WorkingMemory workingMemory = mock(WorkingMemory.class);
        FactHandle a2FactHandle = mock(FactHandle.class);
        FactHandle a3FactHandle = mock(FactHandle.class);
        FactHandle b1FactHandle = mock(FactHandle.class);

        GenericChainedChangeMove move = new GenericChainedChangeMove(a2, a2FactHandle,
                variableDescriptor, b0, a3, a3FactHandle, b1, b1FactHandle);
        move.doMove(workingMemory);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a3.getChainedObject());

        assertEquals(b0, a2.getChainedObject());
        assertEquals(a2, b1.getChainedObject());

        verify(workingMemory).update(a2FactHandle, a2);
        verify(workingMemory).update(a3FactHandle, a3);
        verify(workingMemory).update(b1FactHandle, b1);
        verifyNoMoreInteractions(workingMemory);
    }

}

package org.drools.planner.core.move.generic;

import java.util.ArrayList;
import java.util.List;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GenericChainedChangePartMoveTest {

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
        WorkingMemory workingMemory = mock(WorkingMemory.class);
        FactHandle a3FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a3)).thenReturn(a3FactHandle);
        FactHandle a4FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a4)).thenReturn(a4FactHandle);
        FactHandle a5FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a5)).thenReturn(a5FactHandle);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);
        entitiesSubChain.add(a5);

        GenericChainedChangePartMove move = new GenericChainedChangePartMove(entitiesSubChain,
                variableDescriptor, b1, null, null, null, null);
        move.doMove(workingMemory);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a2.getChainedObject());

        assertEquals(b0, b1.getChainedObject());
        assertEquals(b1, a3.getChainedObject());
        assertEquals(a3, a4.getChainedObject());
        assertEquals(a4, a5.getChainedObject());

        verify(workingMemory).getFactHandle(a3);
        verify(workingMemory).update(a3FactHandle, a3);
        verify(workingMemory).getFactHandle(a4);
        verify(workingMemory).update(a4FactHandle, a4);
        verify(workingMemory).getFactHandle(a5);
        verify(workingMemory).update(a5FactHandle, a5);
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
        TestdataChainedEntity a4 = new TestdataChainedEntity("a4");
        a4.setChainedObject(a3);
        TestdataChainedEntity a5 = new TestdataChainedEntity("a5");
        a5.setChainedObject(a4);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        WorkingMemory workingMemory = mock(WorkingMemory.class);
        FactHandle a2FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a2)).thenReturn(a2FactHandle);
        FactHandle a3FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a3)).thenReturn(a3FactHandle);
        FactHandle a4FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a4)).thenReturn(a4FactHandle);
        FactHandle a5FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(a5)).thenReturn(a5FactHandle);
        FactHandle b1FactHandle = mock(FactHandle.class);
        when(workingMemory.getFactHandle(b1)).thenReturn(b1FactHandle);

        List<Object> entitiesSubChain = new ArrayList<Object>();
        entitiesSubChain.add(a2);
        entitiesSubChain.add(a3);
        entitiesSubChain.add(a4);

        GenericChainedChangePartMove move = new GenericChainedChangePartMove(entitiesSubChain,
                variableDescriptor, b0, a5, a5FactHandle, b1, b1FactHandle);
        move.doMove(workingMemory);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a5.getChainedObject());

        assertEquals(b0, a2.getChainedObject());
        assertEquals(a2, a3.getChainedObject());
        assertEquals(a3, a4.getChainedObject());
        assertEquals(a4, b1.getChainedObject());

        verify(workingMemory).update(a5FactHandle, a5);
        verify(workingMemory).getFactHandle(a2);
        verify(workingMemory).update(a2FactHandle, a2);
        verify(workingMemory).getFactHandle(a3);
        verify(workingMemory).update(a3FactHandle, a3);
        verify(workingMemory).getFactHandle(a4);
        verify(workingMemory).update(a4FactHandle, a4);
        verify(workingMemory).update(b1FactHandle, b1);
        verifyNoMoreInteractions(workingMemory);
    }

}

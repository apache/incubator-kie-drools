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
        FactHandle a3FactHandle = mock(FactHandle.class);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1");
        b1.setChainedObject(b0);

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        GenericChainedChangeMove move = new GenericChainedChangeMove(a3,
                a3FactHandle, entityDescriptor.getPlanningVariableDescriptor("chainedObject"), b1, null, null, null, null);

        WorkingMemory workingMemory = mock(WorkingMemory.class);
        move.doMove(workingMemory);

        assertEquals(a0, a1.getChainedObject());
        assertEquals(a1, a2.getChainedObject());
        assertEquals(b0, b1.getChainedObject());
        assertEquals(b1, a3.getChainedObject());

        verify(workingMemory).update(a3FactHandle, a3);
    }

}

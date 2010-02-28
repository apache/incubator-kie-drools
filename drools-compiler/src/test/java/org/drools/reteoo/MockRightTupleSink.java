package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class MockRightTupleSink
    implements
    RightTupleSink {
    
    private final List        retracted        = new ArrayList();

    public void retractRightTuple(RightTuple rightTuple,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory) {
        this.retracted.add( new Object[]{rightTuple, context, workingMemory} );

    }
    
    public List getRetracted() {
        return this.retracted;
    }

    public int getId() {
        return 0;
    }

    public RuleBasePartitionId getPartitionId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public short getType() {
        // TODO Auto-generated method stub
        return NodeTypeEnums.JoinNode;
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }
}

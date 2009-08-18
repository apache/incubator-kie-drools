package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.spi.PropagationContext;

public class UnificationNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {

    @Override
    public void updateSink(LeftTupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void attach() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void attach(InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doRemove(RuleRemovalContext context,
                            ReteooBuilder builder,
                            BaseNode node,
                            InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void networkUpdated() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNextLeftTupleSinkNode(LeftTupleSinkNode next) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPreviousLeftTupleSinkNode(LeftTupleSinkNode previous) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void assertLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public short getType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object createMemory(RuleBaseConfiguration config) {
        // TODO Auto-generated method stub
        return null;
    }

}

package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.spi.PropagationContext;

public class UnificationNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {

    public void updateSink(LeftTupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void attach() {
        // TODO Auto-generated method stub
        
    }
   
    public void attach(InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub
        
    }

    protected void doRemove(RuleRemovalContext context,
                            ReteooBuilder builder,
                            BaseNode node,
                            InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub
        
    }

    public void networkUpdated() {
        // TODO Auto-generated method stub
        
    }

    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setNextLeftTupleSinkNode(LeftTupleSinkNode next) {
        // TODO Auto-generated method stub
        
    }

    public void setPreviousLeftTupleSinkNode(LeftTupleSinkNode previous) {
        // TODO Auto-generated method stub
        
    }

    public void assertLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public short getType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isLeftTupleMemoryEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        // TODO Auto-generated method stub
        
    }

    public Object createMemory(RuleBaseConfiguration config) {
        // TODO Auto-generated method stub
        return null;
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // TODO Auto-generated method stub
        
    }

}

package org.drools.mvel;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

public class MockBetaNode extends BetaNode {
    
    public MockBetaNode() {
        
    }

    @Override
    protected boolean doRemove( RuleRemovalContext context, ReteooBuilder builder) {
        return true;
    }

    MockBetaNode(final int id,
                 final LeftTupleSource leftInput,
                 final ObjectSource rightInput,
                 BuildContext buildContext) {
        super( id,
               leftInput,
               rightInput,
               EmptyBetaConstraints.getInstance(),
               buildContext );
    }        

    MockBetaNode(final int id,
                 final LeftTupleSource leftInput,
                 final ObjectSource rightInput) {
        super( id,
               leftInput,
               rightInput,
               EmptyBetaConstraints.getInstance(),
               null );
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext pctx,
                             final InternalWorkingMemory workingMemory) {
    }

    @Override
    public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final InternalWorkingMemory workingMemory) {
    }

    public short getType() {
        return 0;
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
    }

    public LeftTuple createLeftTuple( InternalFactHandle factHandle,
                                      boolean leftTupleMemoryEnabled) {
        return new LeftTupleImpl(factHandle, this, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new LeftTupleImpl(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new LeftTupleImpl(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new LeftTupleImpl(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new LeftTupleImpl(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return super.createMemory( config, wm);
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }                
}
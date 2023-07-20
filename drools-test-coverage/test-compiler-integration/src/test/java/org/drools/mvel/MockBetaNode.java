package org.drools.mvel;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Memory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.AbstractLeftTuple;
import org.drools.core.reteoo.JoinNodeLeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;

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
                             final ReteEvaluator reteEvaluator) {
    }

    @Override
    public void modifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
    }

    public void retractRightTuple(final RightTuple rightTuple,
                                  final PropagationContext context,
                                  final ReteEvaluator reteEvaluator) {
    }

    public short getType() {
        return 0;
    }

    public void modifyRightTuple(RightTuple rightTuple,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
    }

    public AbstractLeftTuple createLeftTuple( InternalFactHandle factHandle,
                                      boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }    
    
    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public AbstractLeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final AbstractLeftTuple leftTuple,
                                     final Sink sink) {
        return new JoinNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public AbstractLeftTuple createLeftTuple(AbstractLeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     AbstractLeftTuple currentLeftChild,
                                     AbstractLeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new JoinNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
    public Memory createMemory(RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        return super.createMemory( config, reteEvaluator);
    }

    @Override
    public AbstractLeftTuple createPeer(AbstractLeftTuple original) {
        return null;
    }                
}
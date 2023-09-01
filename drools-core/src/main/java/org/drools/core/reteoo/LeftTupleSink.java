package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

/**
 * Receiver of propagated <code>ReteTuple</code>s from a
 * <code>TupleSource</code>.
 *
 * @see LeftTupleSource
 */
public interface LeftTupleSink extends LeftTupleNode, Sink {

    boolean isLeftTupleMemoryEnabled();

    LeftTuple createPeer(LeftTuple original);
    
    LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                              boolean leftTupleMemoryEnabled);

    LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                              final LeftTuple leftTuple,
                              final Sink sink);

    LeftTuple createLeftTuple(LeftTuple leftTuple,
                              Sink sink,
                              PropagationContext pctx,
                              boolean leftTupleMemoryEnabled);
    
    LeftTuple createLeftTuple(LeftTuple leftTuple,
                              RightTuple rightTuple,
                              Sink sink);
    
    LeftTuple createLeftTuple(LeftTuple leftTuple,
                              RightTuple rightTuple,
                              LeftTuple currentLeftChild,
                              LeftTuple currentRightChild,
                              Sink sink,
                              boolean leftTupleMemoryEnabled);

    ObjectTypeNode.Id getLeftInputOtnId();

    void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId);
    
    BitMask getLeftInferredMask();

    void setPartitionIdWithSinks( RuleBasePartitionId partitionId );
}

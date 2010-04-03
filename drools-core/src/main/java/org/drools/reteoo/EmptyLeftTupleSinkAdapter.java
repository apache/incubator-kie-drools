package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

public class EmptyLeftTupleSinkAdapter extends AbstractLeftTupleSinkAdapter {

    private static final EmptyLeftTupleSinkAdapter instance = new EmptyLeftTupleSinkAdapter();

    public static final EmptyLeftTupleSinkAdapter getInstance() {
        return instance;
    }

    public EmptyLeftTupleSinkAdapter() {
        super( RuleBasePartitionId.MAIN_PARTITION );
        // constructor needed for serialisation
    }

    public void propagateAssertLeftTuple(final LeftTuple leftTuple,
                                         final RightTuple rightTuple,
                                         final LeftTuple currentLeftChild,
                                         final LeftTuple currentRightChild,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {
    }

    public void propagateAssertLeftTuple(final LeftTuple tuple,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final boolean leftTupleMemoryEnabled) {
    }

    public void createAndPropagateAssertLeftTuple(final InternalFactHandle factHandle,
                                                  final PropagationContext context,
                                                  final InternalWorkingMemory workingMemory,
                                                  final boolean leftTupleMemoryEnabled) {
    }

    public void propagateRetractLeftTuple(final LeftTuple tuple,
                                          final PropagationContext context,
                                          final InternalWorkingMemory workingMemory) {
    }

    public void propagateRetractRightTuple(RightTuple tuple,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory) {
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        return null;
    }

    public void propagateRetractLeftTupleDestroyRightTuple(LeftTuple tuple,
                                                           PropagationContext context,
                                                           InternalWorkingMemory workingMemory) {
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public LeftTupleSink[] getSinks() {
        return new LeftTupleSink[]{};
    }

    public int size() {
        return 0;
    }
    
    // related to true modify

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   RightTuple parentRightTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        return null;
    }

    public LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                                   LeftTuple parentLeftTuple,
                                                   PropagationContext context,
                                                   InternalWorkingMemory workingMemory,
                                                   boolean tupleMemoryEnabled) {
        return null;
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    RightTuple parentRightTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        return null;
    }

    public LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                                    LeftTuple parentLeftTuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory) {
        return null;
    }

    public void propagateModifyChildLeftTuple(LeftTuple leftTuple,
                                              PropagationContext context,
                                              InternalWorkingMemory workingMemory,
                                              boolean tupleMemoryEnabled) {
    }

    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {
    }

}

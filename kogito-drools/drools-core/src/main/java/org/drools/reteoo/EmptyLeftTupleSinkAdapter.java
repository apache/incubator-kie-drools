/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public void createChildLeftTuplesforQuery(LeftTuple leftTuple,
                                              RightTuple rightTuple,
                                              boolean leftTupleMemoryEnabled) {
        // TODO Auto-generated method stub
        
    }

    public void doPropagateAssertLeftTuple(PropagationContext context,
                                           InternalWorkingMemory workingMemory,
                                           LeftTuple leftTuple,
                                           LeftTupleSink sink) {
        // TODO Auto-generated method stub
        
    }

}

/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;

public interface LeftTupleSinkPropagator
    extends
    Externalizable {
    
    void createChildLeftTuplesforQuery(final LeftTuple leftTuple,
                                       final RightTuple rightTuple,
                                       boolean leftTupleMemoryEnabled, boolean linkRightTuple);
    
    void propagateAssertLeftTuple(LeftTuple leftTuple,
                                  RightTuple rightTuple,
                                  LeftTuple currentLeftChild, // insert new tuple before this child in the child list
                                  LeftTuple currentRightChild, // insert new tuple before this child in the child list
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory,
                                  boolean leftTupleMemoryEnabled);

    void propagateAssertLeftTuple(LeftTuple tuple,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory,
                                  boolean leftTupleMemoryEnabled);

    void createAndPropagateAssertLeftTuple(InternalFactHandle factHandle,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory,
                                           boolean leftTupleWorkingMemoryEnabled,
                                           LeftInputAdapterNode liaNode);

    void propagateRetractLeftTuple(LeftTuple tuple,
                                   PropagationContext context,
                                   InternalWorkingMemory workingMemory);

    void propagateRetractLeftTupleDestroyRightTuple(LeftTuple tuple,
                                                    PropagationContext context,
                                                    InternalWorkingMemory workingMemory);

    void propagateRetractRightTuple(RightTuple tuple,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

    void doPropagateAssertLeftTuple(PropagationContext context,
                                    InternalWorkingMemory workingMemory,
                                    LeftTuple leftTuple,
                                    LeftTupleSink sink);
    
    BaseNode getMatchingNode(BaseNode candidate);

    LeftTupleSinkNode getFirstLeftTupleSink();

    LeftTupleSinkNode getLastLeftTupleSink();
    
    LeftTupleSink[] getSinks();
    
    void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                 final ModifyPreviousTuples modifyPreviousTuples,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory);

    int size();
    
    // related to true modify
    
    void propagateModifyObject(InternalFactHandle factHandle,
                               ModifyPreviousTuples modifyPreviousTuples,
                               PropagationContext context,
                               InternalWorkingMemory workingMemory);

    LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                            RightTuple parentRightTuple,
                                            PropagationContext context,
                                            InternalWorkingMemory workingMemory,
                                            boolean tupleMemoryEnabled);

    LeftTuple propagateModifyChildLeftTuple(LeftTuple childLeftTuple,
                                            LeftTuple parentLeftTuple,
                                            PropagationContext context,
                                            InternalWorkingMemory workingMemory,
                                            boolean tupleMemoryEnabled);
    
    void propagateModifyChildLeftTuple(LeftTuple leftTuple,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory,
                                       boolean tupleMemoryEnabled);

    LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                             RightTuple parentRightTuple,
                                             PropagationContext context,
                                             InternalWorkingMemory workingMemory);

    LeftTuple propagateRetractChildLeftTuple(LeftTuple childLeftTuple,
                                             LeftTuple parentLeftTuple,
                                             PropagationContext context,
                                             InternalWorkingMemory workingMemory);
}

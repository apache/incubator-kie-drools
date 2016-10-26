/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;

/**
 * Receiver of propagated <code>ReteTuple</code>s from a
 * <code>TupleSource</code>.
 *
 * @see LeftTupleSource
 */
public interface LeftTupleSink
    extends
    LeftTupleNode,
    Externalizable,
    Sink {

    /**
     * Assert a new <code>ReteTuple</code>.
     *
     * @param leftTuple
     *            The <code>ReteTuple</code> to propagate.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    void assertLeftTuple(LeftTuple leftTuple,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);

    void retractLeftTuple(LeftTuple leftTuple,
                          PropagationContext context,
                          InternalWorkingMemory workingMemory);

    boolean isLeftTupleMemoryEnabled();

    void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled);
    
    void modifyLeftTuple(InternalFactHandle factHandle,
                         ModifyPreviousTuples modifyPreviousTuples,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);
    
    void modifyLeftTuple(LeftTuple leftTuple,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);

    LeftTuple createPeer(LeftTuple original);
    
    LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                              Sink sink,
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

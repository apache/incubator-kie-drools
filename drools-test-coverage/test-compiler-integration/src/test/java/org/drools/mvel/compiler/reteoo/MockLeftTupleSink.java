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

package org.drools.mvel.compiler.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

public class MockLeftTupleSink extends LeftTupleSource
    implements
        LeftTupleSinkNode,
    MemoryFactory {
    private static final long serialVersionUID = 510l;
    private final List        asserted         = new ArrayList();
    private final List        retracted        = new ArrayList();

    private LeftTupleSinkNode     previousTupleSinkNode;
    private LeftTupleSinkNode     nextTupleSinkNode;

    public MockLeftTupleSink() {
        super( 0, null );
    }

    public MockLeftTupleSink(final int id) {
        super(id, null);
    }

    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
    }

    public void ruleAttached() {
        // TODO Auto-generated method stub
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return true;
    }

    public void attach(BuildContext buildContext) {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node 
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node 
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
    }

    public short getType() {
        return 0;
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new LeftTupleImpl(factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new LeftTupleImpl(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new LeftTupleImpl(leftTuple,sink, pctx, leftTupleMemoryEnabled );
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

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return null;
    }

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return null;
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }
}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;

/**
 * Node which allows to follow different paths in the Rete-OO network,
 * based on the result of a boolean <code>Test</code>.
 */
public class ConditionalBranchNode extends LeftTupleSource implements LeftTupleSinkNode, MemoryFactory<ConditionalBranchNode.ConditionalBranchMemory>  {

    protected ConditionalBranchEvaluator branchEvaluator;

    protected boolean tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    public ConditionalBranchNode() { }

    public ConditionalBranchNode( int id,
                                  LeftTupleSource tupleSource,
                                  ConditionalBranchEvaluator branchEvaluator,
                                  BuildContext context ) {
        super(id, context);
        setLeftTupleSource( tupleSource );
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        this.branchEvaluator = branchEvaluator;

        initMasks(context, tupleSource);

        hashcode = calculateHashCode();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        tupleMemoryEnabled = in.readBoolean();
        branchEvaluator = (ConditionalBranchEvaluator) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeBoolean(tupleMemoryEnabled);
        out.writeObject(branchEvaluator);
    }
    

    public ConditionalBranchEvaluator getBranchEvaluator() {
        return branchEvaluator;
    }

    public void attach( BuildContext context ) {
        getLeftTupleSource().addTupleSink(this, context);
    }

    public void networkUpdated(UpdateContext updateContext) {
        getLeftTupleSource().networkUpdated(updateContext);
    }

    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[ConditionalBranchNode: cond=" + this.branchEvaluator + "]";
    }

    private int calculateHashCode() {
        return getLeftTupleSource().hashCode() ^ this.branchEvaluator.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if ( object == null || !(object instanceof ConditionalBranchNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        ConditionalBranchNode other = (ConditionalBranchNode)object;
        return getLeftTupleSource().getId() == other.getLeftTupleSource().getId() &&
                this.branchEvaluator.equals( other.branchEvaluator );
    }

    public ConditionalBranchMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new ConditionalBranchMemory( branchEvaluator.createContext() );
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        EvalNodeLeftTuple peer = new EvalNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
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

    public short getType() {
        return NodeTypeEnums.ConditionalBranchNode;
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new EvalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    public static class ConditionalBranchMemory extends AbstractBaseLinkedListNode<Memory>
            implements
            Externalizable,
            Memory {

        private static final long serialVersionUID = 510l;

        public Object             context;
        
        private SegmentMemory     segmentMemory;

        public ConditionalBranchMemory() {

        }

        public ConditionalBranchMemory(final Object context) {
            this.context = context;
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            context = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }

        public short getNodeType() {
            return NodeTypeEnums.ConditionalBranchNode;
        }
        
        public void setSegmentMemory(SegmentMemory segmentMemory) {
            this.segmentMemory = segmentMemory;
        }

        public SegmentMemory getSegmentMemory() {
            return segmentMemory;
        }

        public void reset() { }
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return getLeftTupleSource().getObjectTypeNode();
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        if ( !this.isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            return true;
        } else {
            throw new RuntimeException("ConditionalBranchNode cannot be shared");
        }
    }

    @Override
    protected void initDeclaredMask(BuildContext context, LeftTupleSource leftInput) {
        // See LeftTupleSource.initDeclaredMask() should result for the ConditionalBranch to result in ALLSET:
        // at the moment if pattern is null (e.g. for eval node) we cannot calculate the mask, so we leave it to 0
        // 
        // In other words, a conditional branch is analogous to an eval() call - mask ALL SET
        
        // To achieve the result, we highjack the call:
        super.initDeclaredMask(null, null);
    }
    
}

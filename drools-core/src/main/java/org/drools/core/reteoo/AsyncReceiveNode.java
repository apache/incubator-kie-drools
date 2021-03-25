/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.AsyncReceive;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.index.TupleList;

public class AsyncReceiveNode extends LeftTupleSource
        implements
        LeftTupleSinkNode,
        MemoryFactory<AsyncReceiveNode.AsyncReceiveMemory> {

    private static final long serialVersionUID = 510l;

    private String messageId;
    private boolean tupleMemoryEnabled;

    private AlphaNodeFieldConstraint[] alphaConstraints;
    private BetaConstraints betaConstraints;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private AsyncReceive receive;
    private transient ObjectTypeConf objectTypeConf;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public AsyncReceiveNode() { }

    public AsyncReceiveNode( final int id,
                             final LeftTupleSource tupleSource,
                             final AsyncReceive receive,
                             final AlphaNodeFieldConstraint[] constraints,
                             final BetaConstraints binder,
                             final BuildContext context ) {
        super( id, context );
        this.messageId = receive.getMessageId();
        this.receive = receive;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        setLeftTupleSource( tupleSource );
        this.setObjectCount(leftInput.getObjectCount() + 1); // 'async receive' node increases the object count
        this.alphaConstraints = constraints;
        this.betaConstraints = (binder == null) ? EmptyBetaConstraints.getInstance() : binder;
        this.betaConstraints.init(context, getType());

        initMasks( context, tupleSource );

        hashcode = calculateHashCode();
    }

    public void readExternal( ObjectInput in ) throws IOException,
            ClassNotFoundException {
        super.readExternal( in );
        messageId = ( String ) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        alphaConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        betaConstraints = (BetaConstraints) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( messageId );
        out.writeBoolean(tupleMemoryEnabled);
        out.writeObject( alphaConstraints );
        out.writeObject( betaConstraints );
    }

    public void doAttach( BuildContext context ) {
        super.doAttach(context);
        this.leftInput.addTupleSink( this, context );
        context.getKnowledgeBase().addReceiveNode(this);
    }

    public AlphaNodeFieldConstraint[] getAlphaConstraints() {
        return alphaConstraints;
    }

    public BetaConstraints getBetaConstraints() {
        return betaConstraints;
    }

    public Class<?> getResultClass() {
        return receive.getResultClass();
    }

    public ObjectTypeConf getObjectTypeConf( InternalWorkingMemory workingMemory ) {
        if ( objectTypeConf == null ) {
            // use default entry point and object class. Notice that at this point object is assignable to resultClass
            objectTypeConf = new ClassObjectTypeConf( workingMemory.getEntryPoint(), getResultClass(), workingMemory.getKnowledgeBase() );
        }
        return objectTypeConf;
    }

    public static class AsyncReceiveAction extends PropagationEntry.AbstractPropagationEntry {

        private final AsyncReceiveNode asyncReceiveNode;
        private final Object object;

        private AsyncReceiveAction( AsyncReceiveNode asyncReceiveNode, Object object ) {
            this.asyncReceiveNode = asyncReceiveNode;
            this.object = object;
        }

        @Override
        public void execute( final InternalWorkingMemory wm ) {
            AsyncReceiveMemory memory = wm.getNodeMemory( asyncReceiveNode );
            memory.addMessage( object );
            memory.setNodeDirtyWithoutNotify();

            for (final PathMemory pmem : memory.getSegmentMemory().getPathMemories()) {
                if (pmem.getPathEndNode().getAssociatedRuleSize() == 0) {
                    // if the corresponding rule has been removed avoid to link and notify this pmem
                    continue;
                }
                InternalAgenda agenda = pmem.getActualAgenda( wm );
                pmem.doLinkRule( agenda );
            }
        }
    }

    public void networkUpdated( UpdateContext updateContext ) {
        this.leftInput.networkUpdated( updateContext );
    }

    @Override
    protected Pattern getLeftInputPattern( BuildContext context ) {
        return context.getLastBuiltPatterns()[0];
    }

    public String toString() {
        return "[AsyncReceiveNode(" + this.id + "): messageId=" + messageId + "]";
    }

    private int calculateHashCode() {
        return this.leftInput.hashCode() ^ this.messageId.hashCode();
    }

    @Override
    public boolean equals( final Object object ) {
        if ( this == object ) {
            return true;
        }

        if ( !(object instanceof AsyncReceiveNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        AsyncReceiveNode other = ( AsyncReceiveNode ) object;
        return this.leftInput.getId() != other.leftInput.getId() && this.messageId.equals( other.messageId );
    }

    public AsyncReceiveMemory createMemory( final RuleBaseConfiguration config, InternalWorkingMemory wm ) {
        return new AsyncReceiveMemory(this, wm);
    }

    @Override
    public LeftTuple createPeer( LeftTuple original ) {
        EvalNodeLeftTuple peer = new EvalNodeLeftTuple();
        peer.initPeer( ( BaseLeftTuple ) original, this );
        original.setPeer( peer );
        return peer;
    }

    protected boolean doRemove( final RuleRemovalContext context,
                                final ReteooBuilder builder ) {
        if ( !this.isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            return true;
        }
        return false;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled( boolean tupleMemoryEnabled ) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     *
     * @return The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     *
     * @param next The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode( final LeftTupleSinkNode next ) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     *
     * @return The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     *
     * @param previous The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode( final LeftTupleSinkNode previous ) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.AsyncReceiveNode;
    }

    public LeftTuple createLeftTuple( InternalFactHandle factHandle,
                                      boolean leftTupleMemoryEnabled ) {
        return new EvalNodeLeftTuple( factHandle, this, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple( final InternalFactHandle factHandle,
                                      final LeftTuple leftTuple,
                                      final Sink sink ) {
        return new EvalNodeLeftTuple( factHandle, leftTuple, sink );
    }

    @Override
    public LeftTuple createLeftTuple( LeftTuple leftTuple, Sink sink, PropagationContext pctx, boolean leftTupleMemoryEnabled ) {
        return new EvalNodeLeftTuple(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple( LeftTuple leftTuple,
                                      RightTuple rightTuple,
                                      Sink sink ) {
        return new EvalNodeLeftTuple( leftTuple, rightTuple, sink );
    }

    public LeftTuple createLeftTuple( LeftTuple leftTuple,
                                      RightTuple rightTuple,
                                      LeftTuple currentLeftChild,
                                      LeftTuple currentRightChild,
                                      Sink sink,
                                      boolean leftTupleMemoryEnabled ) {
        return new EvalNodeLeftTuple( leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    public static class AsyncReceiveMemory extends AbstractBaseLinkedListNode<Memory>
            implements
            SegmentNodeMemory {

        private static final long serialVersionUID = 510l;

        private final Consumer<AsyncMessage> receiver;
        private final String messageId;

        private final TupleList insertOrUpdateLeftTuples = new TupleList();
        private final List<Object> messages = new ArrayList<>();

        private SegmentMemory memory;
        private long nodePosMaskBit;

        public AsyncReceiveMemory(AsyncReceiveNode node, InternalWorkingMemory wm) {
            this.messageId = node.messageId;
            this.receiver = asyncMessage -> wm.addPropagation( new AsyncReceiveAction( node, asyncMessage.getObject() ) );
            AsyncMessagesCoordinator.get().registerReceiver( node.messageId, receiver );
        }

        public void addMessage(Object message) {
            messages.add(message);
        }

        public List<Object> getMessages() {
            return messages;
        }

        public TupleList getInsertOrUpdateLeftTuples() {
            return insertOrUpdateLeftTuples;
        }

        public void addInsertOrUpdateLeftTuple(LeftTuple leftTuple) {
            insertOrUpdateLeftTuples.add( leftTuple );
        }

        public short getNodeType() {
            return NodeTypeEnums.AsyncReceiveNode;
        }

        public SegmentMemory getSegmentMemory() {
            return this.memory;
        }

        public void setSegmentMemory( SegmentMemory smem ) {
            this.memory = smem;
        }

        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }

        public void setNodePosMaskBit( long segmentPos ) {
            this.nodePosMaskBit = segmentPos;
        }

        public void setNodeDirtyWithoutNotify() {
            if (memory != null) {
                memory.updateDirtyNodeMask( nodePosMaskBit );
            }
        }

        public void setNodeCleanWithoutNotify() {
            if (memory != null) {
                memory.updateCleanNodeMask( nodePosMaskBit );
            }
        }

        public void reset() {
            messages.clear();
        }

        public void dispose() {
            AsyncMessagesCoordinator.get().deregisterReceiver( messageId, receiver );
        }
    }
}
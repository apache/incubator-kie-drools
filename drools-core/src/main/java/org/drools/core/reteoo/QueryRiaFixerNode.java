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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleIterator;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.TupleStartEqualsConstraint;
import org.drools.core.common.TupleStartEqualsConstraint.TupleStartEqualsConstraintContextEntry;
import org.drools.core.common.UpdateContext;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


public class QueryRiaFixerNode extends LeftTupleSource
    implements
    LeftTupleSinkNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    protected boolean         tupleMemoryEnabled;

    private BetaNode          betaNode;
    
    private LeftTupleSinkNode previousTupleSinkNode;
    
    private LeftTupleSinkNode nextTupleSinkNode;    
    
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public QueryRiaFixerNode() {

    }

    public QueryRiaFixerNode(final int id,
                             final LeftTupleSource tupleSource,
                             final BuildContext context) {
        super(id, context);
        setLeftTupleSource(tupleSource);
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        betaNode = (BetaNode) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(   betaNode  );
        out.writeBoolean( tupleMemoryEnabled );
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public BetaNode getBetaNode() {
        return betaNode;
    }

    @Override
    public void addTupleSink(LeftTupleSink tupleSink, BuildContext context) {
        this.betaNode = (BetaNode) tupleSink;
    }

    public void attach( BuildContext context ) {
        this.leftInput.addTupleSink( this, context );
        if (context == null || context.getKnowledgeBase().getConfiguration().isPhreakEnabled() ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            PropagationContextFactory pctxFactory = context.getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION,
                                                                                               null, null, null);
            this.leftInput.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, false, betaNode)  );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, true, betaNode)  );
    }
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {        
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, false, betaNode)  );
    }

    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[RiaQueryFixerNode: ]";
    }

    public int hashCode() {
        return this.leftInput.hashCode();
    }

    public boolean equals(final Object object) {
        // we never node share, so only return true if we are instance equal
        return this == object;
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        Iterator<LeftTuple> it = LeftTupleIterator.iterator( workingMemory, this );
        
        for ( LeftTuple leftTuple =  it.next(); leftTuple != null; leftTuple = it.next() ) {
            LeftTuple childLeftTuple = leftTuple.getFirstChild();
            while ( childLeftTuple != null ) {
                RightTuple rightParent = childLeftTuple.getRightParent();            
                sink.assertLeftTuple( sink.createLeftTuple( leftTuple, rightParent, childLeftTuple, null, sink, true ),
                                      context,
                                      workingMemory );  
                
                while ( childLeftTuple != null && childLeftTuple.getRightParent() == rightParent ) {
                    // skip to the next child that has a different right parent
                    childLeftTuple = childLeftTuple.getHandleNext();
                }
            }
        }
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            getLeftTupleSource().removeTupleSink(this);
            return true;
        }
        return false;
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
        return NodeTypeEnums.QueryRiaFixerNode;
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return this.betaNode.createLeftTuple( factHandle, this.betaNode, leftTupleMemoryEnabled  );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return this.betaNode.createLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return this.betaNode.createLeftTuple( leftTuple, this.betaNode, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return this.betaNode.createLeftTuple( leftTuple, rightTuple, this.betaNode );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return this.betaNode.createLeftTuple( leftTuple, rightTuple, currentLeftChild, currentRightChild,  this.betaNode, leftTupleMemoryEnabled  );        
    }      


    @Override
    public boolean isInUse() {
        return this.betaNode != null;
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.leftInput;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }

    @Override
    public void removeTupleSink(LeftTupleSink tupleSink) {
        if (tupleSink != betaNode) {
            throw new IllegalArgumentException( tupleSink + " is not a sink for this node" );
        }
        betaNode = null;
    }

    public static class QueryRiaFixerNodeFixer
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private PropagationContext context;

        private LeftTuple leftTuple;
        private BetaNode  node;
        private boolean   retract;

        public QueryRiaFixerNodeFixer(PropagationContext context) {
            this.context = context;
        }

        public QueryRiaFixerNodeFixer(PropagationContext context,
                                      LeftTuple leftTuple,
                                      boolean retract,
                                      BetaNode node) {
            this.context = context;
            this.leftTuple = leftTuple;
            this.retract = retract;
            this.node = node;
        }

        public QueryRiaFixerNodeFixer(MarshallerReaderContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public Action serialize(MarshallerWriteContext context) throws IOException {
            throw new UnsupportedOperationException("Should not be present in network on serialisation");
        }

        public void execute(InternalWorkingMemory workingMemory) {
            leftTuple.setLeftTupleSink(this.node);
            if (leftTuple.getFirstChild() == null) {
                this.node.assertLeftTuple(leftTuple,
                                          context,
                                          workingMemory);
            } else {
                if (retract) {
                    this.node.getSinkPropagator().propagateRetractLeftTuple(leftTuple,
                                                                            context,
                                                                            workingMemory);
                } else {
                    this.node.getSinkPropagator().propagateModifyChildLeftTuple(leftTuple,
                                                                                context,
                                                                                workingMemory,
                                                                                true);
                }
            }

            if (leftTuple.getLeftParent() == null) {
                // It's not an open query, as we aren't recording parent chains, so we need to clear out right memory

                Object node = workingMemory.getNodeMemory(this.node);

                TupleMemory rightMemory = null;
                if (node instanceof BetaMemory) {
                    rightMemory = ((BetaMemory) node).getRightTupleMemory();
                } else if (node instanceof AccumulateMemory) {
                    rightMemory = ((AccumulateMemory) node).getBetaMemory().getRightTupleMemory();
                }


                final TupleStartEqualsConstraint constraint = TupleStartEqualsConstraint.getInstance();
                TupleStartEqualsConstraintContextEntry contextEntry = new TupleStartEqualsConstraintContextEntry();
                contextEntry.updateFromTuple(workingMemory, leftTuple);

                FastIterator rightIt = rightMemory.fastIterator();
                for (Tuple rightTuple = rightMemory.getFirst(leftTuple); rightTuple != null; ) {
                    Tuple temp = (Tuple) rightIt.next(rightTuple);

                    if (constraint.isAllowedCachedLeft(contextEntry, rightTuple.getFactHandle())) {
                        rightMemory.remove(rightTuple);
                    }
                    rightTuple = temp;
                }
            }
        }

        public String toString() {
            return "[QueryRiaFixerNodeFixer leftTuple=" + leftTuple + ",\n        retract=" + retract + "]\n";
        }
    }
}

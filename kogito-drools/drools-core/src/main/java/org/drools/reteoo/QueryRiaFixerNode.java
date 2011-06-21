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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map.Entry;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleBasePartitionId;
import org.drools.definition.rule.Rule;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.EvalCondition;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleComponent;

import org.drools.reteoo.ReteooWorkingMemory.QueryRiaFixerNodeFixer;

/**
 * Node which filters <code>ReteTuple</code>s.
 *
 * <p>
 * Using a semantic <code>Test</code>, this node may allow or disallow
 * <code>Tuples</code> to proceed further through the Rete-OO network.
 * </p>
 *
 * @see QueryRiaFixerNode
 * @see Eval
 * @see LeftTuple
 */
public class QueryRiaFixerNode extends LeftTupleSource
    implements
    LeftTupleSinkNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private static final long serialVersionUID = 510l;

    /** The source of incoming <code>Tuples</code>. */
    private LeftTupleSource   tupleSource;

    protected boolean         tupleMemoryEnabled;

    private AccumulateNode    accumulateNode;
    
    private LeftTupleSinkNode previousTupleSinkNode;
    
    private LeftTupleSinkNode nextTupleSinkNode;    
    
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public QueryRiaFixerNode() {

    }

    /**
     * Construct.
     *
     * @param rule
     *            The rule
     * @param tupleSource
     *            The source of incoming <code>Tuples</code>.
     * @param eval
     */
    public QueryRiaFixerNode(final int id,
                             final LeftTupleSource tupleSource,
                             final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.tupleSource = tupleSource;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        accumulateNode = (AccumulateNode) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(   accumulateNode  );
        out.writeObject( tupleSource );
        out.writeBoolean( tupleMemoryEnabled );
    }

    public AccumulateNode getAccumulateNode() {
        return accumulateNode;
    }
    /**
     * Attaches this node into the network.
     */
    public void attach() {
        this.tupleSource.addTupleSink( this );
    }
    
    @Override
    public void addTupleSink(LeftTupleSink tupleSink) {
        this.accumulateNode = (AccumulateNode) tupleSink;
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.reteoo.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Assert a new <code>Tuple</code>.
     *
     * @param leftTuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, false, accumulateNode)  );
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, true, accumulateNode)  );
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
        if ( leftTuple != null ) {
            leftTuple.reAdd(); //
            // LeftTuple previously existed, so continue as modify
            modifyLeftTuple( leftTuple,
                             context,
                             workingMemory );
        } else {
            // LeftTuple does not exist, so create and continue as assert
            assertLeftTuple( new LeftTupleImpl( factHandle,
                                                this,
                                                true ),
                             context,
                             workingMemory );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {        
        context.getQueue2().addLast( new QueryRiaFixerNodeFixer(context, leftTuple, false, accumulateNode)  );
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
        return this.tupleSource.hashCode();
    }

    public boolean equals(final Object object) {
        // we never node share, so only return true if we are instance equal
        if ( this == object ) {
            return true;
        }
        return false; 
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final RiaQueryFixerNodeAdapter adapter = new RiaQueryFixerNodeAdapter( this,
                                                                                   sink );
        this.tupleSource.updateSink( adapter,
                                     context,
                                     workingMemory );
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
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
        return NodeTypeEnums.EvalConditionNode;
    }

    /**
     * Used with the updateSink method, so that the parent LeftTupleSource
     * can  update the  TupleSink
     */
    private static class RiaQueryFixerNodeAdapter
        implements
        LeftTupleSink, LeftTupleSinkAdapter {
        private final QueryRiaFixerNode node;
        private final LeftTupleSink     sink;

        public RiaQueryFixerNodeAdapter(final QueryRiaFixerNode node,
                                          final LeftTupleSink sink) {
            this.node = node;
            this.sink = sink;
        }

        public void assertLeftTuple(final LeftTuple leftTuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory) {
        }
        
        

        public short getType() {
            return 0;
        }

        public boolean isLeftTupleMemoryEnabled() {
            return false;
        }

        public void modifyLeftTuple(InternalFactHandle factHandle,
                                    ModifyPreviousTuples modifyPreviousTuples,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "LeftTupleSinkUpdateAdapter onlys supports assertLeftTuple method calls" );
        }

        public void modifyLeftTuple(LeftTuple leftTuple,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "LeftTupleSinkUpdateAdapter onlys supports assertLeftTuple method calls" );
        }

        public void retractLeftTuple(LeftTuple leftTuple,
                                     PropagationContext context,
                                     InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "LeftTupleSinkUpdateAdapter onlys supports assertLeftTuple method calls" );
        }

        public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
            throw new UnsupportedOperationException( "LeftTupleSinkUpdateAdapter onlys supports assertLeftTuple method calls" );
        }

        public void readExternal(ObjectInput arg0) throws IOException,
                                                  ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput arg0) throws IOException {
        }

        public int getId() {
            return 0;
        }

        public RuleBasePartitionId getPartitionId() {
            return sink.getPartitionId();
        }

        public LeftTupleSink getRealSink() {
            return this.node;
        }

    }

    @Override
    public boolean isInUse() {
        return this.accumulateNode != null;
    }

}

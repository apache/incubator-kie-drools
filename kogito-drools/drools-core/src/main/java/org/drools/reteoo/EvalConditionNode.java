/**
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

/**
 * Node which filters <code>ReteTuple</code>s.
 *
 * <p>
 * Using a semantic <code>Test</code>, this node may allow or disallow
 * <code>Tuples</code> to proceed further through the Rete-OO network.
 * </p>
 *
 * @see EvalConditionNode
 * @see Eval
 * @see LeftTuple
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class EvalConditionNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
    *
    */
    private static final long serialVersionUID = 400L;

    /** The semantic <code>Test</code>. */
    private EvalCondition     condition;

    /** The source of incoming <code>Tuples</code>. */
    private LeftTupleSource   tupleSource;

    protected boolean         tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public EvalConditionNode() {

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
    public EvalConditionNode(final int id,
                             final LeftTupleSource tupleSource,
                             final EvalCondition eval,
                             final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.condition = eval;
        this.tupleSource = tupleSource;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        condition = (EvalCondition) in.readObject();
        tupleSource = (LeftTupleSource) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( condition );
        out.writeObject( tupleSource );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
    }

    /**
     * Attaches this node into the network.
     */
    public void attach() {
        this.tupleSource.addTupleSink( this );
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

    /**
     * Retrieve the <code>Test</code> associated with this node.
     *
     * @return The <code>Test</code>.
     */
    public EvalCondition getCondition() {
        return this.condition;
    }

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
        final EvalMemory memory = (EvalMemory) workingMemory.getNodeMemory( this );

        final boolean allowed = this.condition.isAllowed( leftTuple,
                                                          workingMemory,
                                                          memory.context );

        if ( allowed ) {
            this.sink.propagateAssertLeftTuple( leftTuple,
                                                context,
                                                workingMemory,
                                                this.tupleMemoryEnabled );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        if ( leftTuple.firstChild != null ) {
            this.sink.propagateRetractLeftTuple( leftTuple,
                                                 context,
                                                 workingMemory );
        }
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
            assertLeftTuple( new LeftTuple( factHandle,
                                            this,
                                            true ),
                             context,
                             workingMemory );
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final EvalMemory memory = (EvalMemory) workingMemory.getNodeMemory( this );
        boolean wasPropagated = leftTuple.firstChild != null;

        final boolean allowed = this.condition.isAllowed( leftTuple,
                                                          workingMemory,
                                                          memory.context );

        if ( allowed ) {
            if ( wasPropagated ) {
                // modify
                this.sink.propagateModifyChildLeftTuple( leftTuple,
                                                         context,
                                                         workingMemory,
                                                         this.tupleMemoryEnabled );
            } else {
                // assert
                this.sink.propagateAssertLeftTuple( leftTuple,
                                                    context,
                                                    workingMemory,
                                                    this.tupleMemoryEnabled );
            }
        } else {
            if ( wasPropagated ) {
                // retract
                this.sink.propagateRetractLeftTuple( leftTuple,
                                                     context,
                                                     workingMemory );
            }
            // else do nothing
        }
    }

    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[EvalConditionNode: cond=" + this.condition + "]";
    }

    public int hashCode() {
        return this.tupleSource.hashCode() ^ this.condition.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || object.getClass() != EvalConditionNode.class ) {
            return false;
        }

        final EvalConditionNode other = (EvalConditionNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.condition.equals( other.condition );
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new EvalMemory( this.condition.createContext() );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#updateNewNode(org.drools.reteoo.WorkingMemoryImpl, org.drools.spi.PropagationContext)
     */
    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final LeftTupleSinkUpdateAdapter adapter = new LeftTupleSinkUpdateAdapter( this,
                                                                                   sink,
                                                                                   condition );
        this.tupleSource.updateSink( adapter,
                                     context,
                                     workingMemory );
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }

        if ( !this.isInUse() ) {
            for( InternalWorkingMemory workingMemory : workingMemories ) {
                workingMemory.clearNodeMemory( this );
            }
        } else {
            // need to re-wire eval expression to the same one from another rule 
            // that is sharing this node
            Entry<Rule, RuleComponent> next = this.getAssociations().entrySet().iterator().next();
            this.condition = (EvalCondition) next.getValue();
        }

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

    public static class EvalMemory
        implements
        Externalizable {

        private static final long serialVersionUID = -2754669682742843929L;

        public Object             context;

        public EvalMemory() {

        }

        public EvalMemory(final Object context) {
            this.context = context;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }
    }

    /**
     * Used with the updateSink method, so that the parent LeftTupleSource
     * can  update the  TupleSink
     */
    private static class LeftTupleSinkUpdateAdapter
        implements
        LeftTupleSink, LeftTupleSinkAdapter {
        private final EvalConditionNode node;
        private final LeftTupleSink     sink;
        private final EvalCondition     constraint;

        public LeftTupleSinkUpdateAdapter(final EvalConditionNode node,
                                          final LeftTupleSink sink,
                                          final EvalCondition constraint) {
            this.node = node;
            this.sink = sink;
            this.constraint = constraint;
        }

        public void assertLeftTuple(final LeftTuple leftTuple,
                                    final PropagationContext context,
                                    final InternalWorkingMemory workingMemory) {

            final EvalMemory memory = (EvalMemory) workingMemory.getNodeMemory( node );
            // need to be overridden, because it was pointing to the adapter instead
            leftTuple.setLeftTupleSink( this.node );

            final boolean allowed = this.constraint.isAllowed( leftTuple,
                                                               workingMemory,
                                                               memory.context );

            if ( allowed ) {
                final LeftTuple tuple = new LeftTuple( leftTuple,
                                                       this.sink,
                                                       false );
                this.sink.assertLeftTuple( tuple,
                                           context,
                                           workingMemory );
            }
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

}

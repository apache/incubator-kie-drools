package org.drools.reteoo;

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

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>BetaNode</code> provides the base abstract class for <code>JoinNode</code> and <code>NotNode</code>. It implements
 * both TupleSink and ObjectSink and as such can receive <code>Tuple</code>s and <code>FactHandle</code>s. BetaNode uses BetaMemory
 * to store the propagated instances.
 *
 * @see org.drools.reteoo.TupleSource
 * @see org.drools.reteoo.TupleSink
 * @see org.drools.reteoo.BetaMemory
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
abstract class BetaNode extends TupleSource
    implements
    TupleSinkNode,
    ObjectSinkNode,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The left input <code>TupleSource</code>. */
    protected TupleSource     leftInput;

    /** The right input <code>TupleSource</code>. */
    protected ObjectSource    rightInput;

    protected BetaConstraints constraints;

    private TupleSinkNode           previousTupleSinkNode;
    private TupleSinkNode           nextTupleSinkNode;

    private ObjectSinkNode          previousObjectSinkNode;
    private ObjectSinkNode          nextObjectSinkNode;

    protected boolean               objectMemory = true; // hard coded to true
    protected boolean               tupleMemoryEnabled;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public BetaNode() {

    }

    /**
     * Constructs a <code>BetaNode</code> using the specified <code>BetaNodeBinder</code>.
     *
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     */
    BetaNode(final int id,
             final TupleSource leftInput,
             final ObjectSource rightInput,
             final BetaConstraints constraints) {
        super( id );
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.constraints = constraints;

        if ( this.constraints == null ) {
            throw new RuntimeException( "cannot have null constraints, must at least be an instance of EmptyBetaConstraints" );
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        leftInput   = (TupleSource)in.readObject();
        rightInput   = (ObjectSource)in.readObject();
        constraints   = (BetaConstraints)in.readObject();
        previousTupleSinkNode   = (TupleSinkNode)in.readObject();
        nextTupleSinkNode   = (TupleSinkNode)in.readObject();
        previousObjectSinkNode   = (ObjectSinkNode)in.readObject();
        nextObjectSinkNode   = (ObjectSinkNode)in.readObject();
        objectMemory    = in.readBoolean();
        tupleMemoryEnabled  = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(leftInput);
        out.writeObject(rightInput);
        out.writeObject(constraints);
        out.writeObject(previousTupleSinkNode);
        out.writeObject(nextTupleSinkNode);
        out.writeObject(previousObjectSinkNode);
        out.writeObject(nextObjectSinkNode);
        out.writeBoolean(objectMemory);
        out.writeBoolean(tupleMemoryEnabled);
    }

    public BetaNodeFieldConstraint[] getConstraints() {
        final LinkedList constraints = this.constraints.getConstraints();

        final BetaNodeFieldConstraint[] array = new BetaNodeFieldConstraint[constraints.size()];
        int i = 0;
        for ( LinkedListEntry entry = (LinkedListEntry) constraints.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
            array[i++] = (BetaNodeFieldConstraint) entry.getObject();
        }
        return array;
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.rightInput.addObjectSink( this );
        this.leftInput.addTupleSink( this );
    }

    public void networkUpdated() {
        this.rightInput.networkUpdated();
        this.leftInput.networkUpdated();
    }

    public List getRules() {
        final List list = new ArrayList();

        final TupleSink[] sinks = this.sink.getSinks();
        for ( int i = 0, length = sinks.length; i < length; i++ ) {
            if ( sinks[i] instanceof RuleTerminalNode ) {
                list.add( ((RuleTerminalNode) sinks[i]).getRule().getName() );
            } else if ( sinks[i] instanceof BetaNode ) {
                list.addAll( ((BetaNode) sinks[i]).getRules() );
            }
        }

        return list;
    }

    public ObjectTypeNode getObjectTypeNode() {
        ObjectSource source = this.rightInput;
        while ( !(source instanceof ObjectTypeNode) ) {
            source = source.objectSource;
        }
        return ((ObjectTypeNode) source);
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.rightInput.updateSink( this,
                                        propagationContext,
                                        workingMemory );
            this.leftInput.updateSink( this,
                                       propagationContext,
                                       workingMemory );
        }

    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        context.visitTupleSource( this );
        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.rightInput.remove( context,
                                builder,
                                this,
                                workingMemories );
        if( !context.alreadyVisited( this.leftInput )) {
            this.leftInput.remove( context,
                                   builder,
                                   this,
                                   workingMemories );
        }

    }

    public boolean isObjectMemoryEnabled() {
        return objectMemory;
    }

    public void setObjectMemoryEnabled(boolean objectMemory) {
        this.objectMemory = objectMemory;
    }

    public boolean isTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public String toString() {
        return "";
    }

    public void dumpMemory(final InternalWorkingMemory workingMemory) {
        final MemoryVisitor visitor = new MemoryVisitor( workingMemory );
        visitor.visit( this );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof BetaNode) ) {
            return false;
        }

        final BetaNode other = (BetaNode) object;

        return this.getClass() == other.getClass() && this.leftInput.equals( other.leftInput ) && this.rightInput.equals( other.rightInput ) && this.constraints.equals( other.constraints );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return this.constraints.createBetaMemory(config);
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public TupleSinkNode getNextTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextTupleSinkNode(final TupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public TupleSinkNode getPreviousTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousTupleSinkNode(final TupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextObjectSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }

}

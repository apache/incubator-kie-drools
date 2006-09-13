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
import org.drools.common.BetaNodeBinder;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PropagationContext;

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
abstract class BetaNode extends TupleSource implements
    TupleSinkNode,
    ObjectSinkNode,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The left input <code>TupleSource</code>. */
    private final TupleSource    leftInput;

    /** The right input <code>TupleSource</code>. */
    private final ObjectSource   rightInput;

    private final BetaNodeBinder joinNodeBinder;
    
    private TupleSinkNode previousTupleSinkNode;
    private TupleSinkNode nextTupleSinkNode;
    
    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode;   
    
    protected TupleMatchFactory tupleMatchFactory;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * The constructor defaults to using a BetaNodeBinder with no constraints
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     */
    BetaNode(final int id,
             final TupleSource leftInput,
             final ObjectSource rightInput) {
        this( id,
              leftInput,
              rightInput,
              BetaNodeBinder.simpleBinder );
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
             final BetaNodeBinder joinNodeBinder) {
        super( id );
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.joinNodeBinder = joinNodeBinder;
        
        this.tupleMatchFactory = SingleTupleMatchFactory.getInstance();     
    }
    
    public void addTupleSink(TupleSink tupleSink) {
        int previousSize = 0;
        if ( this.sink != null ) {
            previousSize = this.sink.size();
        }
        super.addTupleSink( tupleSink );
        
        // we are now greater than one, so use a CompositeTupleMatchFactory
        if ( previousSize == 1 ) {
            this.tupleMatchFactory = CompositeTupleMatchFactory.getInstance();
        }        
    }
    
    public void removeTupleSink(TupleSink tupleSink) {
        super.removeTupleSink( tupleSink );
        
        // We are now only one, so use a SingleTupleMatchFactory
        if ( this.sink.size() == 1 ) {
            this.tupleMatchFactory = SingleTupleMatchFactory.getInstance();
        }        
    }
    

    public FieldConstraint[] getConstraints() {
        return this.joinNodeBinder.getConstraints();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.leftInput.addTupleSink( this );
        this.rightInput.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.leftInput.updateNewNode( workingMemory,
                                          propagationContext );
            this.rightInput.updateNewNode( workingMemory,
                                           propagationContext );
        }

    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
//        if( !node.isInUse()) {
//            getTupleSinks().remove( node );
//        }
//        removeShare();
//
//        if ( ! this.isInUse() ) {
//            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
//                workingMemories[i].clearNodeMemory( this );
//            }
//        }
//        this.rightInput.remove( this,
//                                workingMemories );
//        this.leftInput.remove( this,
//                               workingMemories );

    }

    /**
     * @return the <code>joinNodeBinder</code>
     */
    BetaNodeBinder getJoinNodeBinder() {
        return this.joinNodeBinder;
    }    
    
    protected TupleMatch attemptJoin(final ReteTuple leftTuple,
                                     final InternalFactHandle handle,
                                     final ObjectMatches objectMatches,
                                     final BetaNodeBinder binder,
                                     final InternalWorkingMemory workingMemory) {
        if ( binder.isAllowed( handle,
                               leftTuple,
                               workingMemory ) ) {
            TupleMatch tupleMatch = this.tupleMatchFactory.newTupleMatch( leftTuple, objectMatches );          
			objectMatches.add( tupleMatch );
            leftTuple.addTupleMatch( handle,
                                     tupleMatch );
            return tupleMatch;

        } else {
            return null;
        }
    }
    
    //public abstract TupleSink getTupleSink();

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString() {
        // return "[JoinNode: common=" + this.commonDeclarations + "; decls=" +
        // this.tupleDeclarations + "]";
        return "";
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

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final BetaNode other = (BetaNode) object;

        return this.leftInput.equals( other.leftInput ) && this.rightInput.equals( other.rightInput ) && this.joinNodeBinder.equals( other.joinNodeBinder );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        return new BetaMemory( config,
                               this.getJoinNodeBinder() );
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
    public void setNextTupleSinkNode(TupleSinkNode next) {
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
    public void setPreviousTupleSinkNode(TupleSinkNode previous) {
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
    public void setNextObjectSinkNode(ObjectSinkNode next) {
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
    public void setPreviousObjectSinkNode(ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }
    
}

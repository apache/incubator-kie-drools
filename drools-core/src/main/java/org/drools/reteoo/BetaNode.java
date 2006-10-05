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
import org.drools.base.evaluators.Operator;
import org.drools.common.BaseNode;
import org.drools.common.BetaNodeConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.rule.VariableConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.PropagationContext;
import org.drools.util.FactHashTable;
import org.drools.util.FieldIndexHashTable;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.TupleHashTable;

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
    private final TupleSource           leftInput;

    /** The right input <code>TupleSource</code>. */
    private final ObjectSource          rightInput;

    protected final BetaNodeConstraints constraints;

    private TupleSinkNode               previousTupleSinkNode;
    private TupleSinkNode               nextTupleSinkNode;

    private ObjectSinkNode              previousObjectSinkNode;
    private ObjectSinkNode              nextObjectSinkNode;

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
              BetaNodeConstraints.emptyBetaNodeConstraints );
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
             final BetaNodeConstraints constraints) {
        super( id );
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.constraints = constraints;
    }

    public FieldConstraint[] getConstraints() {
        LinkedList constraints = this.constraints.getConstraints();

        FieldConstraint[] array = new FieldConstraint[constraints.size()];
        int i = 0;
        for ( LinkedListEntry entry = (LinkedListEntry) constraints.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
            array[i++] = (FieldConstraint) entry.getObject();
        }
        return array;
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
            this.leftInput.updateSink( this,
                                       propagationContext,
                                       workingMemory );
            this.rightInput.updateSink( this,
                                        propagationContext,
                                        workingMemory );
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

        return this.leftInput.equals( other.leftInput ) && this.rightInput.equals( other.rightInput ) && this.constraints.equals( other.constraints );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        // iterate over all the constraints untill we find one that is indexeable. When we find it we remove it from the list and create the 
        // BetaMemory for it. If we don't find one, we create a normal beta memory. We don't need the constraint as we can assume that 
        // anything  returned by the memory already passes that test.
        LinkedList constraints = this.constraints.getConstraints();
        BetaMemory memory = null;
        
        if ( constraints != null ) {
            for ( LinkedListEntry entry = (LinkedListEntry) constraints.getFirst(); entry != null; entry = (LinkedListEntry) entry.getNext() ) {
                FieldConstraint constraint = (FieldConstraint) entry.getObject();
                if ( constraint.getClass() == VariableConstraint.class ) {
                    VariableConstraint variableConstraint = (VariableConstraint) constraint;
                    FieldExtractor extractor = variableConstraint.getFieldExtractor();
                    Evaluator evaluator = variableConstraint.getEvaluator();
                    if ( evaluator.getOperator() == Operator.EQUAL ) {
                        // remove this entry                    
                        constraints.remove( entry );
                        memory = new BetaMemory( new TupleHashTable(),
                                                 new FieldIndexHashTable( extractor,
                                                                          variableConstraint.getRequiredDeclarations()[0] ) );
                        break;

                    }
                }
            }
        }
        
        if ( memory == null )  {
            memory = new BetaMemory( new TupleHashTable(),
                                     new FactHashTable() );            
        }
        
        return memory;
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

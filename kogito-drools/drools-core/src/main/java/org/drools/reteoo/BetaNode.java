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
import org.drools.common.BetaNodeBinder;
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
abstract class BetaNode extends TupleSource
    implements
    TupleSink,
    ObjectSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The left input <code>TupleSource</code>. */
    private final TupleSource    leftInput;

    /** The right input <code>TupleSource</code>. */
    private final ObjectSource   rightInput;

    private final BetaNodeBinder joinNodeBinder;

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

    public void attach(final ReteooWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final ReteooWorkingMemory workingMemory = workingMemories[i];
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
                       final ReteooWorkingMemory[] workingMemories) {
        if( !node.isInUse()) {
            getTupleSinks().remove( node );
        }
        removeShare();

        if ( ! this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.rightInput.remove( this,
                                workingMemories );
        this.leftInput.remove( this,
                               workingMemories );

    }

    /**
     * @return the <code>joinNodeBinder</code>
     */
    BetaNodeBinder getJoinNodeBinder() {
        return this.joinNodeBinder;
    }

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

}

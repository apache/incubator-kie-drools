package org.drools.reteoo;

/*
 * $Id: BetaNode.java,v 1.3 2005/08/14 22:44:12 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

/**
 * A two-input Rete-OO <i>join node </i>.
 * 
 * @see org.drools.reteoo.TupleSource
 * @see org.drools.reteoo.TupleSink
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
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

    private final int            column;

    // private final BetaNodeDecorator decorator;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    BetaNode(int id,
             TupleSource leftInput,
             ObjectSource rightInput,
             int column)// ,
    // BetaNodeDecorator decorator)
    {
        this( id,
              leftInput,
              rightInput,
              column,
              // decorator,
              new BetaNodeBinder() );
    }

    /**
     * Construct.
     * 
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>TupleSource</code>.
     */
    BetaNode(int id,
             TupleSource leftInput,
             ObjectSource rightInput,
             int column,
             // BetaNodeDecorator decorator,
             BetaNodeBinder joinNodeBinder) {
        super( id );
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.column = column;
        // this.decorator = decorator;
        this.joinNodeBinder = joinNodeBinder;

    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) throws FactException {
        this.attachingNewNode = true;

        TupleSource source = null;

        // iterate until we find a child with memory
        for ( Iterator it = this.getTupleSinks().iterator(); it.hasNext(); ) {
            source = (TupleSource) it.next();
            if ( source.hasMemory ) {
                break;
            }

        }

        if ( source != null && source.hasMemory() ) {
            // We have a child with memory so use its tuples
            Object object = workingMemory.getNodeMemory( (NodeMemory) source );
            if ( object instanceof BetaMemory ) {
                BetaMemory memory = (BetaMemory) object;
                memory.getLeftMemory();
                for ( Iterator it = memory.getLeftMemory().values().iterator(); it.hasNext(); ) {
                    ReteTuple tuple = ((TupleMatches) it.next()).getTuple();
                    propagateAssertTuple( tuple,
                                          context,
                                          workingMemory );
                }
            } else if ( object instanceof Map ) {
                Map map = (Map) object;
                for ( Iterator it = map.values().iterator(); it.hasNext(); ) {
                    propagateAssertTuple( (ReteTuple) it.next(),
                                          context,
                                          workingMemory );
                }
            }
        } else {
            // No children with memory so re-determine tuples
            // first get a reference to the left and right memories then nuke
            // and rebuild the memory
            // for the node. This will have the side effect of populating our
            // newly attached node
            BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
            Map map = memory.getLeftMemory();
            Set set = memory.getRightMemory();
            workingMemory.clearNodeMemory( this );
            memory = (BetaMemory) workingMemory.getNodeMemory( this );

            // first re-add all the right input facts
            for ( Iterator it = set.iterator(); it.hasNext(); ) {
                FactHandleImpl handle = (FactHandleImpl) it.next();
                Object object = workingMemory.getObject( handle );
                assertObject( object,
                              handle,
                              context,
                              workingMemory );
            }

            // now re-add all the tuples
            for ( Iterator it = map.values().iterator(); it.hasNext(); ) {
                ReteTuple tuple = ((TupleMatches) it.next()).getTuple();
                assertTuple( tuple,
                             context,
                             workingMemory );
            }
        }

        this.attachingNewNode = true;
    }

    /**
     * Propagate joined asserted tuples.
     * 
     * @param joinedTuples
     *            The tuples to propagate.
     * @param workingMemory
     *            The working memory session.
     * @throws AssertionException
     *             If an errors occurs while asserting.
     */
    protected void propagateAssertTuples(TupleSet joinedTuples,
                                         PropagationContext context,
                                         WorkingMemoryImpl workingMemory) throws FactException {
        Iterator tupleIter = joinedTuples.iterator();
        while ( tupleIter.hasNext() ) {
            propagateAssertTuple( (ReteTuple) tupleIter.next(),
                                  context,
                                  workingMemory );
        }
    }

    /**
     * Propagate joined asserted tuples.
     * 
     * @param joinedTuples
     *            The tuples to propagate.
     * @param workingMemory
     *            The working memory session.
     * @throws AssertionException
     *             If an errors occurs while asserting.
     * @throws RetractionException
     */
    protected void propagateRetractTuples(List keys,
                                           PropagationContext context,
                                           WorkingMemoryImpl workingMemory) throws FactException {
        Iterator it = keys.iterator();
        while ( it.hasNext() ) {
            propagateRetractTuples( (TupleKey) it.next(),
                                    context,
                                    workingMemory );
        }
    }

    int getColumn() {
        return this.column;
    }

    public void attach() {
        this.leftInput.addTupleSink( this );
        this.rightInput.addObjectSink( this );
    }

    public void remove() {

    }

    BetaNodeBinder getJoinNodeBinder() {
        return this.joinNodeBinder;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString() {
        // return "[JoinNode: common=" + this.commonDeclarations + "; decls=" +
        // this.tupleDeclarations + "]";
        return "";
    }

    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode();
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        BetaNode other = (BetaNode) object;

        return this.leftInput.equals( other.leftInput ) && this.rightInput.equals( other.rightInput ) && this.joinNodeBinder.equals( other.joinNodeBinder );
    }

    public Object createMemory() {
        return new BetaMemory();
    }

}

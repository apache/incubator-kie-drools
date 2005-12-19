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
    NodeMemory
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The left input <code>TupleSource</code>. */
    private final TupleSource       leftInput;

    /** The right input <code>TupleSource</code>. */
    private final ObjectSource      rightInput;

    private final BetaNodeBinder    joinNodeBinder;

    private final int               column;

    //private final BetaNodeDecorator decorator;

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
             int column)//,
             //BetaNodeDecorator decorator)
    {
        this( id,
              leftInput,
              rightInput,
              column,
              //decorator,
              new BetaNodeBinder( ) );
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
             //BetaNodeDecorator decorator,
             BetaNodeBinder joinNodeBinder)
    {
        super( id );
        this.leftInput = leftInput;
        this.rightInput = rightInput;
        this.column = column;
        //this.decorator = decorator;
        this.joinNodeBinder = joinNodeBinder;

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
                                       WorkingMemoryImpl workingMemory) throws FactException
    {
        Iterator tupleIter = joinedTuples.iterator( );
        while ( tupleIter.hasNext( ) )
        {
            propagateAssertTuple( (ReteTuple) tupleIter.next( ),
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
    protected void propagateRectractTuples(List keys,
                                         PropagationContext context,
                                         WorkingMemoryImpl workingMemory) throws FactException
    {
        Iterator it = keys.iterator( );
        while ( it.hasNext( ) )
        {
            propagateRetractTuples( (TupleKey) it.next( ),
                                    context,
                                    workingMemory );
        }
    }

    int getColumn()
    {
        return this.column;
    }

    public void attach()
    {
        this.leftInput.addTupleSink( this );
        this.rightInput.addObjectSink( this );
    }
    

    BetaNodeBinder getJoinNodeBinder()
    {
        return this.joinNodeBinder;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public String toString()
    {
        // return "[JoinNode: common=" + this.commonDeclarations + "; decls=" +
        // this.tupleDeclarations + "]";
        return "";
    }

    public int hashCode()
    {
        return this.leftInput.hashCode( ) ^ this.rightInput.hashCode( );
    }

    public boolean equals(Object object)
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null || getClass( ) != object.getClass( ) )
        {
            return false;
        }

        BetaNode other = (BetaNode) object;

        return this.leftInput.equals( other.leftInput ) && this.rightInput.equals( other.rightInput ) && this.joinNodeBinder.equals( other.joinNodeBinder ) ;
    }

    public Object createMemory()
    {
        return new BetaMemory( );
    }

}

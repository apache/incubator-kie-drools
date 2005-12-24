package org.drools.reteoo;

/*
 * $Id: ObjectSource.java,v 1.4 2005/08/14 22:44:12 mproctor Exp $
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.PropagationContext;

/**
 * A source of <code>ReteTuple</code> s for a <code>TupleSink</code>.
 * 
 * <p>
 * Nodes that propagate <code>Tuples</code> extend this class.
 * </p>
 * 
 * @see ObjectSource
 * @see ReteTuple
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
abstract class ObjectSource extends BaseNode
    implements
    Serializable
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    private List objectSinks = new ArrayList( 1 );        

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     */
    ObjectSource(int id)
    {
        super( id );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addObjectSink(ObjectSink objectSink)
    {
        if ( !this.objectSinks.contains( objectSink ) )
        {
            this.objectSinks.add( objectSink );
        }        
    }    
    
    protected void removeObjectSink(ObjectSink objectSink)
    {
        this.objectSinks.remove( objectSink );
    }      
    

    /**
     * Propagate the assertion of a <code>Tuple</code> to this node's
     * <code>TupleSink</code>.
     * 
     * @param tuple
     *            The <code>Tuple</code> to propagate.
     * @param workingMemory
     *            the working memory session.
     * 
     * @throws AssertionException
     *             If an errors occurs while attempting assertion.
     */
    protected void propagateAssertObject(Object object,
                                         FactHandleImpl handle,
                                         PropagationContext context, 
                                         WorkingMemoryImpl workingMemory) throws FactException
    {
        if ( ! this.attachingNewNode )
        {
            for ( int i = 0, size = this.objectSinks.size( ); i < size; i++ )
            {
                ((ObjectSink) this.objectSinks.get( i )).assertObject( object,
                                                                       handle,
                                                                       context,
                                                                       workingMemory );
            }
        }
        else
        {
            ((ObjectSink) this.objectSinks.get( this.objectSinks.size( ) - 1 )).assertObject( object,
                                                                                              handle,
                                                                                              context,
                                                                                              workingMemory );            
        }
    }

    /**
     * Propagate the retration of a <code>Tuple</code> to this node's
     * <code>TupleSink</code>.
     * 
     * @param key
     *            The tuple key.
     * @param workingMemory
     *            The working memory session.
     * 
     * @throws RetractionException
     *             If an error occurs while attempting retraction
     * 
     */
    protected void propagateRetractObject(FactHandleImpl handle,
                                          PropagationContext context, 
                                          WorkingMemoryImpl workingMemory) throws FactException
    {
        for ( int i = 0, size = this.objectSinks.size( ); i < size; i++ )
        {
            ((ObjectSink) this.objectSinks.get( i )).retractObject( handle,
                                                                    context,
                                                                    workingMemory );
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.reteoo.TupleSource
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Retrieve the <code>TupleSinks</code> that receive propagated
     * <code>Tuples</code>.
     * 
     * @return The <code>TupleSinks</code> that receive propagated
     *         <code>Tuples</code>.
     */
    public List getObjectSinks()
    {
        return this.objectSinks;
    }                   
    
}

package org.drools.reteoo;

/*
 * $Id: ObjectTypeNode.java,v 1.3 2005/08/14 22:44:12 mproctor Exp $
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactObjectException;
import org.drools.RetractionException;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.PrimitiveLongMap;

/**
 * Filters <code>Objects</code> coming from the <code>Rete</code> using a
 * <code>ObjectType</code> semantic module.
 * 
 * <p>
 * It receives <code>Objects</code> from the <code>Rete</code>, uses a
 * <code>ObjectType</code> instance to determine membership, and propagates
 * matching <code>Objects</code> further to all matching
 * <code>ParameterNode</code>s.
 * </p>
 * 
 * @see ObjectType
 * @see ParameterNode
 * @see Rete
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob@eng.werken.com </a>
 */
class ObjectTypeNode extends ObjectSource
    implements
    ObjectSink,
    Serializable,
    NodeMemory

{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The <code>ObjectType</code> semantic module. */
    private final ObjectType   objectType;

    private final ObjectSource objectSource;    

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct given a semantic <code>ObjectType</code>.
     * 
     * @param objectType
     *            The semantic object-type differentiator.
     */
    public ObjectTypeNode(int id,
                          ObjectType objectType,
                          ObjectSource objectSource)
    {
        super( id );
        this.objectSource = objectSource;
        this.objectType = objectType;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the semantic <code>ObjectType</code> differentiator.
     * 
     * @return The semantic <code>ObjectType</code> differentiator.
     */
    public ObjectType getObjectType()
    {
        return this.objectType;
    }

    public int getId()
    {
        return this.id;
    }

    /**
     * Assert a new fact object into this <code>RuleBase</code> and the
     * specified <code>WorkingMemory</code>.
     * 
     * @param handle
     *            The fact handle.
     * @param object
     *            The object to assert.
     * @param workingMemory
     *            The working memory session.
     * @throws FactException
     *             if an error occurs during assertion.
     */
    public void assertObject(Object object,
                             FactHandleImpl handle,
                             PropagationContext context, 
                             WorkingMemoryImpl workingMemory) throws FactException
    {
        if ( this.objectType.matches( object ) )
        {
            PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );
            memory.put( handle.getId(),
                        handle );
            
            propagateAssertObject( object,
                                   handle,
                                   context,
                                   workingMemory );
        }

    }

    /**
     * Retract a fact object from this <code>RuleBase</code> and the specified
     * <code>WorkingMemory</code>.
     * 
     * @param handle
     *            The handle of the fact to retract.
     * @param workingMemory
     *            The working memory session.
     * @throws FactException
     *             if an error occurs during assertion.
     */
    public void retractObject(FactHandleImpl handle,
                              PropagationContext context, 
                              WorkingMemoryImpl workingMemory) throws FactException
    {
        try
        {
            if ( this.objectType.matches( workingMemory.getObject( handle ) ) )
            {
                PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );
                memory.remove( handle.getId() );

                propagateRetractObject( handle,
                                        context,
                                        workingMemory );
            }
        }
        catch ( NoSuchFactObjectException e )
        {
            throw new RetractionException( e );
        }
    }
    
    public void updateNewRule( WorkingMemoryImpl workingMemory,
                               PropagationContext context ) throws FactException
    {
        /* if this ObjectTypeNode has not been marked that a rule is being attached
         * then there is no updating to do, so return.
         */
        if ( !isAttachingNewRule() )
        {
            return;
        }
        PrimitiveLongMap memory = (PrimitiveLongMap) workingMemory.getNodeMemory( this );
        Iterator it = memory.values().iterator();
                
        FactHandleImpl handle = null;
        Object object = null;
        while ( it.hasNext() )
        {            
            handle = (FactHandleImpl) it.next();
            try
            {
                object = workingMemory.getObject( handle );
                assertObject( object,
                              handle,
                              context,
                              workingMemory );
            }
            catch ( NoSuchFactObjectException e )
            {
                // do nothing, shouldn't happen
            }
        }
    }    

    public void attach()
    {
        this.objectSource.addObjectSink( this );
    }         
    
    public void remove()
    {
        this.objectSource.addObjectSink( this );
    }      

    public Object createMemory()
    {
        return new PrimitiveLongMap( 32,
                                     8 );
    }
    
}

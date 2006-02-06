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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

/**
 * A source of <code>FactHandle</code>s for an <code>ObjectSink</code>.
 * 
 * <p>
 * Nodes that propagate <code>FactHandleImpl</code> extend this class.
 * </p>
 * 
 * @see ObjectSource
 * @see FactHandleImpl
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
abstract class ObjectSource extends BaseNode
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>FactHandleImpl</code>. */
    private List objectSinks = new ArrayList( 1 );

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    
    /**
     * Single parameter constructor that specifies the unique id of the node.
     * @param id
     */    
    ObjectSource(int id) {
        super( id );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Adds the <code>ObjectSink</code> so that it may receive
     * <code>FactHandleImpl</code> propagated from this <code>ObjectSource</code>.
     * 
     * @param objectSink
     *            The <code>ObjectSink</code> to receive propagated
     *            <code>FactHandleImpl</code>.
     */
    protected void addObjectSink(ObjectSink objectSink) {
        if ( !this.objectSinks.contains( objectSink ) ) {
            this.objectSinks.add( objectSink );
        }
    }

    /**
     * Removes the <code>ObjectSink</code> 
     * 
     * @param objectSink
     *            The <code>ObjectSink</code> to remove
     */
    protected void removeObjectSink(ObjectSink objectSink) {
        this.objectSinks.remove( objectSink );
    }

    /**
     * Propagate the assertion of a <code>FactHandleImpl/code> to this node's
     * <code>ObjectSink</code>s.
     * 
     * @param handle
     *           the FactHandleImpl to be asserted
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action            
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    protected void propagateAssertObject(FactHandleImpl handle,
                                         PropagationContext context,
                                         WorkingMemoryImpl workingMemory) {
        if ( !this.attachingNewNode ) {
            for ( int i = 0, size = this.objectSinks.size(); i < size; i++ ) {
                ((ObjectSink) this.objectSinks.get( i )).assertObject( handle,
                                                                       context,
                                                                       workingMemory );
            }
        } else {
            ((ObjectSink) this.objectSinks.get( this.objectSinks.size() - 1 )).assertObject( handle,
                                                                                             context,
                                                                                             workingMemory );
        }
    }

    /**
     * Propagate the retration of a <code>FactHandleImpl/code> to this node's
     * <code>ObjectSink</code>.
     * 
     * @param handle
     *           the FactHandleImpl to be retractred
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action            
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     * 
     */
    protected void propagateRetractObject(FactHandleImpl handle,
                                          PropagationContext context,
                                          WorkingMemoryImpl workingMemory) {
        for ( int i = 0, size = this.objectSinks.size(); i < size; i++ ) {
            ((ObjectSink) this.objectSinks.get( i )).retractObject( handle,
                                                                    context,
                                                                    workingMemory );
        }
    }
    
    /**
     * Retrieve the <code>ObectsSinks</code> that receive propagated
     * <code>FactHandleImpl</code>s.
     * 
     * @return The <code>ObjectsSinks</code> that receive propagated
     *         <code>FactHandles</code>.
     */
    public List getObjectSinks() {
        return this.objectSinks;
    }

}

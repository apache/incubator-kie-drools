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

import org.drools.spi.PropagationContext;

/**
 * A source of <code>ReteTuple</code> s for a <code>TupleSink</code>.
 * 
 * <p>
 * Nodes that propagate <code>Tuples</code> extend this class.
 * </p>
 * 
 * @see TupleSource
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
abstract class TupleSource extends BaseNode
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    private List tupleSinks = new ArrayList( 1 );

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------   
    
    /**
     * Single parameter constructor that specifies the unique id of the node.
     * @param id
     */
    TupleSource(int id) {
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
    protected void addTupleSink(TupleSink tupleSink) {
        if ( !this.tupleSinks.contains( tupleSink ) ) {
            this.tupleSinks.add( tupleSink );
        }
    }

    /**
     * Removes the <code>TupleSink</code> 
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to remove
     */
    protected void removeTupleSink(TupleSink tupleSink) {
        this.tupleSinks.remove( tupleSink );
    }

    /**
     * Propagate the assertion of a <code>ReteTuple</code> to this node's
     * <code>TupleSink</code>.
     * 
     * @param tuple
     *            The <code>ReteTuple</code> to propagate.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action            
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    protected void propagateAssertTuple(ReteTuple tuple,
                                        PropagationContext context,
                                        WorkingMemoryImpl workingMemory) {
        if ( !this.attachingNewNode ) {
            for ( int i = 0, size = this.tupleSinks.size(); i < size; i++ ) {
                ((TupleSink) this.tupleSinks.get( i )).assertTuple( tuple,
                                                                    context,
                                                                    workingMemory );
            }
        } else {
            ((TupleSink) this.tupleSinks.get( this.tupleSinks.size() - 1 )).assertTuple( tuple,
                                                                                         context,
                                                                                         workingMemory );
        }
    }

    /**
     * Retrieve the <code>TupleSinks</code> that receive propagated
     * <code>Tuples</code>s.
     * 
     * @return The <code>TupleSinks</code> that receive propagated
     *         <code>Tuples</code>.
     */
    public List getTupleSinks() {
        return this.tupleSinks;
    }

}

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
import java.util.Iterator;
import java.util.List;

import org.drools.common.BaseNode;
import org.drools.common.DefaultFactHandle;
import org.drools.spi.PropagationContext;

/**
 * A source of <code>FactHandle</code>s for an <code>ObjectSink</code>.
 * 
 * <p>
 * Nodes that propagate <code>FactHandleImpl</code> extend this class.
 * </p>
 * 
 * @see ObjectSource
 * @see DefaultFactHandle
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
    protected ObjectSinkPropagator sink;
    
    protected ObjectSource objectSource;   

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Single parameter constructor that specifies the unique id of the node.
     * 
     * @param id
     */
    ObjectSource(final int id) {
        this( id,
              null );
    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     * 
     * @param id
     */
    ObjectSource(final int id,
                 final ObjectSource objectSource) {
        super( id );
        this.objectSource = objectSource;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Adds the <code>ObjectSink</code> so that it may receive
     * <code>FactHandleImpl</code> propagated from this
     * <code>ObjectSource</code>.
     * 
     * @param objectSink
     *            The <code>ObjectSink</code> to receive propagated
     *            <code>FactHandleImpl</code>.
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        if ( this.sink == null ) {
            this.sink = new SingleObjectSinkAdapter( objectSink );
        } else if ( this.sink.getClass() == SingleObjectSinkAdapter.class ) {
            CompositeObjectSinkAdapter sinkAdapter = ( CompositeObjectSinkAdapter ) new CompositeObjectSinkAdapter();
            sinkAdapter.addObjectSink( this.sink.getSinks()[0] );
            sinkAdapter.addObjectSink( objectSink );
            this.sink = sinkAdapter;
        } else {
            ( (CompositeObjectSinkAdapter) sink ).addObjectSink( objectSink );
        }
    }

    /**
     * Removes the <code>ObjectSink</code>
     * 
     * @param objectSink
     *            The <code>ObjectSink</code> to remove
     */
    protected void removeObjectSink(final ObjectSink objectSink) {
        if ( this.sink.getClass() == SingleObjectSinkAdapter.class ) {
            this.sink = null;
        } else { 
            CompositeObjectSinkAdapter sinkAdapter = ( CompositeObjectSinkAdapter ) objectSink;
            sinkAdapter.removeObjectSink( objectSink );
            if ( sinkAdapter.size() == 1 ) {
                this.sink = new SingleObjectSinkAdapter( sinkAdapter.getSinks()[0] );
            }
        }         
    }
}

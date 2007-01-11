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

import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
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
public abstract class TupleSource extends BaseNode
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected TupleSinkPropagator sink;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Single parameter constructor that specifies the unique id of the node.
     * 
     * @param id
     */
    TupleSource(final int id) {
        super( id );
        this.sink = EmptyTupleSinkAdapter.getInstance();
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
    protected void addTupleSink(final TupleSink tupleSink) {
        if ( this.sink == EmptyTupleSinkAdapter.getInstance() ) {
            this.sink = new SingleTupleSinkAdapter( tupleSink );
        } else if ( this.sink.getClass() == SingleTupleSinkAdapter.class ) {
            final CompositeTupleSinkAdapter sinkAdapter = new CompositeTupleSinkAdapter();
            sinkAdapter.addTupleSink( this.sink.getSinks()[0] );
            sinkAdapter.addTupleSink( tupleSink );
            this.sink = sinkAdapter;
        } else {
            ((CompositeTupleSinkAdapter) this.sink).addTupleSink( tupleSink );
        }
    }

    /**
     * Removes the <code>TupleSink</code>
     * 
     * @param tupleSink
     *            The <code>TupleSink</code> to remove
     */
    protected void removeTupleSink(final TupleSink tupleSink) {
        if (  this.sink == EmptyTupleSinkAdapter.getInstance()){
            throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );            
        }
        
        if ( this.sink.getClass() == SingleTupleSinkAdapter.class ) {
            this.sink = EmptyTupleSinkAdapter.getInstance();
        } else {
            final CompositeTupleSinkAdapter sinkAdapter = (CompositeTupleSinkAdapter) this.sink;
            sinkAdapter.removeTupleSink( tupleSink );
            if ( sinkAdapter.size() == 1 ) {
                this.sink = new SingleTupleSinkAdapter( sinkAdapter.getSinks()[0] );
            }
        }
    }

    public TupleSinkPropagator getSinkPropagator() {
        return this.sink;
    }
    
    public abstract void updateSink(TupleSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

}

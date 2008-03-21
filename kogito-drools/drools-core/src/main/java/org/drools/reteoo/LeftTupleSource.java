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
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

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
 * @see LeftTupleSource
 * @see LeftTuple
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public abstract class LeftTupleSource extends BaseNode
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected LeftTupleSinkPropagator sink;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public LeftTupleSource() {

    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     *
     * @param id
     */
    LeftTupleSource(final int id) {
        super( id );
        this.sink = EmptyLeftTupleSinkAdapter.getInstance();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        sink    = (LeftTupleSinkPropagator)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(sink);
    }

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addTupleSink(final LeftTupleSink tupleSink) {
        if ( this.sink instanceof EmptyLeftTupleSinkAdapter ) {
            this.sink = new SingleLeftTupleSinkAdapter( tupleSink );
        } else if ( this.sink instanceof SingleLeftTupleSinkAdapter ) {
            final CompositeLeftTupleSinkAdapter sinkAdapter = new CompositeLeftTupleSinkAdapter();
            sinkAdapter.addTupleSink( this.sink.getSinks()[0] );
            sinkAdapter.addTupleSink( tupleSink );
            this.sink = sinkAdapter;
        } else {
            ((CompositeLeftTupleSinkAdapter) this.sink).addTupleSink( tupleSink );
        }
    }

    /**
     * Removes the <code>TupleSink</code>
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to remove
     */
    protected void removeTupleSink(final LeftTupleSink tupleSink) {
        if ( this.sink instanceof EmptyLeftTupleSinkAdapter ) {
            throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );
        }

        if ( this.sink instanceof SingleLeftTupleSinkAdapter ) {
            this.sink = EmptyLeftTupleSinkAdapter.getInstance();
        } else {
            final CompositeLeftTupleSinkAdapter sinkAdapter = (CompositeLeftTupleSinkAdapter) this.sink;
            sinkAdapter.removeTupleSink( tupleSink );
            if ( sinkAdapter.size() == 1 ) {
                this.sink = new SingleLeftTupleSinkAdapter( sinkAdapter.getSinks()[0] );
            }
        }
    }

    public LeftTupleSinkPropagator getSinkPropagator() {
        return this.sink;
    }

    public abstract void updateSink(LeftTupleSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

    public boolean isInUse() {
        return this.sink.size() > 0;
    }
}

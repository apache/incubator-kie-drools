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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
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
    LeftTupleSource(final int id, final RuleBasePartitionId partitionId, final boolean partitionsEnabled ) {
        super( id, partitionId, partitionsEnabled );
        this.sink = EmptyLeftTupleSinkAdapter.getInstance();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (LeftTupleSinkPropagator) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
    }

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    public void addTupleSink(final LeftTupleSink tupleSink) {
        if ( this.sink instanceof EmptyLeftTupleSinkAdapter ) {
            if( this.partitionsEnabled && ! this.partitionId.equals( tupleSink.getPartitionId() ) ) {
                // if partitions are enabled and the next node belongs to a different partition,
                // we need to use the asynchronous propagator
                this.sink = new AsyncSingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
            } else {
                // otherwise, we use the lighter synchronous propagator
                this.sink = new SingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
            }
        } else if ( this.sink instanceof SingleLeftTupleSinkAdapter ) {
            final CompositeLeftTupleSinkAdapter sinkAdapter;
            if( this.partitionsEnabled ) {
                // a composite propagator may propagate to both nodes in the same partition
                // as well as in a different partition, so, if partitions are enabled, we
                // must use the asynchronous version
                sinkAdapter = new AsyncCompositeLeftTupleSinkAdapter( this.getPartitionId() );
            } else {
                // if partitions are disabled, then it is safe to use the lighter synchronous propagator
                sinkAdapter = new CompositeLeftTupleSinkAdapter( this.getPartitionId() );
            }
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
                if( this.partitionsEnabled && ! this.partitionId.equals( tupleSink.getPartitionId() ) ) {
                    // if partitions are enabled and the next node belongs to a different partition,
                    // we need to use the asynchronous propagator
                    this.sink = new AsyncSingleLeftTupleSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                } else {
                    // otherwise, we use the lighter synchronous propagator
                    this.sink = new SingleLeftTupleSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                }
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

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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.BaseNode;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.RuleBasePartitionId;
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
public abstract class ObjectSource extends BaseNode
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>FactHandleImpl</code>. */
    protected ObjectSinkPropagator sink;

    protected ObjectSource         source;

    private int                    alphaNodeHashingThreshold;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ObjectSource() {

    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     *
     * @param id
     */
    ObjectSource(final int id,
                 final RuleBasePartitionId partitionId,
                 final boolean partitionsEnabled) {
        this( id,
              partitionId,
              partitionsEnabled,
              null,
              3 );
    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     *
     * @param id
     */
    ObjectSource(final int id,
                 final RuleBasePartitionId partitionId,
                 final boolean partitionsEnabled,
                 final ObjectSource objectSource,
                 final int alphaNodeHashingThreshold) {
        super( id, partitionId, partitionsEnabled );
        this.source = objectSource;
        this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
        this.sink = EmptyObjectSinkAdapter.getInstance();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (ObjectSinkPropagator) in.readObject();
        source = (ObjectSource) in.readObject();
        alphaNodeHashingThreshold = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
        out.writeObject( source );
        out.writeInt( alphaNodeHashingThreshold );
    }

    /**
     * Adds the <code>ObjectSink</code> so that it may receive
     * <code>FactHandleImpl</code> propagated from this
     * <code>ObjectSource</code>.
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to receive propagated
     *            <code>FactHandleImpl</code>.
     */
    public void addObjectSink(final ObjectSink objectSink) {
        if ( this.sink instanceof EmptyObjectSinkAdapter ) {
            if( this.partitionsEnabled && ! this.getPartitionId().equals( objectSink.getPartitionId() ) ) {
                // if partitions are enabled and the next node belongs to a different partition,
                // we need to use the asynchronous propagator
                this.sink = new AsyncSingleObjectSinkAdapter( this.getPartitionId(), objectSink );
            } else {
                // otherwise, we use the lighter synchronous propagator
                this.sink = new SingleObjectSinkAdapter( this.getPartitionId(), objectSink );
            }
        } else if ( this.sink instanceof SingleObjectSinkAdapter ) {
            final CompositeObjectSinkAdapter sinkAdapter;
            if( this.partitionsEnabled ) {
                // a composite propagator may propagate to both nodes in the same partition
                // as well as in a different partition, so, if partitions are enabled, we
                // must use the asynchronous version
                sinkAdapter = new AsyncCompositeObjectSinkAdapter( this.getPartitionId(), this.alphaNodeHashingThreshold );
            } else {
                // if partitions are disabled, then it is safe to use the lighter synchronous propagator
                sinkAdapter = new CompositeObjectSinkAdapter( this.getPartitionId(), this.alphaNodeHashingThreshold );
            }
            sinkAdapter.addObjectSink( this.sink.getSinks()[0] );
            sinkAdapter.addObjectSink( objectSink );
            this.sink = sinkAdapter;
        } else {
            ((CompositeObjectSinkAdapter) this.sink).addObjectSink( objectSink );
        }
    }

    /**
     * Removes the <code>ObjectSink</code>
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to remove
     */
    protected void removeObjectSink(final ObjectSink objectSink) {
        if ( this.sink instanceof EmptyObjectSinkAdapter ) {
            throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );
        }

        if ( this.sink instanceof SingleObjectSinkAdapter ) {
            this.sink = EmptyObjectSinkAdapter.getInstance();
        } else {
            final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) this.sink;
            sinkAdapter.removeObjectSink( objectSink );
            if ( sinkAdapter.size() == 1 ) {
                if( this.partitionsEnabled && ! this.getPartitionId().equals( sinkAdapter.getSinks()[0].getPartitionId() ) ) {
                    // if partitions are enabled and the next node belongs to a different partition,
                    // we need to use the asynchronous propagator
                    this.sink = new AsyncSingleObjectSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                } else {
                    // otherwise, we use the lighter synchronous propagator
                    this.sink = new SingleObjectSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                }
            }
        }
    }

    public abstract void updateSink(ObjectSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

    public void networkUpdated() {
        this.source.networkUpdated();
    }

    public ObjectSinkPropagator getSinkPropagator() {
        return this.sink;
    }

    public boolean isInUse() {
        return this.sink.size() > 0;
    }
    
    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeObjectSink( (ObjectSink) node );
        }
        if ( !this.isInUse() && this instanceof NodeMemory ) {
            for( InternalWorkingMemory workingMemory : workingMemories ) {
                workingMemory.clearNodeMemory( (NodeMemory) this );
            }
        }
        this.source.remove( context,
                            builder,
                            this,
                            workingMemories );
    }
}

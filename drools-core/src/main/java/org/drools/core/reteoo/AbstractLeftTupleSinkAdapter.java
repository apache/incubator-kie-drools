package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.common.RuleBasePartitionId;

/**
 * An abstract super class for the LeftTupleSinkAdapters
 */
public abstract class AbstractLeftTupleSinkAdapter 
    implements
    LeftTupleSinkPropagator {

    protected RuleBasePartitionId partitionId;

    protected AbstractLeftTupleSinkAdapter( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
    }

    protected AbstractLeftTupleSinkAdapter() {
    }

    public void writeExternal(ObjectOutput out ) throws IOException {
        out.writeObject( this.partitionId );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        this.partitionId = (RuleBasePartitionId) in.readObject();
    }

    /**
     * Returns the partition to which this propagator belongs to
     *
     * @return the ID of the partition
     */
    public RuleBasePartitionId getPartitionId() {
        return this.partitionId;
    }

    /**
     * Sets the partition to which this propagator belongs to
     *
     * @param partitionId
     */
    public void setPartitionId( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
    }

}

/**
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.RuleBasePartitionId;

/**
 * An abstract super class for the LeftTupleSinkAdapters
 * @author: <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public abstract class AbstractLeftTupleSinkAdapter 
    implements
    LeftTupleSinkPropagator {

    protected RuleBasePartitionId partitionId;

    protected AbstractLeftTupleSinkAdapter( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
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

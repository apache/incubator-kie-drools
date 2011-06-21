/*
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

import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.PartitionTaskManager.Action;
import org.drools.spi.PropagationContext;

public class AsyncCompositeLeftTupleSinkAdapter extends CompositeLeftTupleSinkAdapter {

    public AsyncCompositeLeftTupleSinkAdapter() {
    }

    public AsyncCompositeLeftTupleSinkAdapter( RuleBasePartitionId partitionId ) {
        super( partitionId );
    }

    protected void doPropagateAssertLeftTuple( PropagationContext context, InternalWorkingMemory workingMemory,
                                               LeftTupleSinkNode sink, LeftTuple leftTuple ) {
        // composite propagators need to check each node to decide if the propagation
        // must be asynchronous or may eventually be synchronous
        if( this.partitionId.equals( sink.getPartitionId() )) {
            // same partition, so synchronous propagation is fine
            sink.assertLeftTuple( leftTuple, context, workingMemory );
        } else {
            // different partition, so use asynchronous propagation
            PartitionTaskManager manager = workingMemory.getPartitionTaskManager( this.partitionId );
            manager.enqueue( new PartitionTaskManager.LeftTupleAssertAction( leftTuple, context, sink, Action.PRIORITY_HIGH ) );
        }
    }

    protected void doPropagateRetractLeftTuple( PropagationContext context, InternalWorkingMemory workingMemory,
                                                LeftTupleImpl leftTuple, LeftTupleSink sink ) {
        // composite propagators need to check each node to decide if the propagation
        // must be asynchronous or may eventually be synchronous
        if( this.partitionId.equals( sink.getPartitionId() )) {
            // same partition, so synchronous propagation is fine
            sink.retractLeftTuple( leftTuple, context, workingMemory );
        } else {
            // different partition, so use asynchronous propagation
            PartitionTaskManager manager = workingMemory.getPartitionTaskManager( this.partitionId );
            manager.enqueue( new PartitionTaskManager.LeftTupleRetractAction( leftTuple, context, sink, Action.PRIORITY_HIGH ) );
        }
    }
}

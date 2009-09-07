/*
 * Copyright 2008 JBoss Inc
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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.spi.PropagationContext;

import com.sun.corba.se.spi.orbutil.fsm.Action;

/**
 * This is an asynchronous implementation of the CompositeObjectSinkAdapter that
 * is used to propagate facts both between nodes in the same or in different
 * partitions when partitions are enabled in the rulebase
 *
 * @author: <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class AsyncCompositeObjectSinkAdapter extends CompositeObjectSinkAdapter {

    public AsyncCompositeObjectSinkAdapter() {
    }

    public AsyncCompositeObjectSinkAdapter( RuleBasePartitionId partitionId, int alphaNodeHashingThreshold ) {
        super(partitionId, alphaNodeHashingThreshold );
    }

    protected void doPropagateAssertObject( InternalFactHandle factHandle, PropagationContext context,
                                            InternalWorkingMemory workingMemory, ObjectSink sink ) {
        // composite propagators need to check each node to decide if the propagation
        // must be asynchronous or may eventually be synchronous
        if( this.partitionId.equals( sink.getPartitionId() )) {
            // same partition, so synchronous propagation is fine
            sink.assertObject( factHandle, context, workingMemory );
        } else {
            int priority = org.drools.reteoo.PartitionTaskManager.Action.PRIORITY_HIGH;
            if( this.partitionId.equals( RuleBasePartitionId.MAIN_PARTITION ) ) {
                priority = org.drools.reteoo.PartitionTaskManager.Action.PRIORITY_NORMAL;
            }
            // different partition, so use asynchronous propagation
            PartitionTaskManager manager = workingMemory.getPartitionTaskManager( sink.getPartitionId() );
            manager.enqueue( new PartitionTaskManager.FactAssertAction(factHandle, context, sink, priority ) );
        }
    }
}

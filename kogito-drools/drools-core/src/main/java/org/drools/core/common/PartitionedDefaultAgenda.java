/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.spi.PropagationContext;

public class PartitionedDefaultAgenda extends DefaultAgenda {

    private final int partition;

    PartitionedDefaultAgenda(InternalKnowledgeBase kBase,
                             boolean initMain,
                             ExecutionStateMachine executionStateMachine,
                             int partition) {
        super(kBase, initMain, executionStateMachine);
        this.partition = partition;
    }

    @Override
    protected void doRetract( PropagationContext ectx ) {
        InternalFactHandle factHandle = ectx.getFactHandle();
        ObjectTypeNode.retractLeftTuples( factHandle, ectx, workingMemory, partition );
        ObjectTypeNode.retractRightTuples( factHandle, ectx, workingMemory, partition );
        if ( isMasterPartition() && factHandle.isPendingRemoveFromStore() ) {
            String epId = factHandle.getEntryPoint().getEntryPointId();
            ( (InternalWorkingMemoryEntryPoint) workingMemory.getEntryPoint( epId ) ).removeFromObjectStore( factHandle );
        }
    }

    private boolean isMasterPartition() {
        return partition == 0;
    }
}

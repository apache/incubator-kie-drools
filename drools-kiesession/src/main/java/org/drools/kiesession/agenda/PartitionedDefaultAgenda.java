package org.drools.kiesession.agenda;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.ObjectTypeNode;

public class PartitionedDefaultAgenda extends DefaultAgenda {

    private final int partition;

    PartitionedDefaultAgenda(InternalWorkingMemory workingMemory,
                             ExecutionStateMachine executionStateMachine,
                             int partition) {
        super(workingMemory, executionStateMachine);
        this.partition = partition;
    }

    /**
     * Do not use this constructor! It should be used just by deserialization.
     */
    @Override
    protected void retractFactHandle(PropagationContext ectx, InternalFactHandle factHandle) {
        ObjectTypeNode.retractLeftTuples(factHandle, ectx, workingMemory, partition );
        ObjectTypeNode.retractRightTuples(factHandle, ectx, workingMemory, partition );
    }

    @Override
    protected boolean isPendingRemoveFactHandleFromStore(InternalFactHandle factHandle) {
        return isMainPartition() && super.isPendingRemoveFactHandleFromStore(factHandle);
    }

    private boolean isMainPartition() {
        return partition == 0;
    }
}

package org.drools.core.phreak.actions;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;

public class PartitionedDelete extends AbstractPartitionedPropagationEntry<ReteEvaluator> {
    private final InternalFactHandle handle;
    private final PropagationContext context;
    private final ObjectTypeConf     objectTypeConf;

    PartitionedDelete(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, int partition) {
        super(partition);
        this.handle         = handle;
        this.context        = context;
        this.objectTypeConf = objectTypeConf;
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes();

        if (cachedNodes == null) {
            // it is  possible that there are no ObjectTypeNodes for an  object being retracted
            return;
        }

        for (ObjectTypeNode cachedNode : cachedNodes) {
            cachedNode.retractObject(handle, context, reteEvaluator, partition);
        }

        if (handle.isEvent() && isMainPartition()) {
            ((DefaultEventHandle) handle).unscheduleAllJobs(reteEvaluator);
        }
    }

    @Override
    public String toString() {
        return "Delete of " + handle.getObject() + " for partition " + partition;
    }
}

package org.drools.core.phreak.actions;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.CompositePartitionAwareObjectSinkAdapter;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;

import static org.drools.core.reteoo.EntryPointNode.removeRightTuplesMatchingOTN;

public class PartitionedUpdate extends AbstractPartitionedPropagationEntry<ReteEvaluator> {
    private final InternalFactHandle handle;
    private final PropagationContext context;
    private final ObjectTypeConf     objectTypeConf;

    PartitionedUpdate(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf, int partition) {
        super(partition);
        this.handle         = handle;
        this.context        = context;
        this.objectTypeConf = objectTypeConf;
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(handle.detachLinkedTuplesForPartition(partition));
        ObjectTypeNode[]     cachedNodes          = objectTypeConf.getObjectTypeNodes();
        for (int i = 0, length = cachedNodes.length; i < length; i++) {
            ObjectTypeNode otn = cachedNodes[i];
            ((CompositePartitionAwareObjectSinkAdapter) otn.getObjectSinkPropagator())
                    .propagateModifyObjectForPartition(handle, modifyPreviousTuples,
                                                       context.adaptModificationMaskForObjectType(otn.getObjectType(), reteEvaluator),
                                                       reteEvaluator, partition);
            if (i < cachedNodes.length - 1) {
                removeRightTuplesMatchingOTN(context, reteEvaluator, modifyPreviousTuples, otn, partition);
            }
        }
        modifyPreviousTuples.retractTuples(context, reteEvaluator);
    }

    @Override
    public String toString() {
        return "Update of " + handle.getObject() + " for partition " + partition;
    }
}

package org.drools.core.phreak.actions;

import org.drools.base.phreak.actions.AbstractPropagationEntry;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.phreak.PropagationEntry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;

public class Delete extends AbstractPropagationEntry<ReteEvaluator> {
    private final EntryPointNode     epn;
    private final InternalFactHandle handle;
    private final PropagationContext context;
    private final ObjectTypeConf     objectTypeConf;

    public Delete(EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
        this.epn            = epn;
        this.handle         = handle;
        this.context        = context;
        this.objectTypeConf = objectTypeConf;
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        execute(reteEvaluator, epn, handle, context, objectTypeConf);
    }

    public static void execute(ReteEvaluator reteEvaluator, EntryPointNode epn, InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
        epn.propagateRetract(handle, context, objectTypeConf, reteEvaluator);
    }

    @Override
    public boolean isPartitionSplittable() {
        return true;
    }

    @Override
    public PropagationEntry getSplitForPartition(int partitionNr) {
        return new PartitionedDelete(handle, context, objectTypeConf, partitionNr);
    }

    @Override
    public String toString() {
        return "Delete of " + handle.getObject();
    }
}

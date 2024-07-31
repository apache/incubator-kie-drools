package org.drools.core.phreak.actions;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.drools.core.reteoo.EntryPointNode.removeRightTuplesMatchingOTN;

public class Update extends AbstractPropagationEntry<ReteEvaluator> implements Externalizable {
    private InternalFactHandle handle;
    private PropagationContext context;
    private ObjectTypeConf     objectTypeConf;

    public Update() {}

    public Update(InternalFactHandle handle, PropagationContext context, ObjectTypeConf objectTypeConf) {
        this.handle         = handle;
        this.context        = context;
        this.objectTypeConf = objectTypeConf;
    }

    public void internalExecute(ReteEvaluator reteEvaluator) {
        execute(handle, context, objectTypeConf, reteEvaluator);
    }

    public static void execute(InternalFactHandle handle, PropagationContext pctx, ObjectTypeConf objectTypeConf, ReteEvaluator reteEvaluator) {
        if (objectTypeConf == null) {
            // it can be null after deserialization
            objectTypeConf = handle.getEntryPoint(reteEvaluator).getObjectTypeConfigurationRegistry().getOrCreateObjectTypeConf(handle.getEntryPointId(), handle.getObject());
        }
        // make a reference to the previous tuples, then null then on the handle
        ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(handle.detachLinkedTuples());
        ObjectTypeNode[]     cachedNodes          = objectTypeConf.getObjectTypeNodes();
        for (int i = 0, length = cachedNodes.length; i < length; i++) {
            cachedNodes[i].modifyObject(handle, modifyPreviousTuples, pctx, reteEvaluator);
            if (i < cachedNodes.length - 1) {
                removeRightTuplesMatchingOTN(pctx, reteEvaluator, modifyPreviousTuples, cachedNodes[i], 0);
            }
        }
        modifyPreviousTuples.retractTuples(pctx, reteEvaluator);
    }

    @Override
    public boolean isPartitionSplittable() {
        return true;
    }

    @Override
    public PropagationEntry getSplitForPartition(int partitionNr) {
        return new PartitionedUpdate(handle, context, objectTypeConf, partitionNr);
    }

    @Override
    public String toString() {
        return "Update of " + handle.getObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(next);
        out.writeObject(handle);
        out.writeObject(context);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.next    = (PropagationEntry) in.readObject();
        this.handle  = (InternalFactHandle) in.readObject();
        this.context = (PropagationContext) in.readObject();
    }
}

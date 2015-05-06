package org.drools.reteoo.nodes;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;

public class ReteEntryPointNode extends EntryPointNode {
    public ReteEntryPointNode() {
    }

    public ReteEntryPointNode(int id, ObjectSource objectSource, BuildContext context) {
        super(id, objectSource, context);
    }

    public ReteEntryPointNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, ObjectSource objectSource, EntryPointId entryPoint) {
        super(id, partitionId, partitionsEnabled, objectSource, entryPoint);
    }

    public void doRightDelete(PropagationContext pctx, InternalWorkingMemory wm, RightTuple rightTuple) {
        ((BetaNode) rightTuple.getRightTupleSink()).retractRightTuple(rightTuple,
                                                                      pctx,
                                                                      wm);
    }

    public void doDeleteObject(PropagationContext pctx, InternalWorkingMemory wm, LeftTuple leftTuple) {
        leftTuple.getLeftTupleSink().retractLeftTuple(leftTuple,
                                                      pctx,
                                                      wm);
    }

    public void assertQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.propagateAssert( factHandle, context, workingMemory );
        }
    }

    public void retractQuery(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            // There may be no queries defined
            this.queryNode.retractObject(factHandle, context, workingMemory);
        }
    }

    public void modifyQuery(final InternalFactHandle factHandle,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        if ( queryNode == null ) {
            this.queryNode = objectTypeNodes.get( ClassObjectType.DroolsQuery_ObjectType );
        }

        if ( queryNode != null ) {
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(factHandle.getFirstLeftTuple(), factHandle.getFirstRightTuple(), this );
            factHandle.clearLeftTuples();
            factHandle.clearRightTuples();

            // There may be no queries defined
            this.queryNode.modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
            modifyPreviousTuples.retractTuples(context, workingMemory);
        }
    }
}

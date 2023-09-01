package org.drools.core.reteoo;

import org.drools.core.common.ActivationsManager;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.phreak.PhreakRuleTerminalNode;
import org.drools.core.common.PropagationContext;

public class ModifyPreviousTuples {
    private final DefaultFactHandle.LinkedTuples linkedTuples;

    public ModifyPreviousTuples(InternalFactHandle.LinkedTuples linkedTuples) {
        this.linkedTuples = linkedTuples;
    }
    
    public LeftTuple peekLeftTuple(int partition) {
        return linkedTuples.getFirstLeftTuple(partition);
    }

    public LeftTuple peekLeftTuple(RuleBasePartitionId partitionId) {
        return linkedTuples.getFirstLeftTuple(partitionId);
    }

    public RightTuple peekRightTuple(int partition) {
        return linkedTuples.getFirstRightTuple(partition);
    }

    public RightTuple peekRightTuple(RuleBasePartitionId partitionId) {
        return linkedTuples.getFirstRightTuple(partitionId);
    }

    public void removeLeftTuple(int partition) {
        linkedTuples.removeLeftTuple( peekLeftTuple(partition) );
    }

    public void removeLeftTuple(RuleBasePartitionId partitionId) {
        linkedTuples.removeLeftTuple( peekLeftTuple(partitionId) );
    }

    public void removeRightTuple(int partition) {
        linkedTuples.removeRightTuple( peekRightTuple(partition) );
    }

    public void removeRightTuple(RuleBasePartitionId partitionId) {
        linkedTuples.removeRightTuple( peekRightTuple(partitionId) );
    }

    public void retractTuples(PropagationContext pctx,
                              ReteEvaluator reteEvaluator) {
        linkedTuples.forEachLeftTuple( lt -> doDeleteObject(pctx, reteEvaluator, lt) );
        linkedTuples.forEachRightTuple( rt -> doRightDelete(pctx, reteEvaluator, rt) );
    }

    public void doDeleteObject(PropagationContext pctx, ReteEvaluator reteEvaluator, LeftTuple leftTuple) {
        LeftInputAdapterNode liaNode = (LeftInputAdapterNode) leftTuple.getTupleSource();
        LeftInputAdapterNode.LiaNodeMemory lm = reteEvaluator.getNodeMemory( liaNode );
        SegmentMemory sm = lm.getSegmentMemory();
        if (sm != null) {
            LeftInputAdapterNode.doDeleteObject( leftTuple, pctx, sm, reteEvaluator, liaNode, true, lm );
        } else {
            ActivationsManager activationsManager = reteEvaluator.getActivationsManager();
            TerminalNode rtn = (TerminalNode) leftTuple.getTupleSink();
            PathMemory pathMemory = reteEvaluator.getNodeMemory( rtn );
            PhreakRuleTerminalNode.doLeftDelete(activationsManager, pathMemory.getRuleAgendaItem().getRuleExecutor(), leftTuple);
        }
    }

    public void doRightDelete(PropagationContext pctx, ReteEvaluator reteEvaluator, RightTuple rightTuple) {
        rightTuple.setPropagationContext( pctx );
        rightTuple.retractTuple( pctx, reteEvaluator );
    }
}

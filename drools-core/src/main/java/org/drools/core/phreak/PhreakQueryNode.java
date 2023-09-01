package org.drools.core.phreak;

import org.drools.core.base.DroolsQueryImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.common.PropagationContext;

public class PhreakQueryNode {
    public void doNode(QueryElementNode queryNode,
                       QueryElementNodeMemory qmem,
                       StackEntry stackEntry,
                       ReteEvaluator reteEvaluator,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(qmem, reteEvaluator, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(queryNode, qmem, reteEvaluator, srcLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(queryNode, qmem, stackEntry, reteEvaluator, srcLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              StackEntry stackEntry,
                              ReteEvaluator reteEvaluator,
                              TupleSets<LeftTuple> srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext pCtx = leftTuple.getPropagationContext();

            InternalFactHandle handle = queryNode.createFactHandle(pCtx,
                                                                   reteEvaluator,
                                                                   leftTuple);

            DroolsQueryImpl dquery = queryNode.createDroolsQuery(leftTuple, handle, stackEntry,
                                                                 qmem.getSegmentMemory().getPathMemories(),
                                                                 qmem,
                                                                 stackEntry.getSink(), reteEvaluator);

            LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
            LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
            LeftInputAdapterNode.doInsertObject(handle, pCtx, lian, reteEvaluator, lm, false, dquery.isOpen());

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(QueryElementNode queryNode,
                              QueryElementNodeMemory qmem,
                              ReteEvaluator reteEvaluator,
                              TupleSets<LeftTuple> srcLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQueryImpl dquery = (DroolsQueryImpl) fh.getObject();
            dquery.setParameters( queryNode.getActualArguments( leftTuple, reteEvaluator ) );

            SegmentMemory qsmem = qmem.getQuerySegmentMemory();
            LeftInputAdapterNode lian = (LeftInputAdapterNode) qsmem.getRootNode();
            LiaNodeMemory lmem = (LiaNodeMemory) qsmem.getNodeMemories()[0];
            if (dquery.isOpen()) {
                LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doUpdateObject(childLeftTuple, childLeftTuple.getPropagationContext(), reteEvaluator, lian, false, lmem, qmem.getQuerySegmentMemory());
            } else {
                if (fh.getFirstLeftTuple() != null) {
                    throw new RuntimeException("defensive programming while testing"); // @TODO remove later (mdp)
                }
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
                LeftInputAdapterNode.doInsertObject(fh, leftTuple.getPropagationContext(), lian, reteEvaluator, lm, false, dquery.isOpen());
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(QueryElementNodeMemory qmem,
                              ReteEvaluator reteEvaluator,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            InternalFactHandle fh = (InternalFactHandle) leftTuple.getContextObject();
            DroolsQueryImpl dquery = (DroolsQueryImpl) fh.getObject();
            if (dquery.isOpen()) {
                LeftInputAdapterNode lian = (LeftInputAdapterNode) qmem.getQuerySegmentMemory().getRootNode();
                LiaNodeMemory lm = (LiaNodeMemory) qmem.getQuerySegmentMemory().getNodeMemories()[0];
                LeftTuple childLeftTuple = fh.getFirstLeftTuple(); // there is only one, all other LTs are peers
                LeftInputAdapterNode.doDeleteObject(childLeftTuple, childLeftTuple.getPropagationContext(), qmem.getQuerySegmentMemory(), reteEvaluator, lian, false, lm);
            } else {
                LeftTuple childLeftTuple = leftTuple.getFirstChild();
                while (childLeftTuple != null) {
                    LeftTuple nextChild = childLeftTuple.getHandleNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}

package org.drools.core.phreak;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSinkNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.index.LeftTupleList;

import static org.drools.core.phreak.PhreakFromNode.checkConstraintsAndPropagate;

public abstract class ReactiveObject {
    private final LeftTupleList lts = new LeftTupleList();

    public void addLeftTuple(LeftTuple leftTuple) {
        lts.add(leftTuple);
    }

    protected void notifyModification() {
        notifyModification(this);
    }

    protected void notifyModification(Object object) {
        for (LeftTuple leftTuple = lts.getFirst(); leftTuple != null; leftTuple = (LeftTuple)leftTuple.getNext()) {
            PropagationContext propagationContext = leftTuple.getPropagationContext();
            ReactiveFromNode node = (ReactiveFromNode)leftTuple.getSink();

            LeftTupleSinkNode sink = node.getSinkPropagator().getFirstLeftTupleSink();
            InternalWorkingMemory wm = getInternalWorkingMemory(propagationContext);
            ReactiveFromNode.ReactiveFromMemory mem = (ReactiveFromNode.ReactiveFromMemory)wm.getNodeMemory(node);

            RightTuple rightTuple = node.createRightTuple(leftTuple, propagationContext, wm, object);

            checkConstraintsAndPropagate(sink,
                                         leftTuple,
                                         rightTuple,
                                         node.getAlphaConstraints(),
                                         node.getBetaConstraints(),
                                         propagationContext,
                                         wm,
                                         mem,
                                         mem.getBetaMemory().getContext(),
                                         RuleNetworkEvaluator.useLeftMemory(node, leftTuple),
                                         mem.getStagedLeftTuples(),
                                         null);

            mem.getBetaMemory().setNodeDirty(wm);
        }
    }

    private InternalWorkingMemory getInternalWorkingMemory(PropagationContext propagationContext) {
        InternalFactHandle fh = (InternalFactHandle) propagationContext.getFactHandleOrigin();
        return ((InternalWorkingMemoryEntryPoint) fh.getEntryPoint()).getInternalWorkingMemory();
    }
}

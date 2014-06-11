package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.EvalConditionNode.EvalMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.rule.EvalCondition;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:44
* To change this template use File | Settings | File Templates.
*/
public class PhreakEvalNode {

    private static final String EVAL_LEFT_TUPLE_DELETED = "EVAL_LEFT_TUPLE_DELETED";

    public void doNode(EvalConditionNode evalNode,
                       EvalMemory em,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(evalNode, em, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(evalNode, em, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(evalNode, em, sink, wm, srcLeftTuples, trgLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(EvalConditionNode evalNode,
                              EvalMemory em,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples) {
        EvalCondition condition = evalNode.getCondition();
        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            final boolean allowed = condition.isAllowed(leftTuple,
                                                        wm,
                                                        em.context);

            if (allowed) {
                boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(evalNode, leftTuple);

                trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                             sink,
                                                             leftTuple.getPropagationContext(), useLeftMemory));
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(EvalConditionNode evalNode,
                              EvalMemory em,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        EvalCondition condition = evalNode.getCondition();
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            boolean wasPropagated = leftTuple.getFirstChild() != null && leftTuple.getObject() != EVAL_LEFT_TUPLE_DELETED;

            boolean allowed = condition.isAllowed(leftTuple,
                                                  wm,
                                                  em.context);
            if (allowed) {
                leftTuple.setObject(null);

                if (wasPropagated) {
                    // update
                    LeftTuple childLeftTuple = leftTuple.getFirstChild();
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    switch (childLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(childLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(childLeftTuple);
                            break;
                    }

                    trgLeftTuples.addUpdate(childLeftTuple);
                } else {
                    // assert
                    trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                                 sink,
                                                                 leftTuple.getPropagationContext(), true));
                }
            } else {
                if (wasPropagated) {
                    // retract
                    leftTuple.setObject(EVAL_LEFT_TUPLE_DELETED);

                    LeftTuple childLeftTuple = leftTuple.getFirstChild();
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    switch (childLeftTuple.getStagedType()) {
                        // handle clash with already staged entries
                        case LeftTuple.INSERT:
                            stagedLeftTuples.removeInsert(childLeftTuple);
                            break;
                        case LeftTuple.UPDATE:
                            stagedLeftTuples.removeUpdate(childLeftTuple);
                            break;
                    }

                    trgLeftTuples.addDelete(childLeftTuple);
                }
                // else do nothing
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(EvalConditionNode evalNode,
                              EvalMemory em,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();


            LeftTuple childLeftTuple = leftTuple.getFirstChild();
            if (childLeftTuple != null) {
                switch (childLeftTuple.getStagedType()) {
                    // handle clash with already staged entries
                    case LeftTuple.INSERT:
                        stagedLeftTuples.removeInsert(childLeftTuple);
                        break;
                    case LeftTuple.UPDATE:
                        stagedLeftTuples.removeUpdate(childLeftTuple);
                        break;
                }
                childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                trgLeftTuples.addDelete(childLeftTuple);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}

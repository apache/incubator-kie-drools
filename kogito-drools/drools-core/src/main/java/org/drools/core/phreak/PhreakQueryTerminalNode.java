package org.drools.core.phreak;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:42
* To change this template use File | Settings | File Templates.
*/
public class PhreakQueryTerminalNode {
    public void doNode(QueryTerminalNode qtnNode,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LinkedList<StackEntry> stack) {
        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(qtnNode, wm, srcLeftTuples, stack);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(qtnNode, wm, srcLeftTuples, stack);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(qtnNode, wm, srcLeftTuples, stack);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(QueryTerminalNode qtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LinkedList<StackEntry> stack) {

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            //qtnNode.assertLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );

            PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                        leftTuple.getPropagationContext());

            // find the DroolsQuery object
            LeftTuple rootEntry = leftTuple.getRootLeftTuple();

            DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
            dquery.setQuery(qtnNode.getQuery());
            if (dquery.getStackEntry() != null) {
                checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
            }

            // Add results to the adapter
            dquery.getQueryResultCollector().rowAdded(qtnNode.getQuery(),
                                                      leftTuple,
                                                      pCtx,
                                                      wm);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(QueryTerminalNode qtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LinkedList<StackEntry> stack) {

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                        leftTuple.getPropagationContext());

            // qtnNode.modifyLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );
            LeftTuple rootEntry = leftTuple;

            // find the DroolsQuery object
            while (rootEntry.getParent() != null) {
                rootEntry = rootEntry.getParent();
            }
            DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
            dquery.setQuery(qtnNode.getQuery());
            if (dquery.getStackEntry() != null) {
                checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
            }

            // Add results to the adapter
            dquery.getQueryResultCollector().rowUpdated(qtnNode.getQuery(),
                                                        leftTuple,
                                                        pCtx,
                                                        wm);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(QueryTerminalNode qtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LinkedList<StackEntry> stack) {

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            //qtnNode.retractLeftTuple( leftTuple, leftTuple.getPropagationContext(), wm );

            PropagationContext pCtx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                                        leftTuple.getPropagationContext());

            LeftTuple rootEntry = leftTuple;

            // find the DroolsQuery object
            while (rootEntry.getParent() != null) {
                rootEntry = rootEntry.getParent();
            }
            DroolsQuery dquery = (DroolsQuery) rootEntry.getLastHandle().getObject();
            dquery.setQuery(qtnNode.getQuery());

            if (dquery.getStackEntry() != null) {
                checkAndTriggerQueryReevaluation(wm, stack, rootEntry, dquery);
            }

            // Add results to the adapter
            dquery.getQueryResultCollector().rowRemoved(qtnNode.getQuery(),
                                                        leftTuple,
                                                        pCtx,
                                                        wm);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }


    public static void checkAndTriggerQueryReevaluation(InternalWorkingMemory wm, LinkedList<StackEntry> stack, LeftTuple rootEntry, DroolsQuery dquery) {
        StackEntry stackEntry = dquery.getStackEntry();
        if (!isAdded(stack, stackEntry)) {
            // Ignore unless stackEntry is not added to stack

            if (stackEntry.getLiaNode()== rootEntry.getLeftTupleSink().getLeftTupleSource()) {
                // query is recursive, so just re-add the stack entry to the current stack. This happens for reactive queries, triggered by a beta node right input
                stack.add(stackEntry);
            } else {
                // parents is anther rule/query need to notify for agenda to schedule. query is reactive, triggered by right input,
                List<PathMemory> rmems = dquery.getRuleMemories();
                if (rmems != null) {
                    // StackEntry is null, when query is called directly from java

                    // reactivity comes form within the query, so need to notify parent rules to evaluate the results
                    for (int i = 0, length = rmems.size(); i < length; i++) {
                        PathMemory rmem = rmems.get(i);
                        rmem.doLinkRule(wm); // method already ignores is rule is activated and on agenda
                    }
                }
            }
        }
    }

    public static boolean isAdded(LinkedList<StackEntry> stack, StackEntry stackEntry) {
        if (stackEntry == null || stackEntry.getPrevious() != null || stackEntry.getNext() != null || stack.getFirst() == stackEntry) {
            return true;
        }

        return false;
    }
}

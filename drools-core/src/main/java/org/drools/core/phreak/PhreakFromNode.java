package org.drools.core.phreak;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.drools.core.phreak.PhreakJoinNode.updateChildLeftTuple;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:43
* To change this template use File | Settings | File Templates.
*/
public class PhreakFromNode {
    public void doNode(FromNode fromNode,
                       FromMemory fm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(fm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(FromNode fromNode,
                              FromMemory fm,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples) {

        BetaMemory bm = fm.getBetaMemory();
        ContextEntry[] context = bm.getContext();
        BetaConstraints betaConstraints = fromNode.getBetaConstraints();
        AlphaNodeFieldConstraint[] alphaConstraints = fromNode.getAlphaConstraints();
        DataProvider dataProvider = fromNode.getDataProvider();
        Class<?> resultClass = fromNode.getResultClass();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext propagationContext = leftTuple.getPropagationContext();

            Map<Object, RightTuple> matches = null;
            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(fromNode, leftTuple);

            if (useLeftMemory) {
                fm.betaMemory.getLeftTupleMemory().add(leftTuple);
                matches = new LinkedHashMap<Object, RightTuple>();
                leftTuple.setObject(matches);
            }

            betaConstraints.updateFromTuple(context,
                                            wm,
                                            leftTuple);

            for (final java.util.Iterator<?> it = dataProvider.getResults(leftTuple,
                                                                          wm,
                                                                          propagationContext,
                                                                          fm.providerContext); it.hasNext(); ) {
                final Object object = it.next();
                if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                    continue; // skip anything if it not assignable
                }

                RightTuple rightTuple = fromNode.createRightTuple(leftTuple,
                                                                  propagationContext,
                                                                  wm,
                                                                  object);

                checkConstraintsAndPropagate(sink,
                                             leftTuple,
                                             rightTuple,
                                             alphaConstraints,
                                             betaConstraints,
                                             propagationContext,
                                             wm,
                                             fm,
                                             context,
                                             useLeftMemory,
                                             trgLeftTuples,
                                             null);
                if (useLeftMemory) {
                    fromNode.addToCreatedHandlesMap(matches,
                                                    rightTuple);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
        betaConstraints.resetTuple(context);
    }

    public void doLeftUpdates(FromNode fromNode,
                              FromMemory fm,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        BetaMemory bm = fm.getBetaMemory();
        ContextEntry[] context = bm.getContext();
        BetaConstraints betaConstraints = fromNode.getBetaConstraints();
        AlphaNodeFieldConstraint[] alphaConstraints = fromNode.getAlphaConstraints();
        DataProvider dataProvider = fromNode.getDataProvider();
        Class<?> resultClass = fromNode.getResultClass();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext propagationContext = leftTuple.getPropagationContext();

            final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) leftTuple.getObject();
            final Map<Object, RightTuple> newMatches = new HashMap<Object, RightTuple>();
            leftTuple.setObject(newMatches);

            betaConstraints.updateFromTuple(context,
                                            wm,
                                            leftTuple);

            FastIterator rightIt = LinkedList.fastIterator;
            for (final java.util.Iterator<?> it = dataProvider.getResults(leftTuple,
                                                                          wm,
                                                                          propagationContext,
                                                                          fm.providerContext); it.hasNext(); ) {
                final Object object = it.next();
                if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                    continue; // skip anything if it not assignable
                }

                RightTuple rightTuple = previousMatches.remove(object);

                if (rightTuple == null) {
                    // new match, propagate assert
                    rightTuple = fromNode.createRightTuple(leftTuple,
                                                           propagationContext,
                                                           wm,
                                                           object);
                } else {
                    // previous match, so reevaluate and propagate modify
                    if (rightIt.next(rightTuple) != null) {
                        // handle the odd case where more than one object has the same hashcode/equals value
                        previousMatches.put(object,
                                            (RightTuple) rightIt.next(rightTuple));
                        rightTuple.setNext(null);
                    }
                }

                checkConstraintsAndPropagate(sink,
                                             leftTuple,
                                             rightTuple,
                                             alphaConstraints,
                                             betaConstraints,
                                             propagationContext,
                                             wm,
                                             fm,
                                             context,
                                             true,
                                             trgLeftTuples,
                                             null);

                fromNode.addToCreatedHandlesMap(newMatches,
                                                rightTuple);
            }

            for (RightTuple rightTuple : previousMatches.values()) {
                for (RightTuple current = rightTuple; current != null; current = (RightTuple) rightIt.next(current)) {
                    deleteChildLeftTuple(propagationContext, trgLeftTuples, stagedLeftTuples, current.getFirstChild());
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
        betaConstraints.resetTuple(context);
    }

    public void doLeftDeletes(FromMemory fm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        BetaMemory bm = fm.getBetaMemory();
        LeftTupleMemory ltm = bm.getLeftTupleMemory();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            ltm.remove(leftTuple);

            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();

            if (leftTuple.getFirstChild() != null) {
                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                while (childLeftTuple != null) {
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    childLeftTuple = RuleNetworkEvaluator.deleteLeftChild(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                }
            }


            // if matches == null, the deletion might be happening before the fact was even propagated. See BZ-1019473 for details.
            if( matches != null ) {
                
                // @TODO (mdp) is this really necessary? won't the entire FH and RightTuple chaines just et GC'd?
                unlinkCreatedHandles(leftTuple);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public static void unlinkCreatedHandles(final LeftTuple leftTuple) {
        Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
        FastIterator rightIt = LinkedList.fastIterator;
        for (RightTuple rightTuple : matches.values()) {
            for (RightTuple current = rightTuple; current != null; ) {
                RightTuple next = (RightTuple) rightIt.next(current);
                current.unlinkFromRightParent();
                current = next;
            }
        }
    }

    protected void checkConstraintsAndPropagate(final LeftTupleSink sink,
                                                final LeftTuple leftTuple,
                                                final RightTuple rightTuple,
                                                final AlphaNodeFieldConstraint[] alphaConstraints,
                                                final BetaConstraints betaConstraints,
                                                final PropagationContext propagationContext,
                                                final InternalWorkingMemory wm,
                                                final FromMemory fm,
                                                final ContextEntry[] context,
                                                final boolean useLeftMemory,
                                                LeftTupleSets trgLeftTuples,
                                                LeftTupleSets stagedLeftTuples) {
        boolean isAllowed = true;
        if (alphaConstraints != null) {
            // First alpha node filters
            for (int i = 0, length = alphaConstraints.length; i < length; i++) {
                if (!alphaConstraints[i].isAllowed(rightTuple.getFactHandle(),
                                                   wm,
                                                   fm.alphaContexts[i])) {
                    // next iteration
                    isAllowed = false;
                    break;
                }
            }
        }

        if (isAllowed && betaConstraints.isAllowedCachedLeft(context,
                                                             rightTuple.getFactHandle())) {

            if (rightTuple.firstChild == null) {
                // this is a new match, so propagate as assert
                LeftTuple childLeftTuple = sink.createLeftTuple(leftTuple,
                                                                rightTuple,
                                                                null,
                                                                null,
                                                                sink,
                                                                useLeftMemory);
                childLeftTuple.setPropagationContext(propagationContext);
                trgLeftTuples.addInsert(childLeftTuple);
            } else {
                LeftTuple childLeftTuple = rightTuple.firstChild;
                childLeftTuple.setPropagationContext(propagationContext);
                updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);
            }
        } else {
            deleteChildLeftTuple(propagationContext, trgLeftTuples, stagedLeftTuples, rightTuple.firstChild);
        }
    }

    private void deleteChildLeftTuple(PropagationContext propagationContext, LeftTupleSets trgLeftTuples, LeftTupleSets stagedLeftTuples, LeftTuple childLeftTuple) {
        if (childLeftTuple != null) {
            childLeftTuple.unlinkFromLeftParent();
            childLeftTuple.unlinkFromRightParent();

            switch (childLeftTuple.getStagedType()) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    stagedLeftTuples.removeInsert(childLeftTuple);
                    break;
                case LeftTuple.UPDATE:
                    stagedLeftTuples.removeUpdate(childLeftTuple);
                    break;
            }
            childLeftTuple.setPropagationContext(propagationContext);
            trgLeftTuples.addDelete(childLeftTuple);
        }
    }
}

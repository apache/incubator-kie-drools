/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
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
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

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
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples) {

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
                fm.getBetaMemory().getLeftTupleMemory().add(leftTuple);
                matches = new LinkedHashMap<Object, RightTuple>();
                leftTuple.setContextObject( matches );
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
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        BetaMemory bm = fm.getBetaMemory();
        ContextEntry[] context = bm.getContext();
        BetaConstraints betaConstraints = fromNode.getBetaConstraints();
        AlphaNodeFieldConstraint[] alphaConstraints = fromNode.getAlphaConstraints();
        DataProvider dataProvider = fromNode.getDataProvider();
        Class<?> resultClass = fromNode.getResultClass();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext propagationContext = leftTuple.getPropagationContext();

            final Map<Object, RightTuple> previousMatches = (Map<Object, RightTuple>) leftTuple.getContextObject();
            final Map<Object, RightTuple> newMatches = new HashMap<Object, RightTuple>();
            leftTuple.setContextObject( newMatches );

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
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        BetaMemory bm = fm.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            ltm.remove(leftTuple);

            Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getContextObject();

            if (leftTuple.getFirstChild() != null) {
                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                while (childLeftTuple != null) {
                    childLeftTuple.setPropagationContext( leftTuple.getPropagationContext());
                    LeftTuple nextChild = childLeftTuple.getHandleNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
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
        Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getContextObject();
        FastIterator rightIt = LinkedList.fastIterator;
        for (RightTuple rightTuple : matches.values()) {
            for (RightTuple current = rightTuple; current != null; ) {
                RightTuple next = (RightTuple) rightIt.next(current);
                current.unlinkFromRightParent();
                current = next;
            }
        }
    }

    public static void checkConstraintsAndPropagate(final LeftTupleSink sink,
                                                    final LeftTuple leftTuple,
                                                    final RightTuple rightTuple,
                                                    final AlphaNodeFieldConstraint[] alphaConstraints,
                                                    final BetaConstraints betaConstraints,
                                                    final PropagationContext propagationContext,
                                                    final InternalWorkingMemory wm,
                                                    final FromMemory fm,
                                                    final ContextEntry[] context,
                                                    final boolean useLeftMemory,
                                                    TupleSets<LeftTuple> trgLeftTuples,
                                                    TupleSets<LeftTuple> stagedLeftTuples) {
        if ( isAllowed( rightTuple.getFactHandle(), alphaConstraints, wm, fm ) ) {
            propagate( sink, leftTuple, rightTuple, betaConstraints, propagationContext, context, useLeftMemory, trgLeftTuples, stagedLeftTuples );
        }
    }

    public static boolean isAllowed( InternalFactHandle factHandle,
                                     AlphaNodeFieldConstraint[] alphaConstraints,
                                     InternalWorkingMemory wm,
                                     FromMemory fm ) {
        if (alphaConstraints != null) {
            for (int i = 0, length = alphaConstraints.length; i < length; i++) {
                if (!alphaConstraints[i].isAllowed(factHandle, wm)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void propagate( LeftTupleSink sink,
                                  Tuple leftTuple,
                                  RightTuple rightTuple,
                                  BetaConstraints betaConstraints,
                                  PropagationContext propagationContext,
                                  ContextEntry[] context,
                                  boolean useLeftMemory,
                                  TupleSets<LeftTuple> trgLeftTuples,
                                  TupleSets<LeftTuple> stagedLeftTuples ) {
        if (betaConstraints.isAllowedCachedLeft(context, rightTuple.getFactHandle())) {

            if (rightTuple.getFirstChild() == null) {
                // this is a new match, so propagate as assert
                LeftTuple childLeftTuple = sink.createLeftTuple((LeftTuple)leftTuple,
                                                                rightTuple,
                                                                null,
                                                                null,
                                                                sink,
                                                                useLeftMemory);
                childLeftTuple.setPropagationContext(propagationContext);
                trgLeftTuples.addInsert(childLeftTuple);
            } else {
                LeftTuple childLeftTuple = rightTuple.getFirstChild();
                childLeftTuple.setPropagationContext(propagationContext);
                updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);
            }
        } else {
            deleteChildLeftTuple(propagationContext, trgLeftTuples, stagedLeftTuples, rightTuple.getFirstChild());
        }
    }

    public static void deleteChildLeftTuple(PropagationContext propagationContext,
                                            TupleSets<LeftTuple> trgLeftTuples,
                                            TupleSets<LeftTuple> stagedLeftTuples,
                                            LeftTuple childLeftTuple) {
        if (childLeftTuple != null) {
            childLeftTuple.setPropagationContext( propagationContext );
            RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple(childLeftTuple, trgLeftTuples, stagedLeftTuples);
        }
    }
}

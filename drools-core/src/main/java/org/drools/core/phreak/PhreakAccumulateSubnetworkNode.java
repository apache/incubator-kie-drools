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
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.BaseAccumulation;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.FastIterator;

public class PhreakAccumulateSubnetworkNode extends PhreakAccumulateNode {

    @Override
    public void doNode(AccumulateNode accNode,
                       LeftTupleSink sink,
                       AccumulateMemory am,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        BetaMemory bm = am.getBetaMemory();
        TupleSets<RightTuple> srcRightTuples = bm.getStagedRightTuples().takeAll();

        // order of left and right operations is to minimise wasted of innefficient joins.

        // We need to collect which leftTuple where updated, so that we can
        // add their result tuple to the real target tuples later
        TupleSets<LeftTuple> tempLeftTuples = new TupleSetsImpl<>();

        if (srcRightTuples.getDeleteFirst() != null) {
            doRightDeletes(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            RuleNetworkEvaluator.doUpdatesReorderRightMemory(bm, srcRightTuples);
            doRightUpdates(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        if (srcRightTuples.getInsertFirst() != null) {
            doRightInserts(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        Accumulate accumulate = accNode.getAccumulate();
        // we do not need collect retracts. RightTuple retracts end up as updates for lefttuples.
        // LeftTuple retracts are already on the trgLeftTuples
        for (LeftTuple leftTuple = tempLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            evaluateResultConstraints(accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                      wm, am, (BaseAccumulation) leftTuple.getContextObject(),
                                      trgLeftTuples, stagedLeftTuples);
            leftTuple.clearStaged();
            leftTuple = next;
        }

        for (LeftTuple leftTuple = tempLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            evaluateResultConstraints( accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                       wm, am, (BaseAccumulation) leftTuple.getContextObject(),
                                       trgLeftTuples, stagedLeftTuples );
            leftTuple.clearStaged();
            leftTuple = next;
        }

        srcRightTuples.resetAll();

        srcLeftTuples.resetAll();
    }

    private void doRightInserts(AccumulateNode accNode,
                                AccumulateMemory am,
                                InternalWorkingMemory wm,
                                TupleSets<RightTuple> srcRightTuples,
                                TupleSets<LeftTuple> trgLeftTuples) {
        Accumulate accumulate = accNode.getAccumulate();

        BetaMemory bm = am.getBetaMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = accNode.getRawConstraints();

        if (srcRightTuples.getInsertSize() > 32 && rtm instanceof AbstractHashTable ) {
            ((AbstractHashTable) rtm).ensureCapacity(srcRightTuples.getInsertSize());
        }

        for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            rtm.add( rightTuple );

            LeftTuple leftTuple = (( SubnetworkTuple ) rightTuple).getStartTuple();

            constraints.updateFromFactHandle( contextEntry, wm, rightTuple.getFactHandleForEvaluation() );

            if ( constraints.isAllowedCachedRight( contextEntry, leftTuple ) ) {
                BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                if (accctx == null) {
                    accctx = initAccumulationContext( am, wm, accumulate, leftTuple );
                }
                addMatch( accNode, accumulate, leftTuple, rightTuple,
                          null, null, wm, am,
                          accctx, true, false );

                // right inserts and updates are done first
                // so any existing leftTuples we know are updates, but only add if not already added
                if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                    trgLeftTuples.addUpdate( leftTuple );
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle( contextEntry );
    }

    private void doRightUpdates(AccumulateNode accNode,
                                AccumulateMemory am,
                                InternalWorkingMemory wm,
                                TupleSets<RightTuple> srcRightTuples,
                                TupleSets<LeftTuple> trgLeftTuples) {
        BetaMemory bm = am.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = accNode.getRawConstraints();
        Accumulate accumulate = accNode.getAccumulate();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();

            if ( ltm != null && ltm.size() > 0 ) {
                LeftTuple childLeftTuple = rightTuple.getFirstChild();

                FastIterator leftIt = accNode.getLeftIterator( ltm );
                LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, leftIt );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandleForEvaluation() );

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null
                if ( childLeftTuple != null && ltm.isIndexed() && !leftIt.isFullIterator() && ( leftTuple == null || ( leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory() ) ) ) {
                    // our index has changed, so delete all the previous matches
                    removePreviousMatchesForRightTuple( accNode,
                                                        accumulate,
                                                        rightTuple,
                                                        wm,
                                                        am,
                                                        childLeftTuple,
                                                        trgLeftTuples );
                    childLeftTuple = null; // null so the next check will attempt matches for new bucket
                }

                // if LeftTupleMemory is empty, there are no matches to modify
                if ( leftTuple != null ) {
                    doRightUpdatesProcessChildren( accNode,
                                                   am,
                                                   wm,
                                                   bm,
                                                   constraints,
                                                   accumulate,
                                                   leftIt,
                                                   rightTuple,
                                                   childLeftTuple,
                                                   leftTuple,
                                                   trgLeftTuples );
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle(contextEntry);
    }

    private void doRightUpdatesProcessChildren(AccumulateNode accNode,
                                               AccumulateMemory am,
                                               InternalWorkingMemory wm,
                                               BetaMemory bm,
                                               BetaConstraints constraints,
                                               Accumulate accumulate,
                                               FastIterator leftIt,
                                               RightTuple rightTuple,
                                               LeftTuple childLeftTuple,
                                               LeftTuple leftTuple,
                                               TupleSets<LeftTuple> trgLeftTuples) {
        if (childLeftTuple == null) {
            // either we are indexed and changed buckets or
            // we had no children before, but there is a bucket to potentially match, so try as normal assert
            for (; leftTuple != null; leftTuple = (LeftTuple) leftIt.next(leftTuple)) {
                if (constraints.isAllowedCachedRight(bm.getContext(),
                                                     leftTuple)) {
                    if (leftTuple.getStagedType() == LeftTuple.NONE) {
                        trgLeftTuples.addUpdate(leftTuple);
                    }
                    final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                    // add a new match
                    addMatch(accNode, accumulate, leftTuple, rightTuple,
                             null, null, wm, am,
                             accctx, true, false);
                }
            }
        } else {
            // in the same bucket, so iterate and compare
            for (; leftTuple != null; leftTuple = (LeftTuple) leftIt.next(leftTuple)) {
                if (constraints.isAllowedCachedRight(bm.getContext(),
                                                     leftTuple)) {
                    if (leftTuple.getStagedType() == LeftTuple.NONE) {
                        trgLeftTuples.addUpdate(leftTuple);
                    }
                    final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                    LeftTuple temp;
                    if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                        temp = childLeftTuple.getRightParentNext();
                        // we must re-add this to ensure deterministic iteration
                        childLeftTuple.reAddLeft();
                        removeMatch(accNode,
                                    accumulate,
                                    rightTuple,
                                    childLeftTuple,
                                    wm,
                                    am,
                                    accctx,
                                    true);
                        childLeftTuple = temp;
                    }
                    // add a new match
                    addMatch(accNode, accumulate, leftTuple, rightTuple,
                             null, childLeftTuple, wm, am,
                             accctx, true, false);
                } else if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                    if (leftTuple.getStagedType() == LeftTuple.NONE) {
                        trgLeftTuples.addUpdate(leftTuple);
                    }

                    LeftTuple temp = childLeftTuple.getRightParentNext();
                    final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                    // FIXME This will be really slow, if it re-accumulates on the same LeftTuple (MDP)
                    // remove the match
                    removeMatch(accNode,
                                accumulate,
                                rightTuple,
                                childLeftTuple,
                                wm,
                                am,
                                accctx,
                                true);

                    childLeftTuple = temp;
                }
                // else do nothing, was false before and false now.
            }
        }
    }

    private void doRightDeletes(AccumulateNode accNode,
                                AccumulateMemory am,
                                InternalWorkingMemory wm,
                                TupleSets<RightTuple> srcRightTuples,
                                TupleSets<LeftTuple> trgLeftTuples) {
        TupleMemory rtm = am.getBetaMemory().getRightTupleMemory();
        Accumulate accumulate = accNode.getAccumulate();

        for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if (rightTuple.getMemory() != null) {
                // it may have been staged and never actually added
                rtm.remove(rightTuple);

                if (rightTuple.getFirstChild() != null) {
                    LeftTuple match = rightTuple.getFirstChild();

                    while (match != null) {
                        LeftTuple nextLeft = match.getRightParentNext();

                        LeftTuple leftTuple = match.getLeftParent();
                        final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                        // FIXME This will be really slow, if it re-accumulates on the same LeftTuple (MDP)
                        removeMatch(accNode, accumulate, rightTuple, match, wm, am, accctx, true);

                        if (leftTuple.getStagedType() == LeftTuple.NONE) {
                            trgLeftTuples.addUpdate(leftTuple);
                        }

                        match = nextLeft;
                    }
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
    }

    protected void evaluateResultConstraints(final AccumulateNode accNode,
                                            final LeftTupleSink sink,
                                            final Accumulate accumulate,
                                            final LeftTuple leftTuple,
                                            final PropagationContext context,
                                            final InternalWorkingMemory workingMemory,
                                            final AccumulateMemory memory,
                                            final BaseAccumulation accctx,
                                            final TupleSets<LeftTuple> trgLeftTuples,
                                            final TupleSets<LeftTuple> stagedLeftTuples) {

        PropagationContext propagationContext = accctx.getPropagationContext();
        accctx.setPropagationContext( null );

        Object result = accumulate.getResult(memory.workingMemoryContext, accctx, leftTuple, workingMemory);
        propagateResult( accNode, sink, leftTuple, context, workingMemory, memory, trgLeftTuples, stagedLeftTuples,
                         null, result, (AccumulateContextEntry) accctx, propagationContext, workingMemory.getSessionConfiguration().isAccumulateNullPropagation());
    }

    protected Object createResult( AccumulateNode accNode, Object key, Object result ) {
        return result;
    }
}

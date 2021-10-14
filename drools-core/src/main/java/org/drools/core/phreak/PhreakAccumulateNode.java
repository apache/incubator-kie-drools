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
import org.drools.core.common.TupleSetsImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.AccumulateNode.AccumulateContextEntry;
import org.drools.core.reteoo.AccumulateNode.BaseAccumulation;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.FastIterator;

import static org.drools.core.phreak.RuleNetworkEvaluator.normalizeStagedTuples;

public class PhreakAccumulateNode {

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
        if (srcLeftTuples.getDeleteFirst() != null) {
            // use the real target here, as dealing direct with left tuples
            doLeftDeletes(accNode, am, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

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

        if (srcLeftTuples.getUpdateFirst() != null ) {
            RuleNetworkEvaluator.doUpdatesReorderLeftMemory(bm, srcLeftTuples);
            doLeftUpdates(accNode, am, wm, srcLeftTuples, tempLeftTuples);
        }

        if (!accNode.isRightInputIsRiaNode()) {
            // Non subnetworks ore process right then left. This because it's typically faster to ensure all RightTuples
            // are in place then you can iterate with the left evaluation cached.
            if (srcRightTuples.getInsertFirst() != null) {
                doRightInserts(accNode, am, wm, srcRightTuples, tempLeftTuples);
            }

            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(accNode, am, wm, srcLeftTuples, tempLeftTuples);
            }
        } else {
            // subnetworks process left then right. It ensures the LTM is not empty and the acctx is initialised.
            // It then returns and all the matching can safely be done by the RightTuple
            if (srcLeftTuples.getInsertFirst() != null) {
                doLeftInserts(accNode, am, wm, srcLeftTuples, tempLeftTuples);
            }

            if (srcRightTuples.getInsertFirst() != null) {
                doRightInserts(accNode, am, wm, srcRightTuples, tempLeftTuples);
            }
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

    private void doLeftInserts(AccumulateNode accNode,
                               AccumulateMemory am,
                               InternalWorkingMemory wm,
                               TupleSets<LeftTuple> srcLeftTuples,
                               TupleSets<LeftTuple> trgLeftTuples) {

        Accumulate accumulate = accNode.getAccumulate();
        BetaMemory bm = am.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = accNode.getRawConstraints();

        boolean leftTupleMemoryEnabled = accNode.isLeftTupleMemoryEnabled();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            boolean useLeftMemory = leftTupleMemoryEnabled || RuleNetworkEvaluator.useLeftMemory(accNode, leftTuple);

            if (useLeftMemory) {
                ltm.add(leftTuple);
            }

            BaseAccumulation accresult = initAccumulationContext( am, wm, accumulate, leftTuple );
            if (accNode.isRightInputIsRiaNode()) {
                // This is a subnetwork, do not process further. As all matches will processed
                // by the right insert. This is to avoid double iteration (first right side iteration
                // then left side iteration) or for the join to find matching tuple chains, which it previously
                // did via subsumption checking.
                leftTuple.clearStaged();
                trgLeftTuples.addInsert( leftTuple );
                leftTuple = next;

                continue;
            }

            constraints.updateFromTuple( contextEntry,
                                         wm,
                                         leftTuple );

            FastIterator rightIt = accNode.getRightIterator(rtm);

            for (RightTuple rightTuple = accNode.getFirstRightTuple(leftTuple,
                                                                    rtm,
                                                                    null,
                                                                    rightIt); rightTuple != null; ) {
                RightTuple nextRightTuple = (RightTuple) rightIt.next(rightTuple);

                if (constraints.isAllowedCachedLeft(contextEntry,
                                                    rightTuple.getFactHandleForEvaluation())) {
                    // add a match
                    addMatch(accNode, accumulate, leftTuple, rightTuple,
                             null, null, wm, am,
                             accresult, useLeftMemory, true);
                }

                rightTuple = nextRightTuple;
            }

            leftTuple.clearStaged();
            trgLeftTuples.addInsert( leftTuple );

            constraints.resetTuple( contextEntry );

            leftTuple = next;
        }
        constraints.resetTuple( contextEntry );
    }

    BaseAccumulation initAccumulationContext( AccumulateMemory am, InternalWorkingMemory wm, Accumulate accumulate, LeftTuple leftTuple ) {
        AccumulateContext accContext = new AccumulateContext();
        leftTuple.setContextObject(accContext);

        initContext(am.workingMemoryContext, wm, accumulate, leftTuple, accContext);
        return accContext;
    }

    public static void initContext(Object workingMemoryContext, InternalWorkingMemory wm, Accumulate accumulate, Tuple leftTuple, AccumulateContextEntry accContext) {
        // Create the function context, but allow init to override it.
        Object funcContext = accumulate.createFunctionContext();
        funcContext = accumulate.init(workingMemoryContext, accContext, funcContext, leftTuple, wm);
        accContext.setFunctionContext(funcContext);
    }

    private void doRightInserts(AccumulateNode accNode,
                                AccumulateMemory am,
                                InternalWorkingMemory wm,
                                TupleSets<RightTuple> srcRightTuples,
                                TupleSets<LeftTuple> trgLeftTuples) {
        Accumulate accumulate = accNode.getAccumulate();

        BetaMemory bm = am.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = accNode.getRawConstraints();

        if (srcRightTuples.getInsertSize() > 32 && rtm instanceof AbstractHashTable ) {
            ((AbstractHashTable) rtm).ensureCapacity(srcRightTuples.getInsertSize());
        }

        boolean tupleMemoryEnabled = accNode.isLeftTupleMemoryEnabled();

        for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            boolean useTupleMemory = tupleMemoryEnabled || RuleNetworkEvaluator.useLeftMemory(accNode, rightTuple);

            if (useTupleMemory || !accNode.isRightInputIsRiaNode()) {
                // If tuple memory is off, it will still be when it is not a subnetwork.
                rtm.add(rightTuple);
            }

            if (accNode.isRightInputIsRiaNode() || (ltm != null && ltm.size() > 0)) {
                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandleForEvaluation() );

                FastIterator leftIt = accNode.getLeftIterator( ltm );

                for ( LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, leftIt ); leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                        addMatch( accNode, accumulate, leftTuple, rightTuple,
                                  null, null, wm, am,
                                  accctx, true, false );

                        // right inserts and updates are done first
                        // so any existing leftTuples we know are updates, but only add if not already added
                        if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                            trgLeftTuples.addUpdate( leftTuple );
                        }

                    }
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle( contextEntry );
    }

    private void doLeftUpdates(AccumulateNode accNode,
                               AccumulateMemory am,
                               InternalWorkingMemory wm,
                               TupleSets<LeftTuple> srcLeftTuples,
                               TupleSets<LeftTuple> trgLeftTuples) {
        BetaMemory bm = am.getBetaMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        Accumulate accumulate = accNode.getAccumulate();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = accNode.getRawConstraints();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();

            if (accNode.isRightInputIsRiaNode()) {
                // This is a subnetwork, do not process further. As all matches will processed
                // by the right updates. This is to avoid double iteration (first right side iteration
                // then left side iteration) or for the join to find matching tuple chains, which it previously
                // did via subsumption checking.
                leftTuple.clearStaged();
                trgLeftTuples.addUpdate(leftTuple);
                leftTuple = next;
                continue;
            }

            constraints.updateFromTuple(contextEntry,
                                        wm,
                                        leftTuple);

            FastIterator rightIt = accNode.getRightIterator(rtm);
            RightTuple rightTuple = accNode.getFirstRightTuple(leftTuple,
                                                               rtm,
                                                               null,
                                                               rightIt);

            LeftTuple childLeftTuple = leftTuple.getFirstChild();

            // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
            // if rightTuple is null, we assume there was a bucket change and that bucket is empty
            if (childLeftTuple != null && rtm.isIndexed() && !rightIt.isFullIterator() && (rightTuple == null || (rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory()))) {
                // our index has changed, so delete all the previous matchings
                removePreviousMatchesForLeftTuple(accumulate, leftTuple, wm, am, accctx, true);

                childLeftTuple = null; // null so the next check will attempt matches for new bucket
            }

            // we can't do anything if RightTupleMemory is empty
            if (rightTuple != null) {
                doLeftUpdatesProcessChildren(accNode,
                                             am,
                                             wm,
                                             bm,
                                             accumulate,
                                             constraints,
                                             rightIt,
                                             leftTuple,
                                             accctx,
                                             rightTuple,
                                             childLeftTuple);
            }

            leftTuple.clearStaged();
            trgLeftTuples.addUpdate(leftTuple);

            leftTuple = next;
        }
        constraints.resetTuple(contextEntry);
    }

    private void doLeftUpdatesProcessChildren(AccumulateNode accNode,
                                              AccumulateMemory am,
                                              InternalWorkingMemory wm,
                                              BetaMemory bm,
                                              Accumulate accumulate,
                                              BetaConstraints constraints,
                                              FastIterator rightIt,
                                              LeftTuple leftTuple,
                                              final BaseAccumulation accctx,
                                              RightTuple rightTuple,
                                              LeftTuple match) {
        if (match == null) {
            // either we are indexed and changed buckets or
            // we had no children before, but there is a bucket to potentially match, so try as normal assert
            for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                    rightTuple.getFactHandleForEvaluation())) {
                    // add a new match
                    addMatch(accNode, accumulate, leftTuple, rightTuple,
                             null, null, wm, am,
                             accctx, true, true);
                }
            }
        } else {
            boolean isDirty = false;
            // in the same bucket, so iterate and compare
            for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                    rightTuple.getFactHandleForEvaluation())) {
                    if (match == null || match.getRightParent() != rightTuple) {
                        // add a new match
                        addMatch(accNode, accumulate, leftTuple, rightTuple,
                                 match, null, wm, am,
                                 accctx, true, true);
                    } else {
                        // we must re-add this to ensure deterministic iteration
                        LeftTuple temp = match.getHandleNext();
                        match.reAddRight();
                        match = temp;
                        isDirty = accumulate.hasRequiredDeclarations();
                    }
                } else if (match != null && match.getRightParent() == rightTuple) {
                    LeftTuple temp = match.getHandleNext();
                    // remove the match
                    boolean reversed = removeMatch(accNode, accumulate, rightTuple, match,
                                                   wm, am, accctx, false);
                    match = temp;
                    // the next line means that when a match is removed from the current leftTuple
                    // and the accumulate does not support the reverse operation, then the whole
                    // result is dirty (since removeMatch above is not recalculating the total)
                    // and we need to do this later
                    isDirty = !reversed;
                }
                // else do nothing, was false before and false now.
            }
            if (isDirty) {
                reaccumulateForLeftTuple(accNode,
                                         accumulate,
                                         leftTuple,
                                         null,
                                         null,
                                         wm,
                                         am,
                                         accctx,
                                         true);
            }
        }
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


    private void doLeftDeletes(AccumulateNode accNode,
                               AccumulateMemory am,
                               InternalWorkingMemory wm,
                               TupleSets<LeftTuple> srcLeftTuples,
                               TupleSets<LeftTuple> trgLeftTuples,
                               TupleSets<LeftTuple> stagedLeftTuples) {
        BetaMemory bm = am.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();
        Accumulate accumulate = accNode.getAccumulate();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if (leftTuple.getMemory() != null) {
                // it may have been staged and never actually added
                ltm.remove(leftTuple);

                BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
                leftTuple.setContextObject( null );

                removePreviousMatchesForLeftTuple(accumulate, leftTuple, wm, am, accctx, false);

                propagateDelete( trgLeftTuples, stagedLeftTuples, accctx );
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    protected void propagateDelete( TupleSets<LeftTuple> trgLeftTuples, TupleSets<LeftTuple> stagedLeftTuples, Object accPropCtx ) {
        AccumulateContextEntry entry =  (AccumulateContextEntry) accPropCtx;
        if ( entry.isPropagated() ) {
            normalizeStagedTuples( stagedLeftTuples, entry.getResultLeftTuple() );
            trgLeftTuples.addDelete( entry.getResultLeftTuple() );
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

    protected final void propagateResult(AccumulateNode accNode, LeftTupleSink sink, LeftTuple leftTuple, PropagationContext context,
                                         InternalWorkingMemory workingMemory, AccumulateMemory memory, TupleSets<LeftTuple> trgLeftTuples,
                                         TupleSets<LeftTuple> stagedLeftTuples, Object key, Object result,
                                         AccumulateContextEntry accPropCtx, PropagationContext propagationContext, boolean allowNullPropagation) {
        if ( !allowNullPropagation && result == null) {
            if ( accPropCtx.isPropagated()) {
                // retract
                trgLeftTuples.addDelete( accPropCtx.getResultLeftTuple());
                accPropCtx.setPropagated( false );
            }
            return;
        }

        if ( accPropCtx.getResultFactHandle() == null) {
            InternalFactHandle handle = accNode.createResultFactHandle(context, workingMemory, leftTuple, createResult(accNode, key, result ));
            accPropCtx.setResultFactHandle(handle);
            accPropCtx.setResultLeftTuple( sink.createLeftTuple(handle, leftTuple, sink ));
        } else {
            accPropCtx.getResultFactHandle().setObject( createResult(accNode, key, result) );
        }

        // First alpha node filters
        AlphaNodeFieldConstraint[] resultConstraints = accNode.getResultConstraints();
        BetaConstraints resultBinder = accNode.getResultBinder();
        boolean isAllowed = true;
        for ( AlphaNodeFieldConstraint resultConstraint : resultConstraints ) {
            if ( !resultConstraint.isAllowed( accPropCtx.getResultFactHandle(), workingMemory ) ) {
                isAllowed = false;
                break;
            }
        }
        if (isAllowed) {
            resultBinder.updateFromTuple( memory.resultsContext, workingMemory, leftTuple );
            if (!resultBinder.isAllowedCachedLeft( memory.resultsContext, accPropCtx.getResultFactHandle())) {
                isAllowed = false;
            }
            resultBinder.resetTuple( memory.resultsContext );
        }


        LeftTuple childLeftTuple = accPropCtx.getResultLeftTuple();
        childLeftTuple.setPropagationContext( propagationContext != null ? propagationContext : leftTuple.getPropagationContext() );

        if ( accPropCtx.isPropagated()) {
            normalizeStagedTuples( stagedLeftTuples, childLeftTuple );

            if (isAllowed) {
                // modify
                trgLeftTuples.addUpdate(childLeftTuple);
            } else {
                // retract
                trgLeftTuples.addDelete(childLeftTuple);
                accPropCtx.setPropagated( false );
            }
        } else if (isAllowed) {
            // assert
            trgLeftTuples.addInsert(childLeftTuple);
            accPropCtx.setPropagated( true );
        }
    }

    protected Object createResult( AccumulateNode accNode, Object key, Object result ) {
        return result;
    }

    private void addMatch(final AccumulateNode accNode,
                          final Accumulate accumulate,
                          final LeftTuple leftTuple,
                          final RightTuple rightTuple,
                          final LeftTuple currentLeftChild,
                          final LeftTuple currentRightChild,
                          final InternalWorkingMemory wm,
                          final AccumulateMemory am,
                          final BaseAccumulation accctx,
                          final boolean useLeftMemory,
                          final boolean leftPropagation) {
        LeftTuple tuple = leftTuple;
        InternalFactHandle handle = rightTuple.getFactHandle();

        if (accNode.isRightInputIsRiaNode()) {
            // if there is a subnetwork, handle must be unwrapped
            tuple = (LeftTuple) rightTuple;
            handle = rightTuple.getFactHandleForEvaluation();
        }

        if (leftPropagation && handle.isExpired()) {
            return;
        }

        accctx.setPropagationContext(rightTuple.getPropagationContext());

        Object value = accumulate.accumulate(am.workingMemoryContext,
                                             accctx, tuple,
                                             handle, wm);

        // in sequential mode, we don't need to keep record of matched tuples
        if (useLeftMemory) {
            // linking left and right by creating a new left tuple
            LeftTuple match = accNode.createLeftTuple(leftTuple, rightTuple,
                                                      currentLeftChild, currentRightChild,
                                                      accNode,true);

            postAccumulate(accNode, accctx, match);

            match.setContextObject(value);
        }
    }

    void postAccumulate(AccumulateNode accNode, Object accctx, LeftTuple match) {
        // this is only implemented by GroupBy
    }

    /**
     * Removes a match between left and right tuple
     */
    private boolean removeMatch(final AccumulateNode accNode,
                                final Accumulate accumulate,
                                final RightTuple rightTuple,
                                final LeftTuple match,
                                final InternalWorkingMemory wm,
                                final AccumulateMemory am,
                                final BaseAccumulation accctx,
                                final boolean reaccumulate) {
        // save the matching tuple
        LeftTuple leftParent = match.getLeftParent();
        RightTuple rightParent = match.getRightParent();

        // removing link between left and right
        match.unlinkFromLeftParent();
        match.unlinkFromRightParent();

        // if there is a subnetwork, we need to unwrap the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        LeftTuple tuple = leftParent;
        if (accNode.isRightInputIsRiaNode()) {
            tuple = (LeftTuple) rightTuple;
            handle = rightTuple.getFactHandleForEvaluation();
        }

        // just reverse this single match
        boolean reversed = accumulate.tryReverse(am.workingMemoryContext,
                                                 accctx,
                                                 tuple,
                                                 handle,
                                                 rightParent,
                                                 match,
                                                 wm);
        if (!reversed) {
            // otherwise need to recalculate all matches for the given leftTuple
            reaccumulateForLeftTuple(accNode,
                                     accumulate,
                                     leftParent,
                                     rightParent,
                                     match,
                                     wm,
                                     am,
                                     accctx,
                                     reaccumulate);
        }

        return reversed;
    }


    protected void reaccumulateForLeftTuple(final AccumulateNode accNode,
                                            final Accumulate accumulate,
                                            final LeftTuple leftParent,
                                            final RightTuple unused1,
                                            final LeftTuple unused2,
                                            final InternalWorkingMemory wm,
                                            final AccumulateMemory am,
                                            final BaseAccumulation accctx,
                                            final boolean reaccumulate) {
        if (reaccumulate) {
            reinit(accumulate, leftParent, wm, am, accctx);

            for (LeftTuple childMatch = leftParent.getFirstChild(); childMatch != null; childMatch = childMatch.getHandleNext()) {
                RightTuple         rightTuple  = childMatch.getRightParent();
                InternalFactHandle childHandle = rightTuple.getFactHandle();
                LeftTuple          tuple       = leftParent;
                if (accNode.isRightInputIsRiaNode()) {
                    // if there is a subnetwork, handle must be unwrapped
                    tuple = (LeftTuple) rightTuple;
                    childHandle = rightTuple.getFactHandleForEvaluation();
                }

                Object value = accumulate.accumulate(am.workingMemoryContext, accctx, tuple, childHandle, wm);
                postAccumulate(accNode, accctx, childMatch);
                childMatch.setContextObject(value);
            }
        }
    }

    private void removePreviousMatchesForRightTuple(final AccumulateNode accNode,
                                                    final Accumulate accumulate,
                                                    final RightTuple rightTuple,
                                                    final InternalWorkingMemory workingMemory,
                                                    final AccumulateMemory memory,
                                                    final LeftTuple firstChild,
                                                    final TupleSets<LeftTuple> trgLeftTuples) {
        for (LeftTuple match = firstChild; match != null; ) {
            final LeftTuple next = match.getRightParentNext();

            final LeftTuple leftTuple = match.getLeftParent();
            final BaseAccumulation accctx = (BaseAccumulation) leftTuple.getContextObject();
            removeMatch(accNode,
                        accumulate,
                        rightTuple,
                        match,
                        workingMemory,
                        memory,
                        accctx,
                        true);

            if (leftTuple.getStagedType() == LeftTuple.NONE) {
                trgLeftTuples.addUpdate(leftTuple);
            }

            match = next;
        }
    }

    private static void removePreviousMatchesForLeftTuple(final Accumulate accumulate,
                                                          final LeftTuple leftTuple,
                                                          final InternalWorkingMemory workingMemory,
                                                          final AccumulateMemory memory,
                                                          final BaseAccumulation accctx,
                                                          boolean reInit) {
        for (LeftTuple match = leftTuple.getFirstChild(); match != null; ) {
            LeftTuple next = match.getHandleNext();
            match.unlinkFromRightParent();
            match.unlinkFromLeftParent();
            match = next;
        }

        if (reInit) {
            reinit(accumulate, leftTuple, workingMemory, memory, accctx);
        }
    }

    private static void reinit(Accumulate accumulate, LeftTuple leftTuple, InternalWorkingMemory workingMemory, AccumulateMemory memory, BaseAccumulation accctx) {
        // Create the function context, but allow init to override it.
        Object funcContext = ((AccumulateContextEntry) accctx).getFunctionContext();
        funcContext = accumulate.init(memory.workingMemoryContext, accctx, funcContext, leftTuple, workingMemory);
        ((AccumulateContextEntry) accctx).setFunctionContext(funcContext);
    }
}

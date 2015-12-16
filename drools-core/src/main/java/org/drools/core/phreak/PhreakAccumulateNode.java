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
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:45
* To change this template use File | Settings | File Templates.
*/
public class PhreakAccumulateNode {
    public void doNode(AccumulateNode accNode,
                       LeftTupleSink sink,
                       AccumulateMemory am,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {
        BetaMemory bm = am.getBetaMemory();
        if ( !bm.getStagedRightTuples().isEmpty() ) {
            bm.setNodeDirtyWithoutNotify();
        }
        TupleSets<RightTuple> srcRightTuples = bm.getStagedRightTuples().takeAll();

        // order of left and right operations is to minimise wasted of innefficient joins.

        // We need to collect which leftTuple where updated, so that we can
        // add their result tuple to the real target tuples later
        TupleSets<LeftTuple> tempLeftTuples = new TupleSetsImpl<LeftTuple>();

        if (srcLeftTuples.getDeleteFirst() != null) {
            // use the real target here, as dealing direct with left tuples
            doLeftDeletes(accNode, am, wm, srcLeftTuples, trgLeftTuples);
        }

        if (srcRightTuples.getDeleteFirst() != null) {
            doRightDeletes(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null ) {
            RuleNetworkEvaluator.doUpdatesReorderLeftMemory(am.getBetaMemory(),
                                                            srcLeftTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            RuleNetworkEvaluator.doUpdatesReorderRightMemory(am.getBetaMemory(),
                                                             srcRightTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            doRightUpdates(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(accNode, am, wm, srcLeftTuples, tempLeftTuples);
        }

        if (srcRightTuples.getInsertFirst() != null) {
            doRightInserts(accNode, am, wm, srcRightTuples, tempLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(accNode, am, wm, srcLeftTuples, tempLeftTuples);
        }

        Accumulate accumulate = accNode.getAccumulate();
        // we do not need collect retracts. RightTuple retracts end up as updates for lefttuples.
        // LeftTuple retracts are already on the trgLeftTuples
        for (LeftTuple leftTuple = tempLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            evaluateResultConstraints(accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                      wm, am, (AccumulateContext) leftTuple.getContextObject(),
                                      trgLeftTuples, stagedLeftTuples);
            leftTuple.clearStaged();
            leftTuple = next;
        }

        for (LeftTuple leftTuple = tempLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            evaluateResultConstraints( accNode, sink, accumulate, leftTuple, leftTuple.getPropagationContext(),
                                       wm, am, (AccumulateContext) leftTuple.getContextObject(),
                                       trgLeftTuples, stagedLeftTuples );
            leftTuple.clearStaged();
            leftTuple = next;
        }

        srcRightTuples.resetAll();

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(AccumulateNode accNode,
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

            AccumulateContext accresult = new AccumulateContext();

            leftTuple.setContextObject( accresult );

            accresult.context = accumulate.createContext();

            accumulate.init(am.workingMemoryContext,
                            accresult.context,
                            leftTuple,
                            wm);

            constraints.updateFromTuple( contextEntry,
                                         wm,
                                         leftTuple );

            FastIterator rightIt = accNode.getRightIterator(rtm);

            for (RightTuple rightTuple = accNode.getFirstRightTuple(leftTuple,
                                                                    rtm,
                                                                    null,
                                                                    rightIt); rightTuple != null; ) {
                RightTuple nextRightTuple = (RightTuple) rightIt.next(rightTuple);

                InternalFactHandle handle = rightTuple.getFactHandle();
                if (constraints.isAllowedCachedLeft(contextEntry,
                                                    handle)) {
                    // add a match
                    addMatch(accNode,
                             accumulate,
                             leftTuple,
                             rightTuple,
                             null,
                             null,
                             wm,
                             am,
                             accresult,
                             useLeftMemory);

                    if (!useLeftMemory && accNode.isRightInputIsRiaNode()) {
                        // RIAN with no left memory must have their right tuples removed
                        rtm.remove(rightTuple);
                    }
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

    public void doRightInserts(AccumulateNode accNode,
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

        for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            rtm.add( rightTuple );

            if ( ltm != null && ltm.size() > 0 ) {
                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                FastIterator leftIt = accNode.getLeftIterator( ltm );

                for ( LeftTuple leftTuple = accNode.getFirstLeftTuple( rightTuple, ltm, leftIt ); leftTuple != null; leftTuple = (LeftTuple) leftIt.next( leftTuple ) ) {
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                        addMatch( accNode,
                                  accumulate,
                                  leftTuple,
                                  rightTuple,
                                  null,
                                  null,
                                  wm,
                                  am,
                                  accctx,
                                  true );

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

    public void doLeftUpdates(AccumulateNode accNode,
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
            final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();

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
                removePreviousMatchesForLeftTuple(accumulate,
                                                  leftTuple,
                                                  wm,
                                                  am,
                                                  accctx,
                                                  true);

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
                                              final AccumulateContext accctx,
                                              RightTuple rightTuple,
                                              LeftTuple childLeftTuple) {
        if (childLeftTuple == null) {
            // either we are indexed and changed buckets or
            // we had no children before, but there is a bucket to potentially match, so try as normal assert
            for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                final InternalFactHandle handle = rightTuple.getFactHandle();
                if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                    handle)) {
                    // add a new match
                    addMatch(accNode,
                             accumulate,
                             leftTuple,
                             rightTuple,
                             null,
                             null,
                             wm,
                             am,
                             accctx,
                             true);
                }
            }
        } else {
            boolean isDirty = false;
            // in the same bucket, so iterate and compare
            for (; rightTuple != null; rightTuple = (RightTuple) rightIt.next(rightTuple)) {
                final InternalFactHandle handle = rightTuple.getFactHandle();

                if (constraints.isAllowedCachedLeft(bm.getContext(),
                                                    handle)) {
                    if (childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple) {
                        // add a new match
                        addMatch(accNode,
                                 accumulate,
                                 leftTuple,
                                 rightTuple,
                                 childLeftTuple,
                                 null,
                                 wm,
                                 am,
                                 accctx,
                                 true);
                    } else {
                        // we must re-add this to ensure deterministic iteration
                        LeftTuple temp = childLeftTuple.getHandleNext();
                        childLeftTuple.reAddRight();
                        childLeftTuple = temp;
                        isDirty = accumulate.hasRequiredDeclarations();
                    }
                } else if (childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple) {
                    LeftTuple temp = childLeftTuple.getHandleNext();
                    // remove the match
                    removeMatch(accNode,
                                accumulate,
                                rightTuple,
                                childLeftTuple,
                                wm,
                                am,
                                accctx,
                                false);
                    childLeftTuple = temp;
                    // the next line means that when a match is removed from the current leftTuple
                    // and the accumulate does not support the reverse operation, then the whole
                    // result is dirty (since removeMatch above is not recalculating the total)
                    // and we need to do this later
                    isDirty = !accumulate.supportsReverse();
                }
                // else do nothing, was false before and false now.
            }
            if (isDirty) {
                reaccumulateForLeftTuple(accNode,
                                         accumulate,
                                         leftTuple,
                                         wm,
                                         am,
                                         accctx);
            }
        }
    }

    public void doRightUpdates(AccumulateNode accNode,
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
                                                  rightTuple.getFactHandle() );

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
                    if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                        trgLeftTuples.addUpdate( leftTuple );
                    }

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
                    final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                    // add a new match
                    addMatch(accNode,
                             accumulate,
                             leftTuple,
                             rightTuple,
                             null,
                             null,
                             wm,
                             am,
                             accctx,
                             true);
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
                    final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                    LeftTuple temp = null;
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
                    addMatch(accNode,
                             accumulate,
                             leftTuple,
                             rightTuple,
                             null,
                             childLeftTuple,
                             wm,
                             am,
                             accctx,
                             true);
                    if (temp != null) {
                        childLeftTuple = temp;
                    }
                } else if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                    if (leftTuple.getStagedType() == LeftTuple.NONE) {
                        trgLeftTuples.addUpdate(leftTuple);
                    }

                    LeftTuple temp = childLeftTuple.getRightParentNext();
                    final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
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


    public void doLeftDeletes(AccumulateNode accNode,
                              AccumulateMemory am,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples) {
        BetaMemory bm = am.getBetaMemory();
        TupleMemory ltm = bm.getLeftTupleMemory();
        Accumulate accumulate = accNode.getAccumulate();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if (leftTuple.getMemory() != null) {
                // it may have been staged and never actually added
                ltm.remove(leftTuple);


                final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                leftTuple.setContextObject( null );

                removePreviousMatchesForLeftTuple(accumulate,
                                                  leftTuple,
                                                  wm,
                                                  am,
                                                  accctx,
                                                  false);

                if (accctx.propagated) {
                    trgLeftTuples.addDelete(accctx.resultLeftTuple);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doRightDeletes(AccumulateNode accNode,
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
                        final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
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

    private void evaluateResultConstraints(final AccumulateNode accNode,
                                           final LeftTupleSink sink,
                                           final Accumulate accumulate,
                                           final LeftTuple leftTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory,
                                           final AccumulateMemory memory,
                                           final AccumulateContext accctx,
                                           final TupleSets<LeftTuple> trgLeftTuples,
                                           final TupleSets<LeftTuple> stagedLeftTuples) {
        // get the actual result
        Object result = accumulate.getResult(memory.workingMemoryContext,
                                             accctx.context,
                                             leftTuple,
                                             workingMemory);
        if (result == null) {
            return;
        }

        if (accctx.getResultFactHandle() == null) {
            final InternalFactHandle handle = accNode.createResultFactHandle(context,
                                                                             workingMemory,
                                                                             leftTuple,
                                                                             result);

            accctx.setResultFactHandle(handle);

            accctx.setResultLeftTuple(sink.createLeftTuple(handle, leftTuple, sink));
        } else {
            accctx.getResultFactHandle().setObject(result);
        }

        // First alpha node filters
        AlphaNodeFieldConstraint[] resultConstraints = accNode.getResultConstraints();
        BetaConstraints resultBinder = accNode.getResultBinder();
        boolean isAllowed = true;
        for (int i = 0, length = resultConstraints.length; i < length; i++) {
            if (!resultConstraints[i].isAllowed(accctx.resultFactHandle,
                                                workingMemory)) {
                isAllowed = false;
                break;
            }
        }
        if (isAllowed) {
            resultBinder.updateFromTuple(memory.resultsContext,
                                         workingMemory,
                                         leftTuple);
            if (!resultBinder.isAllowedCachedLeft(memory.resultsContext,
                                                  accctx.getResultFactHandle())) {
                isAllowed = false;
            }
            resultBinder.resetTuple(memory.resultsContext);
        }


        LeftTuple childLeftTuple = accctx.getResultLeftTuple();
        if (accctx.getPropagationContext() != null) {
            childLeftTuple.setPropagationContext(accctx.getPropagationContext());
            accctx.setPropagationContext(null);
        } else {
            childLeftTuple.setPropagationContext(leftTuple.getPropagationContext());
        }

        if (accctx.propagated) {
            switch (childLeftTuple.getStagedType()) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    stagedLeftTuples.removeInsert(childLeftTuple);
                    break;
                case LeftTuple.UPDATE:
                    stagedLeftTuples.removeUpdate(childLeftTuple);
                    break;
            }

            if (isAllowed) {
                // modify
                trgLeftTuples.addUpdate(childLeftTuple);
            } else {
                // retract
                trgLeftTuples.addDelete(childLeftTuple);
                accctx.propagated = false;
            }
        } else if (isAllowed) {
            // assert
            trgLeftTuples.addInsert(childLeftTuple);
            accctx.propagated = true;
        }

    }

    private static void addMatch(final AccumulateNode accNode,
                                 final Accumulate accumulate,
                                 final LeftTuple leftTuple,
                                 final RightTuple rightTuple,
                                 final LeftTuple currentLeftChild,
                                 final LeftTuple currentRightChild,
                                 final InternalWorkingMemory wm,
                                 final AccumulateMemory am,
                                 final AccumulateContext accctx,
                                 final boolean useLeftMemory) {
        LeftTuple tuple = leftTuple;
        InternalFactHandle handle = rightTuple.getFactHandle();
        if (accNode.isUnwrapRightObject()) {
            // if there is a subnetwork, handle must be unwrapped
            tuple = (LeftTuple) handle.getObject();
        }

        accctx.setPropagationContext(rightTuple.getPropagationContext());

        accumulate.accumulate(am.workingMemoryContext,
                              accctx.context,
                              tuple,
                              handle,
                              wm);

        // in sequential mode, we don't need to keep record of matched tuples
        if (useLeftMemory) {
            // linking left and right by creating a new left tuple
            accNode.createLeftTuple(leftTuple,
                                    rightTuple,
                                    currentLeftChild,
                                    currentRightChild,
                                    accNode,
                                    true);
        }
    }

    /**
     * Removes a match between left and right tuple
     */
    private static void removeMatch(final AccumulateNode accNode,
                                    final Accumulate accumulate,
                                    final RightTuple rightTuple,
                                    final LeftTuple match,
                                    final InternalWorkingMemory wm,
                                    final AccumulateMemory am,
                                    final AccumulateContext accctx,
                                    final boolean reaccumulate) {
        // save the matching tuple
        LeftTuple leftTuple = match.getLeftParent();

        // removing link between left and right
        match.unlinkFromLeftParent();
        match.unlinkFromRightParent();

        // if there is a subnetwork, we need to unwrap the object from inside the tuple
        InternalFactHandle handle = rightTuple.getFactHandle();
        LeftTuple tuple = leftTuple;
        if (accNode.isUnwrapRightObject()) {
            tuple = (LeftTuple) handle.getObject();
        }

        if (accumulate.supportsReverse()) {
            // just reverse this single match
            accumulate.reverse(am.workingMemoryContext,
                               accctx.context,
                               tuple,
                               handle,
                               wm);
        } else {
            // otherwise need to recalculate all matches for the given leftTuple
            if (reaccumulate) {
                reaccumulateForLeftTuple(accNode,
                                         accumulate,
                                         leftTuple,
                                         wm,
                                         am,
                                         accctx);

            }
        }
    }


    private static void reaccumulateForLeftTuple(final AccumulateNode accNode,
                                                 final Accumulate accumulate,
                                                 final LeftTuple leftTuple,
                                                 final InternalWorkingMemory wm,
                                                 final AccumulateMemory am,
                                                 final AccumulateContext accctx) {
        accumulate.init(am.workingMemoryContext,
                        accctx.context,
                        leftTuple,
                        wm);
        for (LeftTuple childMatch = leftTuple.getFirstChild(); childMatch != null; childMatch = childMatch.getHandleNext()) {
            InternalFactHandle childHandle = childMatch.getRightParent().getFactHandle();
            LeftTuple tuple = leftTuple;
            if (accNode.isUnwrapRightObject()) {
                // if there is a subnetwork, handle must be unwrapped
                tuple = (LeftTuple) childHandle.getObject();
            }
            accumulate.accumulate(am.workingMemoryContext,
                                  accctx.context,
                                  tuple,
                                  childHandle,
                                  wm);
        }
    }

    private static void removePreviousMatchesForRightTuple(final AccumulateNode accNode,
                                                           final Accumulate accumulate,
                                                           final RightTuple rightTuple,
                                                           final InternalWorkingMemory workingMemory,
                                                           final AccumulateMemory memory,
                                                           final LeftTuple firstChild,
                                                           final TupleSets<LeftTuple> trgLeftTuples) {
        for (LeftTuple match = firstChild; match != null; ) {
            final LeftTuple next = match.getRightParentNext();

            final LeftTuple leftTuple = match.getLeftParent();
            final AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
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
                                                          final AccumulateContext accctx,
                                                          boolean reInit) {
        for (LeftTuple match = leftTuple.getFirstChild(); match != null; ) {
            LeftTuple next = match.getHandleNext();
            match.unlinkFromRightParent();
            match.unlinkFromLeftParent();
            match = next;
        }

        if (reInit) {
            // since there are no more matches, the following call will just re-initialize the accumulation
            accumulate.init(memory.workingMemoryContext,
                            accctx.context,
                            leftTuple,
                            workingMemory);
        }
    }

}

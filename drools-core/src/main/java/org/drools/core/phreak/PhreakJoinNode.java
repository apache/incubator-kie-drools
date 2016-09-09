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
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.FastIterator;

public class PhreakJoinNode {
    public void doNode(JoinNode joinNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        TupleSets<RightTuple> srcRightTuples = bm.getStagedRightTuples().takeAll();

        if (srcRightTuples.getDeleteFirst() != null) {
            doRightDeletes(bm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(bm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null ) {
            RuleNetworkEvaluator.doUpdatesReorderLeftMemory(bm,
                                                            srcLeftTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            RuleNetworkEvaluator.doUpdatesReorderRightMemory(bm,
                                                             srcRightTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            doRightUpdates(joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcRightTuples.getInsertFirst() != null) {
            doRightInserts(joinNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(joinNode, sink, bm, wm, srcLeftTuples, trgLeftTuples);
        }

        srcRightTuples.resetAll();
        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(JoinNode joinNode,
                              LeftTupleSink sink,
                              BetaMemory bm,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = joinNode.getRawConstraints();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory( joinNode, leftTuple );

            if (useLeftMemory) {
                ltm.add(leftTuple);
            }

            FastIterator it = joinNode.getRightIterator( rtm );

            constraints.updateFromTuple( contextEntry,
                                         wm,
                                         leftTuple );

            for (RightTuple rightTuple = joinNode.getFirstRightTuple( leftTuple,
                                                                      rtm,
                                                                      null,
                                                                      it ); rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                if (constraints.isAllowedCachedLeft( contextEntry,
                                                     rightTuple.getFactHandle() )) {
                    insertChildLeftTuple(trgLeftTuples,
                                         leftTuple,
                                         rightTuple,
                                         null,
                                         null,
                                         sink,
                                         useLeftMemory);
                }

            }
            leftTuple.clearStaged();
            leftTuple = next;
        }
        constraints.resetTuple( contextEntry );
    }

    public void doRightInserts(JoinNode joinNode,
                               LeftTupleSink sink,
                               BetaMemory bm,
                               InternalWorkingMemory wm,
                               TupleSets<RightTuple> srcRightTuples,
                               TupleSets<LeftTuple> trgLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = joinNode.getRawConstraints();

        if (srcRightTuples.getInsertSize() > 32 && rtm instanceof AbstractHashTable ) {
            ((AbstractHashTable) rtm).ensureCapacity(srcRightTuples.getInsertSize());
        }

        for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            rtm.add( rightTuple );

            if ( ltm != null && ltm.size() > 0 ) {
                FastIterator it = joinNode.getLeftIterator( ltm );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandleForEvaluation() );

                for ( LeftTuple leftTuple = joinNode.getFirstLeftTuple( rightTuple, ltm, it ); leftTuple != null; leftTuple = (LeftTuple) it.next( leftTuple ) ) {
                    if ( leftTuple.getStagedType() == LeftTuple.UPDATE ) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        continue;
                    }

                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        insertChildLeftTuple( trgLeftTuples,
                                              leftTuple,
                                              rightTuple,
                                              null,
                                              null,
                                              sink,
                                              true );
                    }
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle( contextEntry );
    }

    public void doLeftUpdates(JoinNode joinNode,
                              LeftTupleSink sink,
                              BetaMemory bm,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        TupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = joinNode.getRawConstraints();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            constraints.updateFromTuple(contextEntry,
                                        wm,
                                        leftTuple);

            FastIterator it = joinNode.getRightIterator(rtm);
            RightTuple rightTuple = joinNode.getFirstRightTuple(leftTuple,
                                                                rtm,
                                                                null,
                                                                it);

            // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
            // if rightTuple is null, we assume there was a bucket change and that bucket is empty
            if (rtm.isIndexed() && !it.isFullIterator()) {
                // our index has changed, so delete all the previous propagations
                for (LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; ) {
                    LeftTuple nextChild = childLeftTuple.getHandleNext();
                    if (rightTuple == null || rightTuple.getMemory() != childLeftTuple.getRightParent().getMemory()) {
                        RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    }
                    childLeftTuple = nextChild;
                }
            }

            // we can't do anything if RightTupleMemory is empty
            if (rightTuple != null) {
                doLeftUpdatesProcessChildren(leftTuple.getFirstChild(), leftTuple, rightTuple, stagedLeftTuples, contextEntry, constraints, sink, it, trgLeftTuples);
            }
            leftTuple.clearStaged();
            leftTuple = next;
        }
        constraints.resetTuple(contextEntry);
    }

    public LeftTuple doLeftUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                  LeftTuple leftTuple,
                                                  RightTuple rightTuple,
                                                  TupleSets<LeftTuple> stagedLeftTuples,
                                                  ContextEntry[] contextEntry,
                                                  BetaConstraints constraints,
                                                  LeftTupleSink sink,
                                                  FastIterator it,
                                                  TupleSets<LeftTuple> trgLeftTuples) {
        if (childLeftTuple == null) {
            // either we are indexed and changed buckets or
            // we had no children before, but there is a bucket to potentially match, so try as normal assert
            for (; rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                if (constraints.isAllowedCachedLeft(contextEntry,
                                                    rightTuple.getFactHandle())) {
                    insertChildLeftTuple(trgLeftTuples,
                                         leftTuple,
                                         rightTuple,
                                         null,
                                         null,
                                         sink,
                                         true);
                }
            }
        } else {
            // in the same bucket, so iterate and compare
            for (; rightTuple != null; rightTuple = (RightTuple) it.next(rightTuple)) {
                if (constraints.isAllowedCachedLeft(contextEntry,
                                                    rightTuple.getFactHandle())) {
                    // insert, childLeftTuple is not updated
                    if (childLeftTuple == null || childLeftTuple.getRightParent() != rightTuple) {
                        insertChildLeftTuple(trgLeftTuples,
                                             leftTuple,
                                             rightTuple,
                                             childLeftTuple,
                                             null,
                                             sink,
                                             true);
                    } else {
                        // update, childLeftTuple is updated
                        childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                        updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);

                        LeftTuple nextChildLeftTuple = childLeftTuple.getHandleNext();
                        childLeftTuple.reAddRight();
                        childLeftTuple = nextChildLeftTuple;
                    }
                } else if (childLeftTuple != null && childLeftTuple.getRightParent() == rightTuple) {
                    // delete, childLeftTuple is updated
                    LeftTuple nextChild = childLeftTuple.getHandleNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }
        }

        return childLeftTuple;
    }

    public void doRightUpdates(JoinNode joinNode,
                               LeftTupleSink sink,
                               BetaMemory bm,
                               InternalWorkingMemory wm,
                               TupleSets<RightTuple> srcRightTuples,
                               TupleSets<LeftTuple> trgLeftTuples,
                               TupleSets<LeftTuple> stagedLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = joinNode.getRawConstraints();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();

            if ( ltm != null && ltm.size() > 0 ) {
                FastIterator it = joinNode.getLeftIterator( ltm );
                LeftTuple leftTuple = joinNode.getFirstLeftTuple( rightTuple, ltm, it );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandleForEvaluation() );

                // first check our index (for indexed nodes only) hasn't changed and we are returning the same bucket
                // We assume a bucket change if leftTuple == null
                LeftTuple childLeftTuple = rightTuple.getFirstChild();
                if ( childLeftTuple != null && ltm.isIndexed() && !it.isFullIterator() && ( leftTuple == null || ( leftTuple.getMemory() != childLeftTuple.getLeftParent().getMemory() ) ) ) {
                    // our index has changed, so delete all the previous propagations
                    while ( childLeftTuple != null ) {
                        childLeftTuple.setPropagationContext( rightTuple.getPropagationContext() );
                        LeftTuple nextChild = childLeftTuple.getRightParentNext();
                        RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                        childLeftTuple = nextChild;
                    }
                    // childLeftTuple is now null, so the next check will attempt matches for new bucket
                }

                // we can't do anything if LeftTupleMemory is empty
                if ( leftTuple != null ) {
                    doRightUpdatesProcessChildren( childLeftTuple, leftTuple, rightTuple, stagedLeftTuples, contextEntry, constraints, sink, it, trgLeftTuples );
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle(contextEntry);
    }

    public LeftTuple doRightUpdatesProcessChildren(LeftTuple childLeftTuple,
                                                   LeftTuple leftTuple,
                                                   RightTuple rightTuple,
                                                   TupleSets<LeftTuple> stagedLeftTuples,
                                                   ContextEntry[] contextEntry,
                                                   BetaConstraints constraints,
                                                   LeftTupleSink sink,
                                                   FastIterator it,
                                                   TupleSets<LeftTuple> trgLeftTuples) {
        if (childLeftTuple == null) {
            // either we are indexed and changed buckets or
            // we had no children before, but there is a bucket to potentially match, so try as normal assert
            for (; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple)) {
                if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                    // ignore, as it will get processed via left iteration. Children cannot be processed twice
                    continue;
                }

                if (constraints.isAllowedCachedRight(contextEntry,
                                                     leftTuple)) {
                    insertChildLeftTuple(trgLeftTuples,
                                         leftTuple,
                                         rightTuple,
                                         null,
                                         null,
                                         sink,
                                         true);
                }
            }
        } else {
            // in the same bucket, so iterate and compare
            for (; leftTuple != null; leftTuple = (LeftTuple) it.next(leftTuple)) {
                if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                    // ignore, as it will get processed via left iteration. Children cannot be processed twice
                    continue;
                }
                if (constraints.isAllowedCachedRight(contextEntry,
                                                     leftTuple)) {
                    // insert, childLeftTuple is not updated
                    if (childLeftTuple == null || childLeftTuple.getLeftParent() != leftTuple) {
                        insertChildLeftTuple(trgLeftTuples,
                                             leftTuple,
                                             rightTuple,
                                             null,
                                             childLeftTuple,
                                             sink,
                                             true);
                    } else {
                        // update, childLeftTuple is updated
                        childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                        updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);

                        LeftTuple nextChildLeftTuple = childLeftTuple.getRightParentNext();
                        childLeftTuple.reAddLeft();
                        childLeftTuple = nextChildLeftTuple;
                    }
                } else if (childLeftTuple != null && childLeftTuple.getLeftParent() == leftTuple) {
                    // delete, childLeftTuple is updated
                    childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                    LeftTuple nextChild = childLeftTuple.getRightParentNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }
        }

        return childLeftTuple;
    }

    public void doLeftDeletes(BetaMemory bm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples) {
        TupleMemory ltm = bm.getLeftTupleMemory();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if (leftTuple.getMemory() != null) {
                // it may have been staged and never actually added
                ltm.remove(leftTuple);
            }

            if (leftTuple.getFirstChild() != null) {
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

    public void doRightDeletes(BetaMemory bm,
                               TupleSets<RightTuple> srcRightTuples,
                               TupleSets<LeftTuple> trgLeftTuples,
                               TupleSets<LeftTuple> stagedLeftTuples) {
        TupleMemory rtm = bm.getRightTupleMemory();

        for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            if (rightTuple.getMemory() != null) {
                // it may have been staged and never actually added
                rtm.remove(rightTuple);
            }

            if (rightTuple.getFirstChild() != null) {
                LeftTuple childLeftTuple = rightTuple.getFirstChild();
                childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                while (childLeftTuple != null) {
                    LeftTuple nextChild = childLeftTuple.getRightParentNext();
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                    childLeftTuple = nextChild;
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
    }

    public static void updateChildLeftTuple(LeftTuple childLeftTuple,
                                            TupleSets<LeftTuple> stagedLeftTuples,
                                            TupleSets<LeftTuple> trgLeftTuples) {
        switch (childLeftTuple.getStagedType()) {
            // handle clash with already staged entries
            case LeftTuple.INSERT:
                stagedLeftTuples.removeInsert(childLeftTuple);
                trgLeftTuples.addInsert(childLeftTuple);
                break;
            case LeftTuple.UPDATE:
                stagedLeftTuples.removeUpdate(childLeftTuple);
                trgLeftTuples.addUpdate(childLeftTuple);
                break;
            default:
                trgLeftTuples.addUpdate(childLeftTuple);
        }
    }

    private static void insertChildLeftTuple( TupleSets<LeftTuple> trgLeftTuples,
                                              LeftTuple leftTuple,
                                              RightTuple rightTuple,
                                              LeftTuple currentLeftChild,
                                              LeftTuple currentRightChild,
                                              LeftTupleSink sink,
                                              boolean leftTupleMemoryEnabled ) {
        if (!leftTuple.isExpired() && !rightTuple.isExpired()) {
            trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                         rightTuple,
                                                         currentLeftChild,
                                                         currentRightChild,
                                                         sink,
                                                         leftTupleMemoryEnabled));
        }
    }
}

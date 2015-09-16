/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.RightTupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleMemory;
import org.drools.core.rule.ContextEntry;
import org.drools.core.util.FastIterator;

import static org.drools.core.phreak.PhreakJoinNode.updateChildLeftTuple;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:46
* To change this template use File | Settings | File Templates.
*/
public class PhreakExistsNode {
    public void doNode(ExistsNode existsNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples) {
        RightTupleSets srcRightTuples = bm.getStagedRightTuples().takeAll();


        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(bm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getUpdateFirst() != null )  {
            RuleNetworkEvaluator.doUpdatesExistentialReorderLeftMemory(bm,
                                                                       srcLeftTuples);
        }

        if ( srcRightTuples.getUpdateFirst() != null ) {
            RuleNetworkEvaluator.doUpdatesExistentialReorderRightMemory(bm,
                                                                        existsNode,
                                                                        srcRightTuples); // this also preserves the next rightTuple
        }

        if (srcRightTuples.getInsertFirst() != null) {
            // left deletes must come before right deletes. Otherwise right deletes could
            // stage a deletion, that is later deleted in the rightDelete, causing potential problems
            doRightInserts(existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples);
        }

        if (srcRightTuples.getUpdateFirst() != null) {
            // must come after rightInserts and before rightDeletes, to avoid staging clash
            doRightUpdates(existsNode, sink, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcRightTuples.getDeleteFirst() != null) {
            // must come after rightUpdetes, to avoid staging clash
            doRightDeletes(existsNode, bm, wm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }


        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(existsNode, sink, bm, wm, srcLeftTuples, trgLeftTuples);
        }

        srcRightTuples.resetAll();
        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(ExistsNode existsNode,
                              LeftTupleSink sink,
                              BetaMemory bm,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = existsNode.getRawConstraints();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            FastIterator it = existsNode.getRightIterator(rtm);

            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(existsNode, leftTuple);

            constraints.updateFromTuple( contextEntry,
                                         wm,
                                         leftTuple );

            // This method will also remove rightTuples that are from subnetwork where no leftmemory use used
            RuleNetworkEvaluator.findLeftTupleBlocker( existsNode, rtm, contextEntry, constraints, leftTuple, it, useLeftMemory );

            if (leftTuple.getBlocker() != null) {
                // tuple is not blocked to propagate
                trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                             sink,
                                                             leftTuple.getBlocker().getPropagationContext(), useLeftMemory));
            } else if (useLeftMemory) {
                // LeftTuple is not blocked, so add to memory so other RightTuples can match
                ltm.add(leftTuple);
            }
            leftTuple.clearStaged();
            leftTuple = next;
        }
        constraints.resetTuple( contextEntry );
    }

    public void doRightInserts(ExistsNode existsNode,
                               LeftTupleSink sink,
                               BetaMemory bm,
                               InternalWorkingMemory wm,
                               RightTupleSets srcRightTuples,
                               LeftTupleSets trgLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = existsNode.getRawConstraints();

        for (RightTuple rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();
            rtm.add(rightTuple);

            if ( ltm != null && ltm.size() > 0 ) {
                FastIterator it = existsNode.getLeftIterator( ltm );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );

                for ( LeftTuple leftTuple = existsNode.getFirstLeftTuple( rightTuple, ltm, it ); leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple
                    LeftTuple temp = (LeftTuple) it.next( leftTuple );

                    if ( leftTuple.getStagedType() == LeftTuple.UPDATE ) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        ltm.remove( leftTuple );

                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       sink,
                                                                       rightTuple.getPropagationContext(), true ) );
                    }

                    leftTuple = temp;
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle( contextEntry );
    }

    public void doLeftUpdates(ExistsNode existsNode,
                              LeftTupleSink sink,
                              BetaMemory bm,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = existsNode.getRawConstraints();
        boolean leftUpdateOptimizationAllowed = existsNode.isLeftUpdateOptimizationAllowed();

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            FastIterator rightIt = existsNode.getRightIterator(rtm);

            RightTuple firstRightTuple = existsNode.getFirstRightTuple(leftTuple, rtm, null, rightIt);

            // If in memory, remove it, because we'll need to add it anyway if it's not blocked, to ensure iteration order
            RightTuple blocker = leftTuple.getBlocker();
            if (blocker == null) {
                if (leftTuple.getMemory() != null) { // memory can be null, if blocker was deleted in same do loop
                    ltm.remove(leftTuple);
                }
            } else {
                // check if we changed bucket
                if (rtm.isIndexed() && !rightIt.isFullIterator()) {
                    // if newRightTuple is null, we assume there was a bucket change and that bucket is empty
                    if (firstRightTuple == null || firstRightTuple.getMemory() != blocker.getMemory()) {
                        // we changed bucket, so blocker no longer blocks
                        blocker.removeBlocked(leftTuple);
                        blocker = null;
                    }
                }
            }

            constraints.updateFromTuple(contextEntry,
                                        wm,
                                        leftTuple);

            if ( !leftUpdateOptimizationAllowed && blocker != null ) {
                blocker.removeBlocked(leftTuple);
                blocker = null;
            }

            // if we where not blocked before (or changed buckets), or the previous blocker no longer blocks, then find the next blocker
            if (blocker == null || !constraints.isAllowedCachedLeft(contextEntry,
                                                                    blocker.getFactHandle())) {

                if (blocker != null) {
                    // remove previous blocker if it exists, as we know it doesn't block any more
                    blocker.removeBlocked(leftTuple);
                }

                // find first blocker, because it's a modify, we need to start from the beginning again
                for (RightTuple newBlocker = firstRightTuple; newBlocker != null; newBlocker = (RightTuple) rightIt.next(newBlocker)) {
                    if (constraints.isAllowedCachedLeft( contextEntry,
                                                         newBlocker.getFactHandle() )) {
                        leftTuple.setBlocker( newBlocker );
                        newBlocker.addBlocked( leftTuple );

                        break;
                    }
                }
            }

            if (leftTuple.getBlocker() == null) {
                // not blocked
                ltm.add(leftTuple); // add to memory so other fact handles can attempt to match

                if (leftTuple.getFirstChild() != null) {
                    // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( leftTuple.getFirstChild(), trgLeftTuples, stagedLeftTuples );
                }
                // with no previous children. do nothing.
            } else if (leftTuple.getFirstChild() == null) {
                // blocked, with no previous children, insert
                trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                             sink,
                                                             leftTuple.getBlocker().getPropagationContext(),
                                                             true));
            } else {
                // blocked, with previous children, modify
                LeftTuple childLeftTuple = leftTuple.getFirstChild();

                while (childLeftTuple != null) {
                    childLeftTuple.setPropagationContext(leftTuple.getBlocker().getPropagationContext());
                    updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);

                    childLeftTuple.reAddRight();
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
        constraints.resetTuple( contextEntry );
    }

    public void doRightUpdates(ExistsNode existsNode,
                               LeftTupleSink sink,
                               BetaMemory bm,
                               InternalWorkingMemory wm,
                               RightTupleSets srcRightTuples,
                               LeftTupleSets trgLeftTuples,
                               LeftTupleSets stagedLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        RightTupleMemory rtm = bm.getRightTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = existsNode.getRawConstraints();

        boolean iterateFromStart = existsNode.isIndexedUnificationJoin() || rtm.getIndexType().isComparison();

        for (RightTuple rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();

            if ( ltm != null && ltm.size() > 0 ) {
                FastIterator leftIt = existsNode.getLeftIterator( ltm );

                LeftTuple firstLeftTuple = existsNode.getFirstLeftTuple( rightTuple, ltm, leftIt );

                constraints.updateFromFactHandle( contextEntry,
                                                  wm,
                                                  rightTuple.getFactHandle() );


                // first process non-blocked tuples, as we know only those ones are in the left memory.
                for ( LeftTuple leftTuple = firstLeftTuple; leftTuple != null; ) {
                    // preserve next now, in case we remove this leftTuple
                    LeftTuple temp = (LeftTuple) leftIt.next( leftTuple );

                    if ( leftTuple.getStagedType() == LeftTuple.UPDATE ) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    // we know that only unblocked LeftTuples are  still in the memory
                    if ( constraints.isAllowedCachedRight( contextEntry,
                                                           leftTuple ) ) {
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        // this is now blocked so remove from memory
                        ltm.remove( leftTuple );

                        // subclasses like ForallNotNode might override this propagation
                        trgLeftTuples.addInsert( sink.createLeftTuple( leftTuple,
                                                                       sink,
                                                                       rightTuple.getPropagationContext(), true ) );
                    }

                    leftTuple = temp;
                }
            }

            LeftTuple firstBlocked = rightTuple.getTempBlocked();
            if ( firstBlocked != null ) {
                RightTuple rootBlocker = rightTuple.getTempNextRightTuple();
                if ( rootBlocker == null ) {
                    iterateFromStart = true;
                }

                FastIterator rightIt = existsNode.getRightIterator( rtm );

                // iterate all the existing previous blocked LeftTuples
                for ( LeftTuple leftTuple = firstBlocked; leftTuple != null; ) {
                    LeftTuple temp = leftTuple.getBlockedNext();

                    leftTuple.clearBlocker(); // must null these as we are re-adding them to the list

                    if ( leftTuple.getStagedType() == LeftTuple.UPDATE ) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        // but need to add it back into list first
                        leftTuple.setBlocker( rightTuple );
                        rightTuple.addBlocked( leftTuple );

                        leftTuple = temp;
                        continue;
                    }

                    constraints.updateFromTuple( contextEntry,
                                                 wm,
                                                 leftTuple );

                    if ( iterateFromStart ) {
                        rootBlocker = existsNode.getFirstRightTuple( leftTuple, rtm, null, rightIt );
                    }

                    // we know that older tuples have been checked so continue next
                    for ( RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) rightIt.next( newBlocker ) ) {
                        // cannot select a RightTuple queued in the delete list
                        // There may be UPDATE RightTuples too, but that's ok. They've already been re-added to the correct bucket, safe to be reprocessed.
                        if ( leftTuple.getStagedType() != LeftTuple.DELETE && newBlocker.getStagedType() != LeftTuple.DELETE &&
                             constraints.isAllowedCachedLeft( contextEntry, newBlocker.getFactHandle() ) ) {
                            leftTuple.setBlocker( newBlocker );
                            newBlocker.addBlocked( leftTuple );

                            break;
                        }
                    }

                    if ( leftTuple.getBlocker() == null ) {
                        // was previous blocked and not in memory, so add
                        if (ltm != null) {
                            ltm.add( leftTuple );
                        }

                        LeftTuple childLeftTuple = leftTuple.getFirstChild();
                        if ( childLeftTuple != null ) {
                            childLeftTuple.setPropagationContext( rightTuple.getPropagationContext() );
                            RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                        }
                    }

                    leftTuple = temp;
                }
            }
            rightTuple.clearStaged();
            rightTuple = next;
        }
        constraints.resetFactHandle(contextEntry);
    }

    public void doLeftDeletes(BetaMemory bm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples) {
        LeftTupleMemory ltm = bm.getLeftTupleMemory();

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            RightTuple blocker = leftTuple.getBlocker();
            if (blocker == null) {
                if (leftTuple.getMemory() != null) {
                    // it may have been staged and never actually added
                    ltm.remove(leftTuple);
                }
            } else {
                if (leftTuple.getFirstChild() != null) {
                    // no need to update pctx, as no right available, and pctx will exist on a parent LeftTuple anyway
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( leftTuple.getFirstChild(), trgLeftTuples, stagedLeftTuples );
                }
                blocker.removeBlocked(leftTuple);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doRightDeletes(ExistsNode existsNode,
                               BetaMemory bm,
                               InternalWorkingMemory wm,
                               RightTupleSets srcRightTuples,
                               LeftTupleSets trgLeftTuples,
                               LeftTupleSets stagedLeftTuples) {
        RightTupleMemory rtm = bm.getRightTupleMemory();
        LeftTupleMemory ltm = bm.getLeftTupleMemory();
        ContextEntry[] contextEntry = bm.getContext();
        BetaConstraints constraints = existsNode.getRawConstraints();

        for (RightTuple rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
            RightTuple next = rightTuple.getStagedNext();

            FastIterator it = existsNode.getRightIterator(rtm);

            boolean useComparisonIndex = rtm.getIndexType().isComparison();
            RightTuple rootBlocker = useComparisonIndex ? null : (RightTuple) it.next(rightTuple);

            if (rightTuple.getMemory() != null) {
                // it may have been staged and never actually added
                rtm.remove(rightTuple);
            }

            if (rightTuple.getBlocked() != null) {

                for (LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                    LeftTuple temp = leftTuple.getBlockedNext();

                    leftTuple.clearBlocker();

                    if (leftTuple.getStagedType() == LeftTuple.UPDATE) {
                        // ignore, as it will get processed via left iteration. Children cannot be processed twice
                        leftTuple = temp;
                        continue;
                    }

                    constraints.updateFromTuple(contextEntry,
                                                wm,
                                                leftTuple);

                    if (useComparisonIndex) {
                        rootBlocker = rtm.getFirst(leftTuple, null, it);
                    }

                    // we know that older tuples have been checked so continue previously
                    for (RightTuple newBlocker = rootBlocker; newBlocker != null; newBlocker = (RightTuple) it.next(newBlocker)) {
                        if (constraints.isAllowedCachedLeft(contextEntry,
                                                            newBlocker.getFactHandle())) {
                            leftTuple.setBlocker(newBlocker);
                            newBlocker.addBlocked(leftTuple);

                            break;
                        }
                    }

                    if (leftTuple.getBlocker() == null) {
                        // was previous blocked and not in memory, so add
                        ltm.add(leftTuple);

                        LeftTuple childLeftTuple = leftTuple.getFirstChild();
                        if (childLeftTuple != null) {
                            childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                            RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple( childLeftTuple, trgLeftTuples, stagedLeftTuples );
                        }
                    }

                    leftTuple = temp;
                }
            }
            rightTuple.nullBlocked();
            rightTuple.clearStaged();
            rightTuple = next;
        }
    }
}

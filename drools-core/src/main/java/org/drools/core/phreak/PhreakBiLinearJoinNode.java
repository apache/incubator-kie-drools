/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.phreak;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.common.BiLinearBetaConstraints;
import org.drools.core.reteoo.BiLinearJoinNode;
import org.drools.core.reteoo.BiLinearRuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.FastIterator;

import static org.drools.core.phreak.PhreakJoinNode.updateChildLeftTuple;

/**
 * Phreak processor for BiLinearJoinNode that handles joining two left input networks.
 * Unlike traditional joins that have one left and one right input, BiLinearJoinNode
 * has two left inputs that get joined together.
 */
public class PhreakBiLinearJoinNode {

    private final ReteEvaluator reteEvaluator;

    public PhreakBiLinearJoinNode(ReteEvaluator reteEvaluator) {
        this.reteEvaluator = reteEvaluator;
    }

    public void doNode(BiLinearJoinNode biLinearJoinNode,
                       LeftTupleSink sink,
                       BetaMemory bm,
                       TupleSets srcLeftTuples,
                       TupleSets stagedLeftTuples,
                       TupleSets trgLeftTuples) {

        // Get tuples from second left input (stored in "right" memory for BiLinear)
        TupleSets srcRightTuples = bm.getStagedRightTuples().takeAll();

        if (srcRightTuples.getDeleteFirst() != null) {
            doRightDeletes(bm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }
        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(bm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        // Process updates from both inputs (must happen before inserts)
        if (srcRightTuples.getUpdateFirst() != null) {
            doRightUpdates(bm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }
        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        }

        // Process inserts from both inputs
        if (srcRightTuples.getInsertFirst() != null) {
            doRightInserts(biLinearJoinNode, sink, bm, srcRightTuples, trgLeftTuples);
        }
        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(biLinearJoinNode, sink, bm, srcLeftTuples, trgLeftTuples);
        }

        srcRightTuples.resetAll();
        srcLeftTuples.resetAll();
    }

    /**
     * Process left tuple inserts for BiLinearJoinNode.
     * This method joins tuples from the primary left input with tuples from the second left input.
     */
    public void doLeftInserts(BiLinearJoinNode biLinearJoinNode,
                              LeftTupleSink sink,
                              BetaMemory<?> bm,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples) {

        TupleMemory ltm = bm.getLeftTupleMemory();   // Memory for first left input
        TupleMemory rtm = bm.getRightTupleMemory();  // Memory for second left input (treated as "right" for memory purposes)
        Object context = bm.getContext();

        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();

        if (biLinearConstraints == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearBetaConstraints but got null");
        }

        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            boolean useLeftMemory = PhreakNodeOperations.useLeftMemory(biLinearJoinNode, leftTuple);

            if (useLeftMemory) {
                ltm.add(leftTuple);
            }

            if (rtm != null && rtm.size() > 0) {
                FastIterator<TupleImpl> it = biLinearJoinNode.getSecondNetworkIterator(rtm);
                TupleImpl firstSecondTuple = biLinearJoinNode.getFirstSecondNetworkTuple(leftTuple, rtm, it);

                if (biLinearConstraints.isUnconstrainedJoin()) {
                    for (TupleImpl secondLeftTuple = firstSecondTuple;
                         secondLeftTuple != null;
                         secondLeftTuple = it != null ? it.next(secondLeftTuple) : null) {
                        insertBiLinearChildTuple(trgLeftTuples,
                                               leftTuple,
                                               secondLeftTuple,
                                               sink);
                    }
                } else {
                    biLinearConstraints.updateFromTuple(context, reteEvaluator, leftTuple);

                    for (TupleImpl secondLeftTuple = firstSecondTuple;
                         secondLeftTuple != null;
                         secondLeftTuple = it != null ? it.next(secondLeftTuple) : null) {

                        boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(context,
                                                                          secondLeftTuple.getFactHandle());

                        if (isAllowed) {
                            insertBiLinearChildTuple(trgLeftTuples,
                                                   leftTuple,
                                                   secondLeftTuple,
                                                   sink);
                        }
                    }
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }

        biLinearConstraints.resetTuple(context);
    }

    public void doLeftDeletes(BetaMemory<?> bm,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {

        TupleMemory ltm = bm.getLeftTupleMemory();

        for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            if (leftTuple.getMemory() != null) {
                ltm.remove(leftTuple);
            }

            if (leftTuple.getFirstChild() != null) {
                TupleImpl childLeftTuple = leftTuple.getFirstChild();
                while (childLeftTuple != null) {
                    TupleImpl nextChild = childLeftTuple.getHandleNext();
                    PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
                    childLeftTuple = nextChild;
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    /**
     * Process left tuple updates for BiLinearJoinNode.
     * Since BiLinear joins are unconstrained, no constraint re-evaluation is needed.
     * We simply propagate the update to all existing children.
     */
    public void doLeftUpdates(TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {

        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            // Propagate update to all children
            for (TupleImpl child = leftTuple.getFirstChild(); child != null;
                 child = child.getHandleNext()) {
                child.setPropagationContext(leftTuple.getPropagationContext());
                updateChildLeftTuple(child, stagedLeftTuples, trgLeftTuples);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    /**
     * Process second left input (right memory) inserts for BiLinearJoinNode.
     * This method joins tuples from the second left input with tuples from the primary left input.
     */
    public void doRightInserts(BiLinearJoinNode biLinearJoinNode,
                               LeftTupleSink sink,
                               BetaMemory<?> bm,
                               TupleSets srcRightTuples,
                               TupleSets trgLeftTuples) {

        TupleMemory ltm = bm.getLeftTupleMemory();   // Memory for first left input
        TupleMemory rtm = bm.getRightTupleMemory();  // Memory for second left input
        Object context = bm.getContext();

        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();

        if (biLinearConstraints == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearBetaConstraints but got null");
        }

        for (TupleImpl rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
            TupleImpl next = rightTuple.getStagedNext();

            boolean useRightMemory = biLinearJoinNode.isLeftTupleMemoryEnabled();

            if (useRightMemory) {
                rtm.add(rightTuple);
            }

            if (ltm != null && ltm.size() > 0) {
                FastIterator<TupleImpl> it = biLinearJoinNode.getLeftIterator(ltm);
                TupleImpl firstLeftTuple = biLinearJoinNode.getFirstLeftTuple(rightTuple, ltm, it);

                for (TupleImpl leftTuple = firstLeftTuple;
                     leftTuple != null;
                     leftTuple = it.next(leftTuple)) {

                    boolean useLeftMemory = PhreakNodeOperations.useLeftMemory(biLinearJoinNode, leftTuple);

                    biLinearConstraints.updateFromTuple(context, reteEvaluator, leftTuple);
                    boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(context,
                                                                      rightTuple.getFactHandle());

                    if (isAllowed) {
                        insertBiLinearChildTuple(trgLeftTuples,
                                               leftTuple,
                                               rightTuple,
                                               sink);
                    }
                }
            }

            rightTuple.clearStaged();
            rightTuple = next;
        }

        biLinearConstraints.resetTuple(context);
    }

    /**
     * Process second left input (right memory) deletes for BiLinearJoinNode.
     */
    public void doRightDeletes(BetaMemory<?> bm,
                               TupleSets srcRightTuples,
                               TupleSets trgLeftTuples,
                               TupleSets stagedLeftTuples) {

        TupleMemory ltm = bm.getLeftTupleMemory();
        TupleMemory rtm = bm.getRightTupleMemory();

        for (TupleImpl rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
            TupleImpl next = rightTuple.getStagedNext();

            if (rightTuple.getMemory() != null) {
                rtm.remove(rightTuple);
            }

            if (ltm != null && ltm.size() > 0) {
                FastIterator<TupleImpl> it = ltm.fastIterator();
                for (TupleImpl leftTuple = ltm.getFirst(null); leftTuple != null; leftTuple = it.next(leftTuple)) {
                    // Check each child of this left tuple
                    TupleImpl childTuple = leftTuple.getFirstChild();
                    while (childTuple != null) {
                        TupleImpl nextChild = childTuple.getHandleNext();
                        if (childTuple instanceof BiLinearRuleTerminalNodeLeftTuple) {
                            BiLinearRuleTerminalNodeLeftTuple biLinearChild = (BiLinearRuleTerminalNodeLeftTuple) childTuple;
                            if (biLinearChild.getSecondNetworkTuple() == rightTuple) {
                                childTuple.setPropagationContext(rightTuple.getPropagationContext());
                                PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childTuple);
                            }
                        }
                        childTuple = nextChild;
                    }
                }
            }

            rightTuple.clearStaged();
            rightTuple = next;
        }
    }

    /**
     * Process second left input (right memory) updates for BiLinearJoinNode.
     * Since BiLinear joins are unconstrained, no constraint re-evaluation is needed.
     * We find all children that reference the updated right tuple and propagate the update.
     */
    public void doRightUpdates(BetaMemory<?> bm,
                               TupleSets srcRightTuples,
                               TupleSets trgLeftTuples,
                               TupleSets stagedLeftTuples) {

        TupleMemory ltm = bm.getLeftTupleMemory();

        for (TupleImpl rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            TupleImpl next = rightTuple.getStagedNext();

            if (ltm != null && ltm.size() > 0) {
                FastIterator<TupleImpl> it = ltm.fastIterator();
                for (TupleImpl leftTuple = ltm.getFirst(null); leftTuple != null; leftTuple = it.next(leftTuple)) {
                    // Check each child of this left tuple
                    TupleImpl childTuple = leftTuple.getFirstChild();
                    while (childTuple != null) {
                        TupleImpl nextChild = childTuple.getHandleNext();
                        if (childTuple instanceof BiLinearRuleTerminalNodeLeftTuple) {
                            BiLinearRuleTerminalNodeLeftTuple biLinearChild = (BiLinearRuleTerminalNodeLeftTuple) childTuple;
                            if (biLinearChild.getSecondNetworkTuple() == rightTuple) {
                                childTuple.setPropagationContext(rightTuple.getPropagationContext());
                                updateChildLeftTuple(childTuple, stagedLeftTuples, trgLeftTuples);
                            }
                        }
                        childTuple = nextChild;
                    }
                }
            }

            rightTuple.clearStaged();
            rightTuple = next;
        }
    }

    /**
     * Creates a new child tuple that represents the join result of two left inputs.
     * This is the key difference from traditional joins - we're combining two left tuples.
     *
     * BiLinearJoinNode only supports terminal sinks (RuleTerminalNode, QueryTerminalNode).
     * The network builder ensures BiLinearJoinNode is only created when the sink is terminal.
     */
    private void insertBiLinearChildTuple(TupleSets trgLeftTuples,
                                        TupleImpl firstLeftTuple,
                                        TupleImpl secondLeftTuple,
                                        LeftTupleSink sink) {

        TupleImpl childTuple = new BiLinearRuleTerminalNodeLeftTuple(
            firstLeftTuple,
            secondLeftTuple,
            sink
        );

        childTuple.setPropagationContext(firstLeftTuple.getPropagationContext());

        trgLeftTuples.addInsert(childTuple);
    }
}

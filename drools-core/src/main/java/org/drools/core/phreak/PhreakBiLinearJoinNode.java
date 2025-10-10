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
import org.drools.core.reteoo.BiLinearContextEntry;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.BiLinearJoinNode;
import org.drools.core.reteoo.BiLinearRuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.BiLinearTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.FastIterator;

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

        // Process updates from both inputs
        if (srcRightTuples.getUpdateFirst() != null) {
            PhreakNodeOperations.doUpdatesReorderRightMemory(bm, srcRightTuples);
        }
        if (srcLeftTuples.getUpdateFirst() != null) {
            PhreakNodeOperations.doUpdatesReorderLeftMemory(bm, srcLeftTuples);
        }
        
        if (srcRightTuples.getUpdateFirst() != null) {
            doRightUpdates(biLinearJoinNode, sink, bm, srcRightTuples, trgLeftTuples, stagedLeftTuples);
        }
        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(biLinearJoinNode, sink, bm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
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
        Object contextEntry = bm.getContext();

        // Get BiLinear constraints wrapper (all BiLinearJoinNode constraints are wrapped)
        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();
        
        if (biLinearConstraints == null) {
            for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                TupleImpl next = leftTuple.getStagedNext();
                leftTuple.clearStaged();
                leftTuple = next;
            }
            return;
        }
        
        BiLinearContextEntry biLinearContext = contextEntry instanceof BiLinearContextEntry ?
            (BiLinearContextEntry) contextEntry : null;
        
        if (biLinearContext == null) {
            for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
                TupleImpl next = leftTuple.getStagedNext();
                leftTuple.clearStaged();
                leftTuple = next;
            }
            return;
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

                biLinearConstraints.updateFromTuple(biLinearContext, reteEvaluator, leftTuple);

                for (TupleImpl secondLeftTuple = firstSecondTuple; 
                     secondLeftTuple != null; 
                     secondLeftTuple = it != null ? it.next(secondLeftTuple) : null) {
                    
                    biLinearConstraints.updateFromBiLinearTuples(biLinearContext, reteEvaluator, 
                                                               leftTuple, secondLeftTuple);
                    boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(biLinearContext, 
                                                                      secondLeftTuple.getFactHandle());
                    
                    if (isAllowed) {
                        // Create a bi-linear join result combining both left inputs
                        insertBiLinearChildTuple(trgLeftTuples,
                                               leftTuple,
                                               secondLeftTuple,
                                               sink,
                                               useLeftMemory);
                    }
                }
            }
            
            leftTuple.clearStaged();
            leftTuple = next;
        }
        
        biLinearConstraints.resetTupleContext(biLinearContext);
    }

    public void doLeftUpdates(BiLinearJoinNode biLinearJoinNode,
                              LeftTupleSink sink,
                              BetaMemory<?> bm,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples) {
        
        TupleMemory rtm = bm.getRightTupleMemory();
        Object contextEntry = bm.getContext();

        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();
        
        if (biLinearConstraints == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearBetaConstraints but got null");
        }
        
        BiLinearContextEntry biLinearContext = contextEntry instanceof BiLinearContextEntry ?
            (BiLinearContextEntry) contextEntry : null;
        
        if (biLinearContext == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearContextEntry but got " + 
                                          (contextEntry != null ? contextEntry.getClass().getSimpleName() : "null"));
        }

        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            boolean useLeftMemory = PhreakNodeOperations.useLeftMemory(biLinearJoinNode, leftTuple);

            biLinearConstraints.updateFromTuple(biLinearContext, reteEvaluator, leftTuple);

            FastIterator<TupleImpl> it = biLinearJoinNode.getSecondNetworkIterator(rtm);
            
            TupleImpl childLeftTuple = leftTuple.getFirstChild();
            if (childLeftTuple != null) {
                PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
            }

            for (TupleImpl secondLeftTuple = biLinearJoinNode.getFirstSecondNetworkTuple(leftTuple, rtm, it); 
                 secondLeftTuple != null; 
                 secondLeftTuple = it.next(secondLeftTuple)) {
                
                biLinearConstraints.updateFromBiLinearTuples(biLinearContext, reteEvaluator,
                                                           leftTuple, secondLeftTuple);
                boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(biLinearContext,
                                                                  secondLeftTuple.getFactHandle());
                
                if (isAllowed) {
                    insertBiLinearChildTuple(trgLeftTuples,
                                           leftTuple,
                                           secondLeftTuple,
                                           sink,
                                           useLeftMemory);
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
        
        biLinearConstraints.resetTupleContext(biLinearContext);
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

            TupleImpl childLeftTuple = leftTuple.getFirstChild();
            if (childLeftTuple != null) {
                PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
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
        Object contextEntry = bm.getContext();

        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();
        
        if (biLinearConstraints == null) {
            for (TupleImpl rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                TupleImpl next = rightTuple.getStagedNext();
                rightTuple.clearStaged();
                rightTuple = next;
            }
            return;
        }
        
        BiLinearContextEntry biLinearContext = contextEntry instanceof BiLinearContextEntry ?
            (BiLinearContextEntry) contextEntry : null;
        
        if (biLinearContext == null) {
            for (TupleImpl rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                TupleImpl next = rightTuple.getStagedNext();
                rightTuple.clearStaged();
                rightTuple = next;
            }
            return;
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

                    biLinearConstraints.updateFromTuple(biLinearContext, reteEvaluator, leftTuple);
                    biLinearConstraints.updateFromBiLinearTuples(biLinearContext, reteEvaluator,
                                                               leftTuple, rightTuple);
                    boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(biLinearContext,
                                                                      rightTuple.getFactHandle());

                    if (isAllowed) {
                        insertBiLinearChildTuple(trgLeftTuples,
                                               leftTuple,
                                               rightTuple,
                                               sink,
                                               useLeftMemory);
                    }
                }
            }
            
            rightTuple.clearStaged();
            rightTuple = next;
        }
        
        biLinearConstraints.resetTupleContext(biLinearContext);
    }

    /**
     * Process second left input (right memory) updates for BiLinearJoinNode.
     */
    public void doRightUpdates(BiLinearJoinNode biLinearJoinNode,
                               LeftTupleSink sink,
                               BetaMemory<?> bm,
                               TupleSets srcRightTuples,
                               TupleSets trgLeftTuples,
                               TupleSets stagedLeftTuples) {
        
        TupleMemory ltm = bm.getLeftTupleMemory();
        Object contextEntry = bm.getContext();

        BiLinearBetaConstraints biLinearConstraints = biLinearJoinNode.getBiLinearConstraints();
        
        if (biLinearConstraints == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearBetaConstraints but got null");
        }
        
        BiLinearContextEntry biLinearContext = contextEntry instanceof BiLinearContextEntry ?
            (BiLinearContextEntry) contextEntry : null;
        
        if (biLinearContext == null) {
            throw new IllegalStateException("BiLinearJoinNode must have BiLinearContextEntry but got " + 
                                          (contextEntry != null ? contextEntry.getClass().getSimpleName() : "null"));
        }

        for (TupleImpl rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
            TupleImpl next = rightTuple.getStagedNext();

            FastIterator<TupleImpl> it = biLinearJoinNode.getLeftIterator(ltm);
            
            TupleImpl childLeftTuple = rightTuple.getFirstChild();
            if (childLeftTuple != null) {
                PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
            }

            for (TupleImpl leftTuple = biLinearJoinNode.getFirstLeftTuple(rightTuple, ltm, it);
                 leftTuple != null;
                 leftTuple = it.next(leftTuple)) {

                boolean useLeftMemory = PhreakNodeOperations.useLeftMemory(biLinearJoinNode, leftTuple);

                biLinearConstraints.updateFromTuple(biLinearContext, reteEvaluator, leftTuple);
                biLinearConstraints.updateFromBiLinearTuples(biLinearContext, reteEvaluator,
                                                           leftTuple, rightTuple);
                boolean isAllowed = biLinearConstraints.isAllowedCachedLeft(biLinearContext,
                                                                  rightTuple.getFactHandle());

                if (isAllowed) {
                    insertBiLinearChildTuple(trgLeftTuples,
                                           leftTuple,
                                           rightTuple,
                                           sink,
                                           useLeftMemory);
                }
            }

            rightTuple.clearStaged();
            rightTuple = next;
        }
        
        biLinearConstraints.resetTupleContext(biLinearContext);
    }

    /**
     * Process second left input (right memory) deletes for BiLinearJoinNode.
     */
    public void doRightDeletes(BetaMemory<?> bm,
                               TupleSets srcRightTuples,
                               TupleSets trgLeftTuples,
                               TupleSets stagedLeftTuples) {
        
        TupleMemory rtm = bm.getRightTupleMemory();

        for (TupleImpl rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
            TupleImpl next = rightTuple.getStagedNext();

            if (rightTuple.getMemory() != null) {
                rtm.remove(rightTuple);
            }

            TupleImpl childLeftTuple = rightTuple.getFirstChild();
            if (childLeftTuple != null) {
                PhreakNodeOperations.unlinkAndDeleteChildLeftTuple(trgLeftTuples, stagedLeftTuples, childLeftTuple);
            }

            rightTuple.clearStaged();
            rightTuple = next;
        }
    }

    /**
     * Creates a new child tuple that represents the join result of two left inputs.
     * This is the key difference from traditional joins - we're combining two left tuples.
     *
     * For terminal sinks (RuleTerminalNode, QueryTerminalNode), creates BiLinearRuleTerminalNodeLeftTuple
     * which is compatible with PhreakRuleTerminalNode while providing cross-network access.
     *
     * For other sinks, creates BiLinearTuple for proper cross-network variable resolution.
     */
    private void insertBiLinearChildTuple(TupleSets trgLeftTuples,
                                        TupleImpl firstLeftTuple,
                                        TupleImpl secondLeftTuple,
                                        LeftTupleSink sink,
                                        boolean leftTupleMemoryEnabled) {

        TupleImpl childTuple;
        int sinkType = sink.getType();

        if (sinkType == NodeTypeEnums.RuleTerminalNode || sinkType == NodeTypeEnums.QueryTerminalNode) {
            // For terminal sinks, use BiLinearRuleTerminalNodeLeftTuple
            // This extends RuleTerminalNodeLeftTuple (compatible with PhreakRuleTerminalNode)
            // while providing cross-network fact access
            childTuple = new BiLinearRuleTerminalNodeLeftTuple(
                firstLeftTuple,                  // First network tuple (e.g., A+B)
                secondLeftTuple,                 // Second network tuple (e.g., C+D)
                sink                             // Terminal sink
            );
        } else {
            // For non-terminal sinks, use BiLinearTuple
            childTuple = new BiLinearTuple(
                firstLeftTuple,                  // First network tuple
                secondLeftTuple,                 // Second network tuple
                null,                            // No right fact handle
                sink                             // Downstream sink
            );
        }

        childTuple.setPropagationContext(firstLeftTuple.getPropagationContext());

        trgLeftTuples.addInsert(childTuple);
    }
}
/**
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

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.SubnetworkTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.index.TupleList;

import static org.drools.core.phreak.PhreakJoinNode.updateChildLeftTuple;

public class PhreakSubnetworkNotExistsNode {
    public static void doSubNetworkNode(BetaNode node,
                                        LeftTupleSink sink,
                                        BetaMemory bm,
                                        TupleSets srcLeftTuples,
                                        TupleSets trgLeftTuples,
                                        TupleSets stagedLeftTuples) {

        TupleSets srcRightTuples = bm.getStagedRightTuples().takeAll();

        TupleMemory ltm                = bm.getLeftTupleMemory();
        boolean     tupleMemoryEnabled = node.isLeftTupleMemoryEnabled();

        deleteLeft(srcLeftTuples, trgLeftTuples, stagedLeftTuples, ltm);

        insertRight(node, sink, trgLeftTuples, stagedLeftTuples, srcRightTuples, tupleMemoryEnabled);

        insertLeft(node, sink, srcLeftTuples, trgLeftTuples, ltm, tupleMemoryEnabled);

        updateRight(srcRightTuples);

        deleteRight(node, sink, trgLeftTuples, stagedLeftTuples, srcRightTuples);

        updateLeft(srcLeftTuples, trgLeftTuples, stagedLeftTuples, ltm);

        srcRightTuples.resetAll();
        srcLeftTuples.resetAll();
    }

    private static void updateLeft(TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleSets stagedLeftTuples, TupleMemory ltm) {
        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            TupleImpl childLeftTuple = leftTuple.getFirstChild();

            if (!leftTuple.isExpired()  &&
                childLeftTuple != null && // Not/Exists nodes only have one child
                childLeftTuple.getStagedType() == Tuple.NONE) {  // only apply if not already staged
                childLeftTuple.setPropagationContext(leftTuple.getPropagationContext());
                // By adding the child now, it avoid iterating again to find all leftTuples that have no matches
                updateChildLeftTuple(childLeftTuple, stagedLeftTuples, trgLeftTuples);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    private static void deleteRight(BetaNode node, LeftTupleSink sink, TupleSets trgLeftTuples, TupleSets stagedLeftTuples, TupleSets srcRightTuples) {
        if (srcRightTuples.getDeleteFirst() != null) {
            // must come last, to avoid staging something for propagation that is then unstaged
            for (TupleImpl rightTuple = srcRightTuples.getDeleteFirst(); rightTuple != null; ) {
                TupleImpl next = rightTuple.getStagedNext();

                TupleImpl leftTuple = node.getStartTuple((SubnetworkTuple)rightTuple);
                // don't use matches here, as it may be null, if the LT was also being removed.
                rightTuple.getMemory().remove(rightTuple);

                TupleList matches = (TupleList) leftTuple.getContextObject();
                if (matches != null && matches.isEmpty()) { // matches is null, if LT was deleted too
                    // Not/Exists nodes only have one child
                    if (node.getType() == NodeTypeEnums.ExistsNode) {
                        TupleImpl childLeftTuple = leftTuple.getFirstChild();
                        childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                        RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                    } else if (!leftTuple.isExpired()) { // else !exists
                        trgLeftTuples.addInsert(TupleFactory.createLeftTuple(leftTuple, sink, leftTuple.getPropagationContext(), true));
                    }
                }
                rightTuple.clearStaged();
                rightTuple = next;
            }
        }
    }

    private static void updateRight(TupleSets srcRightTuples) {
        if (srcRightTuples.getUpdateFirst() != null) {
            // Does nothing. It was here before, here now, so over all state does not change.
            for (TupleImpl rightTuple = srcRightTuples.getUpdateFirst(); rightTuple != null; ) {
                TupleImpl next = rightTuple.getStagedNext();
                rightTuple.clearStaged();
                rightTuple = next;
            }
        }
    }

    private static void insertLeft(BetaNode node, LeftTupleSink sink, TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleMemory ltm, boolean tupleMemoryEnabled) {
        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            boolean useTupleMemory = tupleMemoryEnabled || RuleNetworkEvaluator.useLeftMemory(node, leftTuple);
            // Do not need to init tupleList for matches, as this is done on right inserts.
            if (useTupleMemory) {
                ltm.add(leftTuple);
            }

            if (node.getType() == NodeTypeEnums.NotNode) {
                // It's a not node, to check if there was no matches, and if so create and propagate the child lt
                TupleList matches = (TupleList) leftTuple.getContextObject();
                if (matches == null && !leftTuple.isExpired()) {
                    // By adding the child now, it avoid iterating again to find all leftTuples that have no matches
                    trgLeftTuples.addInsert(TupleFactory.createLeftTuple(leftTuple, sink, leftTuple.getPropagationContext(), useTupleMemory));
                }
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    private static void insertRight(BetaNode node, LeftTupleSink sink, TupleSets trgLeftTuples, TupleSets stagedLeftTuples, TupleSets srcRightTuples, boolean tupleMemoryEnabled) {
        if (srcRightTuples.getInsertFirst() != null) {
            // this must come before left insert, so 'not' knows if there are matches or not before creating the child lt
            for (TupleImpl rightTuple = srcRightTuples.getInsertFirst(); rightTuple != null; ) {
                TupleImpl next = rightTuple.getStagedNext();

                TupleImpl leftTuple = node.getStartTuple(rightTuple);
                TupleList matches = (TupleList) leftTuple.getContextObject();
                if (matches == null) { // even if there is no tuple memory, we still need to know if there are matches or not, in later code
                    matches = new TupleList();
                    leftTuple.setContextObject(matches);
                }
                matches.add(rightTuple);

                if (matches.size() == 1) {
                    // first match was added, so create or delete child
                    // Not/Exists nodes only have one child
                    if (node.getType() == NodeTypeEnums.ExistsNode) {
                        if (!leftTuple.isExpired()) {
                            boolean useTupleMemory = tupleMemoryEnabled || RuleNetworkEvaluator.useLeftMemory(node, rightTuple);
                            trgLeftTuples.addInsert(TupleFactory.createLeftTuple(leftTuple, sink, leftTuple.getPropagationContext(), useTupleMemory));
                        }
                    } else { // else !exists
                        TupleImpl childLeftTuple = leftTuple.getFirstChild();
                        if (childLeftTuple != null) { // this can be null if the LT is not yet added
                            childLeftTuple.setPropagationContext(rightTuple.getPropagationContext());
                            RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                        }
                    }
                }

                rightTuple.clearStaged();
                rightTuple = next;
            }
        }
    }

    private static void deleteLeft(TupleSets srcLeftTuples, TupleSets trgLeftTuples, TupleSets stagedLeftTuples, TupleMemory ltm) {
        if (srcLeftTuples.getDeleteFirst() != null) {
            for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
                TupleImpl next = leftTuple.getStagedNext();
                if (leftTuple.getMemory() != null) {
                    ltm.remove(leftTuple);
                }
                TupleImpl childLeftTuple = leftTuple.getFirstChild();
                if (childLeftTuple != null) {
                    childLeftTuple.setPropagationContext(leftTuple.getPropagationContext());
                    RuleNetworkEvaluator.unlinkAndDeleteChildLeftTuple(childLeftTuple, trgLeftTuples, stagedLeftTuples);
                }

                leftTuple.setContextObject(null); // this is now right delete knows the LT is also being removed
                leftTuple.clearStaged();
                leftTuple = next;
            }
        }
    }
}

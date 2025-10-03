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

import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TupleFactory;
import org.drools.core.reteoo.TupleImpl;

import static org.drools.core.phreak.RuleNetworkEvaluator.normalizeStagedTuples;


public class PhreakBranchNode {
    public void doNode(ConditionalBranchNode branchNode,
                       ConditionalBranchMemory cbm,
                       LeftTupleSink sink,
                       ActivationsManager activationsManager,
                       TupleSets srcLeftTuples,
                       TupleSets trgLeftTuples,
                       TupleSets stagedLeftTuples,
                       RuleExecutor executor) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(sink, activationsManager, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(branchNode, cbm, sink, activationsManager, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(branchNode, cbm, sink, activationsManager, srcLeftTuples, trgLeftTuples, executor);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(ConditionalBranchNode branchNode,
                              ConditionalBranchMemory cbm,
                              LeftTupleSink sink,
                              ActivationsManager activationsManager,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              RuleExecutor executor) {
        ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();

        for (TupleImpl leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            boolean breaking = false;
            ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, activationsManager.getReteEvaluator(), cbm.context);

            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(branchNode, leftTuple);

            if (conditionalExecution != null) {
                RuleTerminalNode rtn = (RuleTerminalNode) conditionalExecution.getSink().getFirstLeftTupleSink();
                RuleTerminalNodeLeftTuple branchedLeftTuple = (RuleTerminalNodeLeftTuple) TupleFactory.createLeftTuple(leftTuple,
                                                                           rtn,
                                                                           leftTuple.getPropagationContext(), useLeftMemory);
                PhreakRuleTerminalNode.doLeftTupleInsert( rtn, executor, activationsManager,
                                                          executor.getRuleAgendaItem(), branchedLeftTuple) ;
                breaking = conditionalExecution.isBreaking();
            }

            if (!breaking) {
                trgLeftTuples.addInsert(TupleFactory.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     leftTuple.getPropagationContext(), useLeftMemory));
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(ConditionalBranchNode branchNode,
                              ConditionalBranchMemory cbm,
                              LeftTupleSink sink,
                              ActivationsManager activationsManager,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples,
                              RuleExecutor executor) {
        ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();

        for (TupleImpl leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();


            BranchTuples branchTuples = getBranchTuples(sink, leftTuple);

            RuleTerminalNode oldRtn = null;
            if (branchTuples.rtnLeftTuple != null) {
                oldRtn = (RuleTerminalNode) branchTuples.rtnLeftTuple.getSink();
            }

            ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, activationsManager.getReteEvaluator(), cbm.context);

            RuleTerminalNode newRtn = null;
            boolean breaking = false;
            if (conditionalExecution != null) {
                newRtn = (RuleTerminalNode) conditionalExecution.getSink().getFirstLeftTupleSink();
                breaking = conditionalExecution.isBreaking();
            }

            // Handle conditional branches
            if (oldRtn != null) {
                if (newRtn == null) {
                    // old exits, new does not, so delete
                    if ( branchTuples.rtnLeftTuple.getMemory() != null ) {
                        executor.removeActiveTuple(branchTuples.rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(activationsManager, executor, branchTuples.rtnLeftTuple);

                } else if (newRtn == oldRtn) {
                    // old and new on same branch, so update
                    PhreakRuleTerminalNode.doLeftTupleUpdate(newRtn, executor, activationsManager, branchTuples.rtnLeftTuple) ;

                } else {
                    // old and new on different branches, delete one and insert the other
                    if ( branchTuples.rtnLeftTuple.getMemory() != null ) {
                        executor.removeActiveTuple(branchTuples.rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(activationsManager, executor, branchTuples.rtnLeftTuple);

                    branchTuples.rtnLeftTuple = (RuleTerminalNodeLeftTuple) TupleFactory.createLeftTuple(leftTuple,
                                                                             newRtn,
                                                                             leftTuple.getPropagationContext(), true);
                    PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, activationsManager,
                                                              executor.getRuleAgendaItem(), branchTuples.rtnLeftTuple) ;
                }

            } else if (newRtn != null) {
                // old does not exist, new exists, so insert
                branchTuples.rtnLeftTuple = (RuleTerminalNodeLeftTuple) TupleFactory.createLeftTuple(leftTuple, newRtn,
                                                                         leftTuple.getPropagationContext(), true);
                PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, activationsManager,
                                                          executor.getRuleAgendaItem(), branchTuples.rtnLeftTuple) ;
            }

            // Handle main branch
            if (branchTuples.mainLeftTuple != null) {
                normalizeStagedTuples( stagedLeftTuples, branchTuples.mainLeftTuple );

                if (!breaking) {
                    // default consequence will also be executed
                    trgLeftTuples.addUpdate(branchTuples.mainLeftTuple);
                }
            } else if (!breaking) {
                // child didn't exist, new one does, so insert
                trgLeftTuples.addInsert(TupleFactory.createLeftTuple(leftTuple,
                                                                     sink,
                                                                     leftTuple.getPropagationContext(), true));
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(LeftTupleSink sink,
                              ActivationsManager activationsManager,
                              TupleSets srcLeftTuples,
                              TupleSets trgLeftTuples,
                              TupleSets stagedLeftTuples,
                              RuleExecutor executor) {
        for (TupleImpl leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            TupleImpl next = leftTuple.getStagedNext();

            BranchTuples branchTuples = getBranchTuples(sink, leftTuple);

            if (branchTuples.rtnLeftTuple != null) {
                if ( branchTuples.rtnLeftTuple.getMemory() != null ) {
                    executor.removeActiveTuple(branchTuples.rtnLeftTuple);
                }
                PhreakRuleTerminalNode.doLeftDelete(activationsManager, executor, branchTuples.rtnLeftTuple);
            }

            if (branchTuples.mainLeftTuple != null) {
                RuleNetworkEvaluator.deleteChildLeftTuple(branchTuples.mainLeftTuple, trgLeftTuples, stagedLeftTuples);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    /**
     * A branch has two potential sinks. rtnSink  is for the sink if the contained logic returns true.
     * mainSink is for propagations after the branch node, if they are allowed.
     * it may have one or the other or both. there is no state that indicates whether one or the other or both
     * are present, so all tuple children must be inspected and references coalesced from that.
     * when handling updates and deletes it must search the child tuples to colasce the references.
     * This is done by checking the tuple sink with the known main or rtn sink.
     */
    private BranchTuples getBranchTuples(LeftTupleSink sink, TupleImpl leftTuple) {
        BranchTuples branchTuples = new BranchTuples();
        TupleImpl child = leftTuple.getFirstChild();
        if ( child != null ) {
            // assigns the correct main or rtn LeftTuple based on the identified sink
            if (child.getSink() == sink ) {
                branchTuples.mainLeftTuple = child;
            } else {
                branchTuples.rtnLeftTuple = (RuleTerminalNodeLeftTuple) child;
            }
            child = child.getHandleNext();
            if ( child != null ) {
                if (child.getSink() == sink ) {
                    branchTuples.mainLeftTuple = child;
                } else {
                    branchTuples.rtnLeftTuple = (RuleTerminalNodeLeftTuple) child;
                }
            }
        }
        return branchTuples;
    }

    private static class BranchTuples {
        RuleTerminalNodeLeftTuple rtnLeftTuple;
        TupleImpl mainLeftTuple;
    }
}

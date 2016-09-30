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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.spi.Salience;

import static org.drools.core.phreak.RuleNetworkEvaluator.normalizeStagedTuples;


public class PhreakBranchNode {
    public void doNode(ConditionalBranchNode branchNode,
                       ConditionalBranchMemory cbm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples,
                       RuleExecutor executor) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(branchNode, cbm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(branchNode, cbm, sink, wm, srcLeftTuples, trgLeftTuples, executor);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(ConditionalBranchNode branchNode,
                              ConditionalBranchMemory cbm,
                              LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              RuleExecutor executor) {
        ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();

        RuleAgendaItem ruleAgendaItem = executor.getRuleAgendaItem();
        int salienceInt = 0;
        Salience salience = ruleAgendaItem.getRule().getSalience();
        if ( !salience.isDynamic() ) {
            salienceInt = ruleAgendaItem.getRule().getSalience().getValue();
            salience = null;
        }

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            boolean breaking = false;
            ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, wm, cbm.context);

            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(branchNode, leftTuple);

            if (conditionalExecution != null) {
                RuleTerminalNode rtn = (RuleTerminalNode) conditionalExecution.getSink().getFirstLeftTupleSink();
                LeftTuple branchedLeftTuple = rtn.createLeftTuple(leftTuple,
                                                                  rtn,
                                                                  leftTuple.getPropagationContext(), useLeftMemory);
                PhreakRuleTerminalNode.doLeftTupleInsert( rtn, executor, wm.getAgenda(),
                                                          executor.getRuleAgendaItem(), salienceInt, salience, branchedLeftTuple, wm) ;
                breaking = conditionalExecution.isBreaking();
            }

            if (!breaking) {
                trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
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
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples,
                              RuleExecutor executor) {
        ConditionalBranchEvaluator branchEvaluator = branchNode.getBranchEvaluator();
        RuleAgendaItem ruleAgendaItem = executor.getRuleAgendaItem();
        int salienceInt = 0;
        Salience salience = ruleAgendaItem.getRule().getSalience();
        if ( !salience.isDynamic() ) {
            salienceInt = ruleAgendaItem.getRule().getSalience().getValue();
            salience = null;
        }

        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();


            BranchTuples branchTuples = getBranchTuples(sink, leftTuple);

            RuleTerminalNode oldRtn = null;
            if (branchTuples.rtnLeftTuple != null) {
                oldRtn = branchTuples.rtnLeftTuple.getTupleSink();
            }

            ConditionalExecution conditionalExecution = branchEvaluator.evaluate(leftTuple, wm, cbm.context);

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
                        executor.removeLeftTuple(branchTuples.rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(wm, executor, branchTuples.rtnLeftTuple);

                } else if (newRtn == oldRtn) {
                    // old and new on same branch, so update
                    PhreakRuleTerminalNode.doLeftTupleUpdate(newRtn, executor, wm.getAgenda(), salienceInt, salience, branchTuples.rtnLeftTuple, wm) ;

                } else {
                    // old and new on different branches, delete one and insert the other
                    if ( branchTuples.rtnLeftTuple.getMemory() != null ) {
                        executor.removeLeftTuple(branchTuples.rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(wm, executor, branchTuples.rtnLeftTuple);

                    branchTuples.rtnLeftTuple = newRtn.createLeftTuple(leftTuple,
                                                                       newRtn,
                                                                       leftTuple.getPropagationContext(), true);
                    PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, wm.getAgenda(),
                                                              executor.getRuleAgendaItem(), salienceInt, salience, branchTuples.rtnLeftTuple, wm) ;
                }

            } else if (newRtn != null) {
                // old does not exist, new exists, so insert
                branchTuples.rtnLeftTuple = newRtn.createLeftTuple(leftTuple, newRtn,
                                                                   leftTuple.getPropagationContext(), true);
                PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, wm.getAgenda(),
                                                          executor.getRuleAgendaItem(), salienceInt, salience, branchTuples.rtnLeftTuple, wm) ;
            }

            // Handle main branch
            if (branchTuples.mainLeftTuple != null) {
                normalizeStagedTuples( stagedLeftTuples, branchTuples.mainLeftTuple );

                if (!breaking) {
                    // child exist, new one does, so update
                    trgLeftTuples.addUpdate(branchTuples.mainLeftTuple);
                } else {
                    // child exist, new one does not, so delete
                    trgLeftTuples.addDelete(branchTuples.mainLeftTuple);
                }
            } else if (!breaking) {
                // child didn't exist, new one does, so insert
                trgLeftTuples.addInsert(sink.createLeftTuple(leftTuple,
                                                             sink,
                                                             leftTuple.getPropagationContext(), true));
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(LeftTupleSink sink,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples,
                              TupleSets<LeftTuple> trgLeftTuples,
                              TupleSets<LeftTuple> stagedLeftTuples,
                              RuleExecutor executor) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            BranchTuples branchTuples = getBranchTuples(sink, leftTuple);

            if (branchTuples.rtnLeftTuple != null) {
                if ( branchTuples.rtnLeftTuple.getMemory() != null ) {
                    executor.removeLeftTuple(branchTuples.rtnLeftTuple);
                }
                PhreakRuleTerminalNode.doLeftDelete(wm, executor, branchTuples.rtnLeftTuple);
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
    private BranchTuples getBranchTuples(LeftTupleSink sink, LeftTuple leftTuple) {
        BranchTuples branchTuples = new BranchTuples();
        LeftTuple child = leftTuple.getFirstChild();
        if ( child != null ) {
            // assigns the correct main or rtn LeftTuple based on the identified sink
            if ( child.getTupleSink() == sink ) {
                branchTuples.mainLeftTuple = child;
            } else {
                branchTuples.rtnLeftTuple = child;
            }
            child = child.getHandleNext();
            if ( child != null ) {
                if ( child.getTupleSink() == sink ) {
                    branchTuples.mainLeftTuple = child;
                } else {
                    branchTuples.rtnLeftTuple = child;
                }
            }
        }
        return branchTuples;
    }

    private static class BranchTuples {
        LeftTuple rtnLeftTuple;
        LeftTuple mainLeftTuple;
    }
}

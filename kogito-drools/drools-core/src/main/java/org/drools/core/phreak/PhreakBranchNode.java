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

import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.ConditionalBranchEvaluator;
import org.drools.core.reteoo.ConditionalBranchEvaluator.ConditionalExecution;
import org.drools.core.reteoo.ConditionalBranchNode;
import org.drools.core.reteoo.ConditionalBranchNode.ConditionalBranchMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.spi.Salience;


public class PhreakBranchNode {
    public void doNode(ConditionalBranchNode branchNode,
                       ConditionalBranchMemory cbm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       LeftTupleSets trgLeftTuples,
                       LeftTupleSets stagedLeftTuples,
                       RuleExecutor executor) {

        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(branchNode, cbm, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples, executor);
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
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
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
                PhreakRuleTerminalNode.doLeftTupleInsert( rtn, executor, (InternalAgenda) wm.getAgenda(),
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
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples,
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


            LeftTuple rtnLeftTuple = null;
            LeftTuple mainLeftTuple = null;
            LeftTuple child = leftTuple.getFirstChild();
            if ( child != null ) {
                // assigns the correct main or rtn LeftTuple based on the identified sink
                if ( child.getSink() == sink ) {
                    mainLeftTuple = child;
                } else {
                    rtnLeftTuple = child;
                }
                child = child.getLeftParentNext();
                if ( child != null ) {
                    if ( child.getSink() == sink ) {
                        mainLeftTuple = child;
                    } else {
                        rtnLeftTuple = child;
                    }
                }
            }

            RuleTerminalNode oldRtn = null;
            if (rtnLeftTuple != null) {
                oldRtn = (RuleTerminalNode) rtnLeftTuple.getSink();
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
                    if ( rtnLeftTuple.getMemory() != null ) {
                        executor.removeLeftTuple(rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(wm, executor, rtnLeftTuple);

                } else if (newRtn == oldRtn) {
                    // old and new on same branch, so update
                    PhreakRuleTerminalNode.doLeftTupleUpdate(newRtn, executor, (InternalAgenda) wm.getAgenda(), salienceInt, salience, rtnLeftTuple, wm) ;

                } else {
                    // old and new on different branches, delete one and insert the other
                    if ( rtnLeftTuple.getMemory() != null ) {
                        executor.removeLeftTuple(rtnLeftTuple);
                    }
                    PhreakRuleTerminalNode.doLeftDelete(wm, executor, rtnLeftTuple);

                    rtnLeftTuple = newRtn.createLeftTuple(leftTuple,
                                                          newRtn,
                                                          leftTuple.getPropagationContext(), true);
                    PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, (InternalAgenda) wm.getAgenda(),
                                                              executor.getRuleAgendaItem(), salienceInt, salience, rtnLeftTuple, wm) ;
                }

            } else if (newRtn != null) {
                // old does not exist, new exists, so insert
                rtnLeftTuple = newRtn.createLeftTuple(leftTuple, newRtn,
                                                                     leftTuple.getPropagationContext(), true);
                PhreakRuleTerminalNode.doLeftTupleInsert( newRtn, executor, (InternalAgenda) wm.getAgenda(),
                                                          executor.getRuleAgendaItem(), salienceInt, salience, rtnLeftTuple, wm) ;
            }

            // Handle main branch
            if (mainLeftTuple != null) {
                switch (mainLeftTuple.getStagedType()) {
                    // handle clash with already staged entries
                    case LeftTuple.INSERT:
                        stagedLeftTuples.removeInsert(mainLeftTuple);
                        break;
                    case LeftTuple.UPDATE:
                        stagedLeftTuples.removeUpdate(mainLeftTuple);
                        break;
                }

                if (!breaking) {
                    // child exist, new one does, so update
                    trgLeftTuples.addUpdate(mainLeftTuple);
                } else {
                    // child exist, new one does not, so delete
                    trgLeftTuples.addDelete(mainLeftTuple);
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

    public void doLeftDeletes(ConditionalBranchNode branchNode,
                              ConditionalBranchMemory cbm,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              LeftTupleSets trgLeftTuples,
                              LeftTupleSets stagedLeftTuples,
                              RuleExecutor executor) {
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            LeftTuple rtnLeftTuple = (LeftTuple) leftTuple.getObject();
            LeftTuple mainLeftTuple = leftTuple.getFirstChild();

            if (rtnLeftTuple != null) {
                if ( rtnLeftTuple.getMemory() != null ) {
                    executor.removeLeftTuple(rtnLeftTuple);
                }
                PhreakRuleTerminalNode.doLeftDelete(wm, executor, rtnLeftTuple);
            }

            if (mainLeftTuple != null) {
                switch (mainLeftTuple.getStagedType()) {
                    // handle clash with already staged entries
                    case LeftTuple.INSERT:
                        stagedLeftTuples.removeInsert(mainLeftTuple);
                        break;
                    case LeftTuple.UPDATE:
                        stagedLeftTuples.removeUpdate(mainLeftTuple);
                        break;
                }
                trgLeftTuples.addDelete(mainLeftTuple);
            }

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }
}

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

import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Salience;
import org.drools.core.spi.Tuple;
import org.kie.api.event.rule.MatchCancelledCause;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:42
* To change this template use File | Settings | File Templates.
*/
public class PhreakRuleTerminalNode {
    public void doNode(TerminalNode rtnNode,
                       InternalAgenda agenda,
                       TupleSets<LeftTuple> srcLeftTuples,
                       RuleExecutor executor) {
        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(agenda, srcLeftTuples, executor);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(rtnNode, agenda, srcLeftTuples, executor);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(rtnNode, agenda, srcLeftTuples, executor);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(TerminalNode rtnNode,
                              InternalAgenda agenda,
                              TupleSets<LeftTuple> srcLeftTuples,
                              RuleExecutor executor) {
        RuleAgendaItem ruleAgendaItem = executor.getRuleAgendaItem();

        int salienceInt = 0;
        Salience salience = ruleAgendaItem.getRule().getSalience();
        if ( !salience.isDynamic() ) {
            salienceInt = salience.getValue();
            salience = null;
        }

        if ( rtnNode.getRule().getAutoFocus() && !ruleAgendaItem.getAgendaGroup().isActive() ) {
            agenda.setFocus( ruleAgendaItem.getAgendaGroup() );
        }

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            doLeftTupleInsert(rtnNode, executor, agenda, ruleAgendaItem, salienceInt, salience, leftTuple);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public static void doLeftTupleInsert(TerminalNode rtnNode, RuleExecutor executor,
                                         InternalAgenda agenda, RuleAgendaItem ruleAgendaItem, int salienceInt,
                                         Salience salience, LeftTuple leftTuple) {
        PropagationContext pctx = leftTuple.getPropagationContext();
        pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple, pctx);

        if ( rtnNode.getRule().isNoLoop() && rtnNode.equals(pctx.getTerminalNodeOrigin()) ) {
            return;
        }

        InternalWorkingMemory wm = agenda.getWorkingMemory();
        if ( salience != null ) {
            salienceInt = salience.getValue(new DefaultKnowledgeHelper((AgendaItem) leftTuple, wm),
                                            rtnNode.getRule(), wm);
        }

        RuleTerminalNodeLeftTuple rtnLeftTuple = (RuleTerminalNodeLeftTuple) leftTuple;
        agenda.createAgendaItem( rtnLeftTuple, salienceInt, pctx, ruleAgendaItem, ruleAgendaItem.getAgendaGroup() );

        EventSupport es = (EventSupport) wm;
        es.getAgendaEventSupport().fireActivationCreated(rtnLeftTuple, wm);

        if (  rtnNode.getRule().isLockOnActive() &&
              leftTuple.getPropagationContext().getType() != PropagationContext.Type.RULE_ADDITION ) {
            long handleRecency = pctx.getFactHandle().getRecency();
            InternalAgendaGroup agendaGroup = executor.getRuleAgendaItem().getAgendaGroup();
            if (blockedByLockOnActive(rtnNode.getRule(), pctx, handleRecency, agendaGroup)) {
                es.getAgendaEventSupport().fireActivationCancelled(rtnLeftTuple, wm, MatchCancelledCause.FILTER );
                return;
            }
        }

        if (agenda.getActivationsFilter() != null && !agenda.getActivationsFilter().accept( rtnLeftTuple, wm, rtnNode)) {
            // only relevant for seralization, to not refire Matches already fired
            return;
        }

        agenda.addItemToActivationGroup( rtnLeftTuple );

        executor.addLeftTuple(leftTuple);
        leftTuple.increaseActivationCountForEvents(); // increased here, decreased in Agenda's cancelActivation and fireActivation
        if( !rtnNode.isFireDirect() && executor.isDeclarativeAgendaEnabled() ) {
            agenda.insertAndStageActivation(rtnLeftTuple);
        }
    }

    public void doLeftUpdates(TerminalNode rtnNode,
                              InternalAgenda agenda,
                              TupleSets<LeftTuple> srcLeftTuples,
                              RuleExecutor executor) {
        RuleAgendaItem ruleAgendaItem = executor.getRuleAgendaItem();
        if ( rtnNode.getRule().getAutoFocus() && !ruleAgendaItem.getAgendaGroup().isActive() ) {
            agenda.setFocus(ruleAgendaItem.getAgendaGroup());
        }

        int salienceInt = 0;
        Salience salience = ruleAgendaItem.getRule().getSalience();
        if ( !salience.isDynamic() ) {
            salienceInt = salience.getValue();
            salience = null;
        }

        //Salience salienceInt = ruleAgendaItem.getRule().getSalience();
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            doLeftTupleUpdate(rtnNode, executor, agenda, salienceInt, salience, leftTuple);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public static void doLeftTupleUpdate(TerminalNode rtnNode, RuleExecutor executor,
                                         InternalAgenda agenda, int salienceInt, Salience salience,
                                         LeftTuple leftTuple) {
        PropagationContext pctx = leftTuple.getPropagationContext();
        pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                 pctx);

        boolean blocked = false;
        RuleTerminalNodeLeftTuple rtnLeftTuple = (RuleTerminalNodeLeftTuple) leftTuple;
        if( executor.isDeclarativeAgendaEnabled() ) {
           if ( rtnLeftTuple.getBlockers() != null && !rtnLeftTuple.getBlockers().isEmpty() ) {
               blocked = true; // declarativeAgenda still blocking LeftTuple, so don't add back ot list
           }
        } else {
            blocked = rtnNode.getRule().isNoLoop() && rtnNode.equals(pctx.getTerminalNodeOrigin());
        }

        InternalWorkingMemory wm = agenda.getWorkingMemory();
        if ( salience != null ) {
            salienceInt = salience.getValue( new DefaultKnowledgeHelper(rtnLeftTuple, wm),
                                             rtnNode.getRule(), wm);
        }
        
        if (agenda.getActivationsFilter() != null && !agenda.getActivationsFilter().accept( rtnLeftTuple, wm, rtnNode)) {
            // only relevant for serialization, to not re-fire Matches already fired
            return;
        }
        
        if ( !blocked ) {
            boolean addToExector = true;
            if (  rtnNode.getRule().isLockOnActive() &&
                  pctx.getType() != PropagationContext.Type.RULE_ADDITION ) {

                long handleRecency = pctx.getFactHandle().getRecency();
                InternalAgendaGroup agendaGroup = executor.getRuleAgendaItem().getAgendaGroup();
                if (blockedByLockOnActive(rtnNode.getRule(), pctx, handleRecency, agendaGroup)) {
                    addToExector = false;
                }
            }
            if ( addToExector ) {
                if (!rtnLeftTuple.isQueued() ) {
                    // not queued, so already fired, so it's effectively recreated
                    EventSupport es = (EventSupport) wm;
                    es.getAgendaEventSupport().fireActivationCreated(rtnLeftTuple, wm);

                    rtnLeftTuple.update(salienceInt, pctx);
                    executor.addLeftTuple(leftTuple);
                }
            }

        } else {
            // LeftTuple is blocked, and thus not queued, so just update it's values
            rtnLeftTuple.update(salienceInt, pctx);
        }

        if( !rtnNode.isFireDirect() && executor.isDeclarativeAgendaEnabled()) {
            agenda.modifyActivation(rtnLeftTuple, rtnLeftTuple.isQueued());
        }
    }

    public void doLeftDeletes(InternalAgenda agenda,
                              TupleSets<LeftTuple> srcLeftTuples,
                              RuleExecutor executor) {

        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            doLeftDelete(agenda, executor, leftTuple);

            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public static void doLeftDelete(InternalAgenda agenda, RuleExecutor executor, Tuple leftTuple) {
        PropagationContext pctx = leftTuple.getPropagationContext();
        pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple, pctx);

        RuleTerminalNodeLeftTuple rtnLt = ( RuleTerminalNodeLeftTuple ) leftTuple;

        Activation activation = (Activation) leftTuple;
        activation.setMatched( false );

        agenda.cancelActivation( leftTuple,
                                 pctx,
                                 activation,
                                 rtnLt.getTerminalNode() );

        if ( leftTuple.getMemory() != null ) {
            // Expiration propagations should not be removed from the list, as they still need to fire
            executor.removeLeftTuple(leftTuple);
        }

        rtnLt.setActivationUnMatchListener(null);
        leftTuple.setContextObject( null );
    }

    private static boolean blockedByLockOnActive(RuleImpl rule,
                                          PropagationContext pctx,
                                          long handleRecency,
                                          InternalAgendaGroup agendaGroup) {
        if ( rule.isLockOnActive() ) {
            boolean isActive = agendaGroup.isActive();
            long activatedForRecency = agendaGroup.getActivatedForRecency();
            long clearedForRecency = agendaGroup.getClearedForRecency();

            if ( isActive && activatedForRecency < handleRecency &&
                 agendaGroup.getAutoFocusActivator() != pctx ) {
                return true;
            } else if ( clearedForRecency != -1 && clearedForRecency >= handleRecency ) {
                return true;
            }

        }
        return false;
    }
}

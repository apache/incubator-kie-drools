package org.drools.core.phreak;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Rule;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.index.LeftTupleList;

/**
* Created with IntelliJ IDEA.
* User: mdproctor
* Date: 03/05/2013
* Time: 15:42
* To change this template use File | Settings | File Templates.
*/
public class PhreakRuleTerminalNode {
    public void doNode(TerminalNode rtnNode,
                       InternalWorkingMemory wm,
                       LeftTupleSets srcLeftTuples,
                       RuleExecutor executor) {
        if (srcLeftTuples.getDeleteFirst() != null) {
            doLeftDeletes(rtnNode, wm, srcLeftTuples, executor);
        }

        if (srcLeftTuples.getUpdateFirst() != null) {
            doLeftUpdates(rtnNode, wm, srcLeftTuples, executor);
        }

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(rtnNode, wm, srcLeftTuples, executor);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(TerminalNode rtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              RuleExecutor executor) {
        boolean declarativeAgendaEnabled = executor.isDeclarativeAgendaEnabled();
        InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();
        int salience = 0;
        if( declarativeAgendaEnabled && rtnNode.getType() == NodeTypeEnums.RuleTerminalNode ) {
            salience = rtnNode.getRule().getSalience().getValue(null, null, null); // currently all branches have the same salience for the same rule
        }

        RuleAgendaItem agendaItem = executor.getRuleAgendaItem();
        LeftTupleList tupleList = executor.getLeftTupleList();
        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            if (  rtnNode.getRule().isLockOnActive() &&
                  leftTuple.getPropagationContext().getType() != org.kie.api.runtime.rule.PropagationContext.RULE_ADDITION ) {

                PropagationContext pctx = leftTuple.getPropagationContext();
                pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                         pctx);
                long handleRecency = ((InternalFactHandle) pctx.getFactHandle()).getRecency();
                InternalAgendaGroup agendaGroup = executor.getRuleAgendaItem().getAgendaGroup();
                if (blockedByLockOnActive(rtnNode.getRule(), agenda, pctx, handleRecency, agendaGroup)) {
                    leftTuple.clearStaged();
                    leftTuple = next;
                    continue;
                }
            }

            tupleList.add(leftTuple);
            leftTuple.increaseActivationCountForEvents(); // increased here, decreased in Agenda's cancelActivation and fireActivation
            if( !rtnNode.isFireDirect() && declarativeAgendaEnabled ) {
                PropagationContext pctx = leftTuple.getPropagationContext();

                AgendaItem item = agenda.createAgendaItem(leftTuple, salience, pctx,
                                                          rtnNode, agendaItem, agendaItem.getAgendaGroup(), agendaItem.getRuleFlowGroup() );
                item.setQueued(true);
                leftTuple.setObject(item);
                agenda.insertAndStageActivation(item);
            }
            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftUpdates(TerminalNode rtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              RuleExecutor executor) {
        boolean declarativeAgendaEnabled = executor.isDeclarativeAgendaEnabled();
        InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();

        LeftTupleList tupleList = executor.getLeftTupleList();
        for (LeftTuple leftTuple = srcLeftTuples.getUpdateFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            boolean reAdd = true;
            AgendaItem item = null;
            if( declarativeAgendaEnabled && leftTuple.getObject() != null ) {
               item = ( AgendaItem )leftTuple.getObject();
               if ( item.getBlockers() != null && !item.getBlockers().isEmpty() ) {
                   reAdd = false; // declarativeAgenda still blocking LeftTuple, so don't add back ot list
               }
            }
            if ( reAdd && leftTuple.getMemory() == null ) {
                if (  rtnNode.getRule().isLockOnActive() &&
                      leftTuple.getPropagationContext().getType() != org.kie.api.runtime.rule.PropagationContext.RULE_ADDITION ) {

                    PropagationContext pctx = leftTuple.getPropagationContext();
                    pctx = RuleTerminalNode.findMostRecentPropagationContext(leftTuple,
                                                                             pctx);
                    long handleRecency = ((InternalFactHandle) pctx.getFactHandle()).getRecency();
                    InternalAgendaGroup agendaGroup = executor.getRuleAgendaItem().getAgendaGroup();
                    if (blockedByLockOnActive(rtnNode.getRule(), agenda, pctx, handleRecency, agendaGroup)) {
                        leftTuple.clearStaged();
                        leftTuple = next;
                        continue;
                    }
                }

                tupleList.add(leftTuple);
            }

            if( !rtnNode.isFireDirect() && declarativeAgendaEnabled) {
                agenda.modifyActivation(item, item.isQueued());
            }
            leftTuple.clearStaged();
            leftTuple = next;
        }
    }

    public void doLeftDeletes(TerminalNode rtnNode,
                              InternalWorkingMemory wm,
                              LeftTupleSets srcLeftTuples,
                              RuleExecutor executor) {
        LeftTupleList tupleList = executor.getLeftTupleList();
        for (LeftTuple leftTuple = srcLeftTuples.getDeleteFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();
            PropagationContext pctx = leftTuple.getPropagationContext();
            if ( leftTuple.getMemory() != null && !(pctx.getType() == PropagationContext.EXPIRATION && pctx.getFactHandleOrigin() != null ) ) {
                // Expiration propagations should not be removed from the list, as they still need to fire
                tupleList.remove(leftTuple);
            }
            rtnNode.retractLeftTuple(leftTuple, leftTuple.getPropagationContext(), wm);
            leftTuple.clearStaged();
            leftTuple.setObject(null);
            leftTuple = next;
        }
    }

    private boolean blockedByLockOnActive(Rule rule,
                                          InternalAgenda agenda,
                                          PropagationContext pctx,
                                          long handleRecency,
                                          InternalAgendaGroup agendaGroup) {
        if ( rule.isLockOnActive() ) {
            boolean isActive = false;
            long activatedForRecency = 0;
            long clearedForRecency = 0;

            if ( rule.getRuleFlowGroup() == null ) {
                isActive = agendaGroup.isActive();
                activatedForRecency = agendaGroup.getActivatedForRecency();
                clearedForRecency = agendaGroup.getClearedForRecency();
            } else {
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) agenda.getRuleFlowGroup( rule.getRuleFlowGroup() );
                isActive = rfg.isActive();
                activatedForRecency = rfg.getActivatedForRecency();
                clearedForRecency = rfg.getClearedForRecency();
            }

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

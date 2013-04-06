package org.drools.core.phreak;

import org.drools.core.common.*;
import org.drools.core.reteoo.*;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.rule.Rule;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.index.LeftTupleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleNetworkEvaluatorActivation extends AgendaItem {

    private static final Logger log = LoggerFactory.getLogger(RuleNetworkEvaluatorActivation.class);

    private PathMemory rmem;

    private static RuleNetworkEvaluator networkEvaluator = new RuleNetworkEvaluator();

    private LeftTupleList tupleList;

    private boolean dirty;

    private boolean declarativeAgendaEnabled;

    public RuleNetworkEvaluatorActivation() {

    }

    public RuleNetworkEvaluatorActivation(final long activationNumber,
                                          final LeftTuple tuple,
                                          final int salience,
                                          final PropagationContext context,
                                          final PathMemory rmem,
                                          final TerminalNode rtn,
                                          boolean declarativeAgendaEnabled) {
        super(activationNumber, tuple, salience, context, rtn, null);
        this.rmem = rmem;
        tupleList = new LeftTupleList();
        this.declarativeAgendaEnabled = declarativeAgendaEnabled;
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDeclarativeAgendaEnabled() {
        return this.declarativeAgendaEnabled;
    }

    public int evaluateNetwork(InternalWorkingMemory wm, int fireCount, int fireLimit) {
        this.networkEvaluator.evaluateNetwork(rmem, wm, this);
        setDirty( false );

        //int fireCount = 0;
        int localFireCount = 0;
        if ( !tupleList.isEmpty() ) {
            RuleTerminalNode rtn =  ( RuleTerminalNode ) rmem.getRuleTerminalNode();
            Rule rule = rtn.getRule();

            InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();
            int salience = rule.getSalience().getValue(null, null, null); // currently all branches have the same salience for the same rule

            if ( isDeclarativeAgendaEnabled() ) {
                // Network Evaluation can notify meta rules, which should be given a chance to fire first
                RuleNetworkEvaluatorActivation nextRule = agenda.peekNextRule();
                if ( !isHighestSalience(nextRule, salience) ) {
                    // add it back onto the agenda, as the list still needs to be check after the meta rules have evalutated the matches
                    ((InternalAgenda) wm.getAgenda()).addActivation( this );
                    return localFireCount;
                }
            }

            start:
            while (!tupleList.isEmpty() ) {
                LeftTuple leftTuple = tupleList.removeFirst();

                rtn =  ( RuleTerminalNode ) leftTuple.getSink(); // branches result in multiple RTN's for a given rule, so unwrap per LeftTuple
                rule = rtn.getRule();

                PropagationContext pctx = leftTuple.getPropagationContext();
                pctx = RuleTerminalNode.findMostRecentPropagationContext( leftTuple,
                                                                          pctx );

                //check if the rule is not effective or
                // if the current Rule is no-loop and the origin rule is the same then return
                if (isNotEffective(wm, rtn, rule, leftTuple, pctx)) {
                    continue start;
                }

                long handleRecency = ((InternalFactHandle) pctx.getFactHandle()).getRecency();
                InternalAgendaGroup agendaGroup = (InternalAgendaGroup) agenda.getAgendaGroup(rule.getAgendaGroup());
                if (blockedByLockOnActive(rule, agenda, pctx, handleRecency, agendaGroup)) {
                    continue start;
                }


                AgendaItem item = ( AgendaItem ) leftTuple.getObject();
                if ( item == null ) {
                    item = agenda.createAgendaItem(leftTuple, salience, pctx, rtn, this);
                    item.setActivated(true);
                    leftTuple.setObject(item);
                } else {
                    item.setPropagationContext(pctx);
                }
                agenda.fireActivation(item);
                localFireCount++;


                RuleNetworkEvaluatorActivation nextRule = agenda.peekNextRule();
                if (haltRuleFiring(nextRule, fireCount, fireLimit, localFireCount, agenda, salience)) {
                    break; // another rule has high priority and is on the agenda, so evaluate it first
                }
                if (  isDirty() ) {
                    dequeue();
                    setDirty( false );
                    this.networkEvaluator.evaluateNetwork(rmem, wm, this);

                }
            }
        }

        return localFireCount;
    }

    private boolean isNotEffective(InternalWorkingMemory wm, RuleTerminalNode rtn, Rule rule, LeftTuple leftTuple, PropagationContext pctx) {
        // NB. stopped setting the LT.object to Boolean.TRUE, that Reteoo did.
        if ( (!rule.isEffective( leftTuple,
                                 rtn,
                                 wm )) ||
             (rule.isNoLoop() && rule.equals( pctx.getRuleOrigin() )) ) {
            return true;
        }

        if ( rule.getCalendars() != null ) {
            long timestamp = wm.getSessionClock().getCurrentTime();
            for ( String cal : rule.getCalendars() ) {
                if ( !wm.getCalendars().get( cal ).isTimeIncluded( timestamp ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean blockedByLockOnActive(Rule rule, InternalAgenda agenda, PropagationContext pctx, long handleRecency, InternalAgendaGroup agendaGroup) {
        if ( rule.isLockOnActive() ) {
            boolean isActive = false;
            long activatedForRecency = 0;
            long clearedForRecency = 0;

            if ( rule.getRuleFlowGroup() == null ) {
                isActive = agendaGroup.isActive();
                activatedForRecency = agendaGroup.getActivatedForRecency();
                clearedForRecency =  agendaGroup.getClearedForRecency();
            }   else {
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) agenda.getRuleFlowGroup( rule.getRuleFlowGroup() );
                isActive = rfg.isActive();
                activatedForRecency = rfg.getActivatedForRecency();
                clearedForRecency = rfg.getClearedForRecency();
            }

            if ( isActive && activatedForRecency < handleRecency &&
                 agendaGroup.getAutoFocusActivator() != pctx) {
                return true;
            } else if ( clearedForRecency != -1  && clearedForRecency >= handleRecency ) {
                return true;
            }

        }
        return false;
    }

    private boolean haltRuleFiring(RuleNetworkEvaluatorActivation nextRule, int fireCount, int fireLimit, int localFireCount, InternalAgenda agenda, int salience) {
        if ( !agenda.continueFiring(0) || !isHighestSalience(nextRule, salience) || ( fireLimit >=0 && (localFireCount + fireCount >= fireLimit )) ) {
            return true;
        }
        return false;
    }

    public boolean isHighestSalience(RuleNetworkEvaluatorActivation nextRule,
                                     int currentSalience) {
        return ( nextRule == null ) || nextRule.getRule().getSalience().getValue(null, null, null) <= currentSalience;
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

    public LeftTupleList getLeftTupleList() {
        return this.tupleList;
    }

}

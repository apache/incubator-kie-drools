package org.drools.core.phreak;

import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
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

    public RuleNetworkEvaluatorActivation() {

    }

    public RuleNetworkEvaluatorActivation(final long activationNumber,
                                          final LeftTuple tuple,
                                          final int salience,
                                          final PropagationContext context,
                                          final PathMemory rmem,
                                          final TerminalNode rtn) {
        super(activationNumber, tuple, salience, context, rtn);
        this.rmem = rmem;
        tupleList = new LeftTupleList();
    }

    public int evaluateNetwork(InternalWorkingMemory wm) {
        this.networkEvaluator.evaluateNetwork(rmem, wm, this);

        if ( !tupleList.isEmpty() ) {
            RuleTerminalNode rtn =  ( RuleTerminalNode ) rmem.getRuleTerminalNode();
            Rule rule = rtn.getRule();
            InternalAgenda agenda = ( InternalAgenda ) wm.getAgenda();

            int salience = rule.getSalience().getValue(null, null, null);

            start:
            while (!tupleList.isEmpty() ) {
                LeftTuple leftTuple = tupleList.removeFirst();

                PropagationContext pctx = leftTuple.getPropagationContext();
                pctx = RuleTerminalNode.findMostRecentPropagationContext( leftTuple,
                                                                          pctx );

                //check if the rule is not effective or
                // if the current Rule is no-loop and the origin rule is the same then return
                if ( (!rule.isEffective( leftTuple,
                                         rtn,
                                         wm )) ||
                     (rule.isNoLoop() && rule.equals( pctx.getRuleOrigin() )) ) {
                    leftTuple.setObject( Boolean.TRUE );
                    continue start;
                }

                if ( rule.getCalendars() != null ) {
                    long timestamp = wm.getSessionClock().getCurrentTime();
                    for ( String cal : rule.getCalendars() ) {
                        if ( !wm.getCalendars().get( cal ).isTimeIncluded( timestamp ) ) {
                            continue start;
                        }
                    }
                }

                AgendaItem item = agenda.createAgendaItem(leftTuple, salience, pctx, rtn);
                agenda.fireActivation(item);

                if ( !agenda.isActive( rule ) ) {
                    break; // another rule has high priority and is on the agenda, so evaluate it first
                }
            }

        }

        return 0;
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

    public LeftTupleList getLeftTupleList() {
        return this.tupleList;
    }

}

package org.drools.core.phreak;

import org.drools.core.common.AgendaItem;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleInstanceAgendaItem extends AgendaItem {

    private static final Logger log = LoggerFactory.getLogger(RuleInstanceAgendaItem.class);

    public RuleExecutor executor;


    public RuleInstanceAgendaItem() {

    }

    public RuleInstanceAgendaItem(final long activationNumber,
                                  final LeftTuple tuple,
                                  final int salience,
                                  final PropagationContext context,
                                  final PathMemory rmem,
                                  final TerminalNode rtn,
                                  boolean declarativeAgendaEnabled) {
        super(activationNumber, tuple, salience, context, rtn, null);
        executor = new RuleExecutor(rmem, this, declarativeAgendaEnabled);
    }

    public RuleExecutor getRuleExecutor() {
        return executor;
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return true;
    }

}

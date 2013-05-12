package org.drools.core.phreak;

import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleAgendaItem extends AgendaItemImpl {

    private static final Logger log = LoggerFactory.getLogger(RuleAgendaItem.class);

    public RuleExecutor executor;


    public RuleAgendaItem() {

    }

    public RuleAgendaItem(final long activationNumber,
                          final LeftTuple tuple,
                          final int salience,
                          final PropagationContext context,
                          final PathMemory rmem,
                          final TerminalNode rtn,
                          boolean declarativeAgendaEnabled,
                          InternalAgendaGroup agendaGroup,
                          InternalRuleFlowGroup ruleFlowGroup) {
        super(activationNumber, tuple, salience, context, rtn, null, agendaGroup, ruleFlowGroup);
        executor = new RuleExecutor(rmem, this, declarativeAgendaEnabled);
    }

    public RuleExecutor getRuleExecutor() {
        return executor;
    }

    public boolean isRuleAgendaItem() {
        return true;
    }

}

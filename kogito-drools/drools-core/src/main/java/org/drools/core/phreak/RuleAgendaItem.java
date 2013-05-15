package org.drools.core.phreak;

import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.LinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class RuleAgendaItem extends AgendaItemImpl implements LinkedListNode<RuleAgendaItem> {

    private static final Logger log = LoggerFactory.getLogger(RuleAgendaItem.class);

    public RuleExecutor executor;

    private RuleAgendaItem previous;

    private RuleAgendaItem next;

    public RuleAgendaItem() {

    }

    public RuleAgendaItem(final long activationNumber,
                          final LeftTuple tuple,
                          final int salience,
                          final PropagationContext context,
                          final PathMemory pmem,
                          final TerminalNode rtn,
                          boolean declarativeAgendaEnabled,
                          InternalAgendaGroup agendaGroup,
                          InternalRuleFlowGroup ruleFlowGroup) {
        super(activationNumber, tuple, salience, context, rtn, null, agendaGroup, ruleFlowGroup);
        executor = new RuleExecutor(pmem, this, declarativeAgendaEnabled);
    }

    public RuleExecutor getRuleExecutor() {
        return executor;
    }

    public boolean isRuleAgendaItem() {
        return true;
    }


    public RuleAgendaItem getPrevious() {
        return previous;
    }

    public void setPrevious(RuleAgendaItem previous) {
        this.previous = previous;
    }

    public void setNext(RuleAgendaItem next) {
        this.next = next;
    }

    public RuleAgendaItem getNext() {
        return next;
    }

    public boolean isInList() {
        return previous != null && next != null;
    }
}

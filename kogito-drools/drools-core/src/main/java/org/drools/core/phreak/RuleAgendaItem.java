package org.drools.core.phreak;

import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedListNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleAgendaItem extends AgendaItemImpl implements LinkedListNode<RuleAgendaItem> {

    private static final Logger log = LoggerFactory.getLogger(RuleAgendaItem.class);
    public           RuleExecutor   executor;
    private          RuleAgendaItem previous;
    private          RuleAgendaItem next;
    private volatile boolean        blocked;

    public RuleAgendaItem() {

    }

    public RuleAgendaItem(final long activationNumber,
                          final LeftTuple tuple,
                          final int salience,
                          final PropagationContext context,
                          final PathMemory pmem,
                          final TerminalNode rtn,
                          boolean declarativeAgendaEnabled,
                          InternalAgendaGroup agendaGroup) {
        super(activationNumber, tuple, salience, context, rtn, agendaGroup);
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

    public RuleAgendaItem getNext() {
        return next;
    }

    public void setNext(RuleAgendaItem next) {
        this.next = next;
    }

    public boolean isInList() {
        return previous != null && next != null;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        return object instanceof RuleAgendaItem && getRule().equals(((RuleAgendaItem) object).getRule());
    }

    /**
     * Return the hashCode of the
     * <code>TupleKey<code> as the hashCode of the AgendaItem
     *
     * @return
     */
    @Override
    public int hashCode() {
        return getRule().hashCode();
    }
}

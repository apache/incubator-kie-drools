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

import org.drools.core.common.AgendaItemImpl;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.LinkedList;
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
                          final Tuple tuple,
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

    public boolean isInList( LinkedList<RuleAgendaItem> list ) {
        return previous != null || next != null || list.getFirst() == this;
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

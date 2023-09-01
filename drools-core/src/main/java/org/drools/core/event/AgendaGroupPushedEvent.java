package org.drools.core.event;

import org.drools.core.common.InternalAgendaGroup;

public class AgendaGroupPushedEvent extends AgendaGroupEvent {
    private static final long serialVersionUID = 510l;

    public AgendaGroupPushedEvent(final InternalAgendaGroup agendaGroup) {
        super( agendaGroup );
    }

    public String toString() {
        return ">==[AgendaGroupPushedEvent(" + getAgendaGroup().getName() + "]";
    }
}

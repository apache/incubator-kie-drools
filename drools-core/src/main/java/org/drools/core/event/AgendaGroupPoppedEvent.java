package org.drools.core.event;

import org.drools.core.common.InternalAgendaGroup;

public class AgendaGroupPoppedEvent extends AgendaGroupEvent {
    private static final long serialVersionUID = 510l;

    public AgendaGroupPoppedEvent(final InternalAgendaGroup agendaGroup) {
        super( agendaGroup );
    }

    public String toString() {
        return "<==[AgendaGroupPoppedEvent(" + getAgendaGroup().getName() + "]";
    }
}

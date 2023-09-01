package org.drools.core.event;

import java.util.EventObject;

import org.drools.core.common.InternalAgendaGroup;

public class AgendaGroupEvent extends EventObject {

    private static final long serialVersionUID = 510l;

    public AgendaGroupEvent(final InternalAgendaGroup agendaGroup) {
        super( agendaGroup );
    }

    public InternalAgendaGroup getAgendaGroup() {
        return (InternalAgendaGroup) getSource();
    }

}

package org.drools.common;

import java.util.List;

import org.drools.event.AgendaEventSupport;

public interface EventSupport {
    public List getAgendaEventListeners();

    public List getWorkingMemoryEventListeners();

    public AgendaEventSupport getAgendaEventSupport();
}

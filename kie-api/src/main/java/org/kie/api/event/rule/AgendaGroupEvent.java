package org.kie.api.event.rule;

import org.kie.api.event.KieRuntimeEvent;
import org.kie.api.runtime.rule.AgendaGroup;

public interface AgendaGroupEvent
    extends
    KieRuntimeEvent {

    public AgendaGroup getAgendaGroup();

}

package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.AgendaGroup;

public class AgendaGroupPoppedEventImpl extends AgendaGroupEventImpl implements AgendaGroupPoppedEvent {

    public AgendaGroupPoppedEventImpl(AgendaGroup agendaGroup, KieRuntime kruntime ) {
        super( agendaGroup, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public AgendaGroupPoppedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[AgendaGroupPoppedEvent: getAgendaGroup()=" + getAgendaGroup() + ", getKnowledgeRuntime()="
                + getKieRuntime() + "]";
    }
}

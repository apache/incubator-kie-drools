package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.AgendaGroup;

public class AgendaGroupPushedEventImpl extends AgendaGroupEventImpl implements AgendaGroupPushedEvent {

    public AgendaGroupPushedEventImpl(AgendaGroup agendaGroup, KieRuntime kruntime ) {
        super( agendaGroup, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public AgendaGroupPushedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[AgendaGroupPushedEvent: getAgendaGroup()=" + getAgendaGroup() + ", getKnowledgeRuntime()="
                + getKieRuntime() + "]";
    }
}

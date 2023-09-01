package org.drools.core.common;


import org.drools.core.impl.InternalRuleBase;

public interface AgendaGroupFactory {
    InternalAgendaGroup createAgendaGroup(String name, InternalRuleBase kBase);
}

package org.drools.common;

import org.drools.spi.AgendaGroup;

public interface AgendaGroupFactory {    
    AgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase);
}
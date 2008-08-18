package org.drools.common;


public interface AgendaGroupFactory {    
    InternalAgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase);
}
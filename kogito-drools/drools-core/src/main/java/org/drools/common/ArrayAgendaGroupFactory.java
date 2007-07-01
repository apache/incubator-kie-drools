package org.drools.common;

import org.drools.spi.AgendaGroup;

public class ArrayAgendaGroupFactory implements AgendaGroupFactory {
    private static final AgendaGroupFactory INSTANCE = new ArrayAgendaGroupFactory();
    
    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }    
    
    public AgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase) {
        return new ArrayAgendaGroup( name,
                                     ruleBase );        
    }
}

package org.drools.common;


public class ArrayAgendaGroupFactory implements AgendaGroupFactory {
    private static final AgendaGroupFactory INSTANCE = new ArrayAgendaGroupFactory();
    
    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }    
    
    public InternalAgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase) {
        return new ArrayAgendaGroup( name,
                                     ruleBase );        
    }
}

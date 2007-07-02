package org.drools.common;

import java.io.Serializable;

import org.drools.spi.AgendaGroup;

public class PriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Serializable {
    private static final AgendaGroupFactory INSTANCE = new PriorityQueueAgendaGroupFactory();
    
    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }
    
    public AgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase) {
        return new BinaryHeapQueueAgendaGroup( name,
                                    ruleBase );        
    }
}

package org.drools.kiesession.agenda;


import org.drools.core.common.AgendaFactory;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;

import java.io.Serializable;

public class DefaultAgendaFactory implements AgendaFactory, Serializable {

    private static final AgendaFactory INSTANCE = new DefaultAgendaFactory();

    public static AgendaFactory getInstance() {
        return INSTANCE;
    }

    private DefaultAgendaFactory() { }

    public InternalAgenda createAgenda(InternalWorkingMemory workingMemory) {
        return workingMemory.getKnowledgeBase().getRuleBaseConfiguration().isParallelExecution() ?
                new CompositeDefaultAgenda( workingMemory ) :
                new DefaultAgenda( workingMemory );
    }
}

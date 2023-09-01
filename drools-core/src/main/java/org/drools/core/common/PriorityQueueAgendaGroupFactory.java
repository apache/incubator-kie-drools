package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.impl.InternalRuleBase;


public class PriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Externalizable {

    private static final AgendaGroupFactory INSTANCE = new PriorityQueueAgendaGroupFactory();

    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException { }

    public void writeExternal(ObjectOutput out) throws IOException { }

    public InternalAgendaGroup createAgendaGroup(String name, InternalRuleBase kBase) {
        return new AgendaGroupQueueImpl( name, kBase );
    }
}

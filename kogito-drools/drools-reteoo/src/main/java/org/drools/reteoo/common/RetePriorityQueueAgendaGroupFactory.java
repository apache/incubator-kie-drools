package org.drools.reteoo.common;

import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.impl.InternalKnowledgeBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RetePriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Externalizable {

    private static final AgendaGroupFactory INSTANCE = new RetePriorityQueueAgendaGroupFactory();

    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public InternalAgendaGroup createAgendaGroup(String name, InternalKnowledgeBase kBase) {
        return new ReteAgendaGroupQueueImpl( name, kBase );
    }
}

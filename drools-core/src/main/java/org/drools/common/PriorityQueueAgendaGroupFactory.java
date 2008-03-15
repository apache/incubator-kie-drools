package org.drools.common;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.spi.AgendaGroup;

public class PriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Externalizable {
    private static final AgendaGroupFactory INSTANCE = new PriorityQueueAgendaGroupFactory();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }

    public AgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase) {
        return new BinaryHeapQueueAgendaGroup( name,
                                    ruleBase );
    }
}

package org.drools.event.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.runtime.rule.AgendaGroup;

public class SerializableAgendaGroup implements AgendaGroup, Externalizable {
    
    private String name;
    
    SerializableAgendaGroup() {
        
    }
    
    SerializableAgendaGroup(AgendaGroup agendaGroup) {
        this.name = agendaGroup.getName();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( this.name );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = in.readUTF();
    }
    
    public String getName() {
        return this.name;
    }    

}

package org.drools.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.Agenda;
import org.drools.common.InternalAgenda;
import org.drools.runtime.rule.AgendaGroup;

public class AgendaGroupImpl implements AgendaGroup, Externalizable {
    
    private String name;
    
    private InternalAgenda agenda;
    
    AgendaGroupImpl() {
        
    }
    
    AgendaGroupImpl(org.drools.spi.AgendaGroup agendaGroup, InternalAgenda agenda) {
        this.name = agendaGroup.getName();
        this.agenda = agenda;
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
    
    public void clear() {
        this.agenda.clearAndCancelAgendaGroup( this.name );
    }    
    
    public void setFocus() {
        this.agenda.setFocus( this.name );
    }

}

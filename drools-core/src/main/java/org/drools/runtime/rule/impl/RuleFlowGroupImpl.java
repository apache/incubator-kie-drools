package org.drools.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.Agenda;
import org.drools.common.InternalAgenda;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupImpl implements RuleFlowGroup, Externalizable {
    
    private String name;
    
    private InternalAgenda agenda;
    
    RuleFlowGroupImpl() {
        
    }
    
    RuleFlowGroupImpl(org.drools.spi.RuleFlowGroup ruleflowGroup, InternalAgenda agenda) {
        this.name = ruleflowGroup.getName();
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
        this.agenda.clearAndCancelRuleFlowGroup( this.name );
    }    

}

package org.drools.runtime.rule.impl;

import org.drools.common.InternalAgenda;
import org.drools.runtime.rule.ActivationGroup;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.RuleFlowGroup;

public class AgendaImpl
    implements
    Agenda {
    private InternalAgenda agenda;    
    
    public AgendaImpl(InternalAgenda agenda) {
        super();
        this.agenda = agenda;
    }

    public void clear() {
        this.agenda.clearAndCancel();
    }
    
    public AgendaGroup getAgendaGroup(String name) {
        org.drools.spi.AgendaGroup agendaGroup = this.agenda.getAgendaGroup( name );
        if (  agendaGroup != null ) {
            return new AgendaGroupImpl( agendaGroup, this.agenda );
        } else {
            return null;
        }
    }
    
    public ActivationGroup getActivationGroup(String name) {
        org.drools.spi.ActivationGroup activationGroup = this.agenda.getActivationGroup( name );
        if (  activationGroup != null ) {
            return new ActivationGroupImpl( activationGroup, this.agenda );
        } else {
            return null;
        }
    }    
    
    public RuleFlowGroup getRuleFlowGroup(String name) {
        org.drools.spi.RuleFlowGroup ruleFlowGroup = this.agenda.getRuleFlowGroup( name );
        if (  ruleFlowGroup != null ) {
            return new RuleFlowGroupImpl( ruleFlowGroup, this.agenda );
        } else {
            return null;
        }
    }       
}

package org.drools.common;

import org.drools.core.util.Iterator;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.spi.Activation;

public class ActiveActivationIterator
    implements
    Iterator {
    private DefaultAgenda      agenda;

    private static final int   AGENDA_GROUPS   = 0;
    private static final int   RULEFLOW_GROUPS = 1;

    private java.util.Iterator groupsIter;

    private int                group           = -1;

    Activation[]               activations;
    int                        pos             = 0;

    ActiveActivationIterator() {

    }

    private ActiveActivationIterator(InternalWorkingMemory wm) {
        agenda = (DefaultAgenda) wm.getAgenda();

        if ( !agenda.getAgendaGroupsMap().isEmpty() ) {
            groupsIter = agenda.getAgendaGroupsMap().values().iterator();
            group = AGENDA_GROUPS;

            InternalAgendaGroup group = null;
            for ( ; groupsIter.hasNext();) {
                group = (InternalAgendaGroup) groupsIter.next() ;
                if ( !group.isEmpty() ) {
                    activations = (Activation[]) group.getActivations();
                    return;
                }
            }
        }
        
        if ( !agenda.getRuleFlowGroupsMap().isEmpty() ) {
            groupsIter = agenda.getRuleFlowGroupsMap().values().iterator();
            group = RULEFLOW_GROUPS;

            RuleFlowGroupImpl group = null;
            for ( ; groupsIter.hasNext();) {
                group = (RuleFlowGroupImpl) groupsIter.next() ;
                if ( !group.isEmpty() ) {
                    activations = (Activation[]) group.getActivations();
                    return;
                }
            }
        }        
    }

    public static ActiveActivationIterator iterator(InternalWorkingMemory wm) {
        return new ActiveActivationIterator( wm );
    }
    
    public static ActiveActivationIterator iterator(StatefulKnowledgeSession ksession) {
        return new ActiveActivationIterator( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory() );
    }

    public Object next() {
        if ( activations == null ) {
            return null;
        }
        
        if ( pos < activations.length ) {
            Activation act = activations[pos++];
            return act;
        } else {
            if (group == AGENDA_GROUPS ) {
                InternalAgendaGroup agendaGroup = null;
                for ( ; groupsIter.hasNext();) {
                    agendaGroup = (InternalAgendaGroup) groupsIter.next() ;
                    if ( !agendaGroup.isEmpty() ) {
                        activations = (Activation[]) agendaGroup.getActivations();
                        pos = 0;                        
                        Activation act = activations[pos++];                        
                        return act;
                    }
                }
                groupsIter = agenda.getRuleFlowGroupsMap().values().iterator();
                group = RULEFLOW_GROUPS;                
            }            

            RuleFlowGroupImpl ruleflowGroup = null;
            for ( ; groupsIter.hasNext();) {
                ruleflowGroup = (RuleFlowGroupImpl) groupsIter.next() ;
                if ( !ruleflowGroup.isEmpty() ) {
                    activations = (Activation[]) ruleflowGroup.getActivations();
                    pos = 0;
                    Activation act = activations[pos++];
                    return act;
                }
            }              
        }
        
        return null;
    }

}

package org.drools.core.common;

import org.drools.core.util.Iterator;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.spi.Activation;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class ActiveActivationIterator
    implements
    Iterator {
    private InternalAgenda    agenda;

    private static final int   AGENDA_GROUPS   = 0;

    private java.util.Iterator groupsIter;

    private int                group           = -1;

    Activation[]               activations;
    int                        pos             = 0;

    ActiveActivationIterator() {

    }

    private ActiveActivationIterator(InternalWorkingMemory wm) {
        agenda = (InternalAgenda) wm.getAgenda();

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

    }

    public static Iterator iterator(InternalWorkingMemory wm) {
        if (wm.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return PhreakActiveActivationIterator.iterator(wm);
        } else {
            return new ActiveActivationIterator( wm );
        }
    }
    
    public static Iterator iterator(StatefulKnowledgeSession ksession) {
        InternalWorkingMemory wm = ((InternalWorkingMemoryEntryPoint) ksession).getInternalWorkingMemory();
        if (wm.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return PhreakActiveActivationIterator.iterator(wm);
        } else {
            return new ActiveActivationIterator( ((StatefulKnowledgeSessionImpl) ksession).getInternalWorkingMemory() );
        }
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

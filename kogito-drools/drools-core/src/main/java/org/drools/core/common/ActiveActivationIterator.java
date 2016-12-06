/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import org.drools.core.spi.Activation;
import org.drools.core.util.Iterator;
import org.kie.api.runtime.KieSession;

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
        return PhreakActiveActivationIterator.iterator(wm);
    }
    
    public static Iterator iterator(KieSession ksession) {
        InternalWorkingMemory wm = ((InternalWorkingMemoryEntryPoint) ksession).getInternalWorkingMemory();
        return PhreakActiveActivationIterator.iterator(wm);
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

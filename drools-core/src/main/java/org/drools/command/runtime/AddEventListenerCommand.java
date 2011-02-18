/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class AddEventListenerCommand
    implements
    GenericCommand<Object> {

    private WorkingMemoryEventListener workingMemoryEventlistener = null;
    private AgendaEventListener        agendaEventlistener        = null;
    private ProcessEventListener       processEventListener       = null;

    public AddEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventlistener = listener;
    }

    public AddEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventlistener = listener;
    }
    
    public AddEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }


    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        if ( workingMemoryEventlistener != null ) {
            ksession.addEventListener( workingMemoryEventlistener );
        } else if ( agendaEventlistener != null ) {
            ksession.addEventListener( agendaEventlistener );
        } else {
            ksession.addEventListener( processEventListener );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventlistener != null ) {
            return "session.addEventListener( " + workingMemoryEventlistener + " );";
        } else if ( agendaEventlistener != null ) {
            return "session.addEventListener( " + agendaEventlistener + " );";
        }  else  if ( processEventListener != null ) {
            return "session.addEventListener( " + processEventListener + " );";
        }
        
        return "AddEventListenerCommand";
    }
}

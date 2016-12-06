/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.runtime;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.Context;

public class RemoveEventListenerCommand
    implements
    ExecutableCommand<Void> {

    private RuleRuntimeEventListener   ruleRuntimeEventlistener = null;
    private AgendaEventListener        agendaEventListener        = null;
    private ProcessEventListener       processEventListener       = null;
    
    public RemoveEventListenerCommand() {
    }

    public RemoveEventListenerCommand(RuleRuntimeEventListener listener) {
        this.ruleRuntimeEventlistener = listener;
    }

    public RemoveEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventListener = listener;
    }

    public RemoveEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if ( ruleRuntimeEventlistener != null ) {
            ksession.removeEventListener( ruleRuntimeEventlistener );
        } else if ( agendaEventListener != null ) {
            ksession.removeEventListener( agendaEventListener );
        } else {
            ksession.removeEventListener( processEventListener );
        }
        return null;
    }

    public String toString() {
        if ( ruleRuntimeEventlistener != null ) {
            return "session.removeEventListener( " + ruleRuntimeEventlistener + " );";
        } else if ( agendaEventListener != null ) {
            return "session.removeEventListener( " + agendaEventListener + " );";
        } else {
            return "session.removeEventListener( " + processEventListener + " );";
        }
    }
}

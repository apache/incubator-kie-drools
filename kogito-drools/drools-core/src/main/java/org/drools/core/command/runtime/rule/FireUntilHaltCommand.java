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

package org.drools.core.command.runtime.rule;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.command.runtime.UnpersistableCommand;
import org.drools.core.reteoo.ReteooWorkingMemoryInterface;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.command.Context;

public class FireUntilHaltCommand
    implements
    GenericCommand<Void>, UnpersistableCommand {
    private static final long serialVersionUID = 510l;

    private AgendaFilter agendaFilter = null;

    public FireUntilHaltCommand() {
    }

    public FireUntilHaltCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        final ReteooWorkingMemoryInterface session = (ReteooWorkingMemoryInterface)ksession;
        
        new Thread(new Runnable() {
            public void run() {
                if ( agendaFilter != null ) {
                    session.fireUntilHalt( agendaFilter );
                } else {
                    session.fireUntilHalt();
                }
            }
        }).start();

        return null;
    }

    public String toString() {
        if ( agendaFilter != null ) {
            return "session.fireUntilHalt( " + agendaFilter + " );";
        } else {
            return "session.fireUntilHalt();";
        }
    }
}

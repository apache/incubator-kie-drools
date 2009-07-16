package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class UnregisterExitPointCommand
    implements
    GenericCommand<Object> {

    private String name;

    public UnregisterExitPointCommand(String name) {
        this.name = name;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        ksession.unregisterExitPoint( name );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.unregisterExitPoint( " + name + " );";
    }
}

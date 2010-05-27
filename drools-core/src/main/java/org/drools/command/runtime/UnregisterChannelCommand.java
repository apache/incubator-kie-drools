package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;

public class UnregisterChannelCommand
    implements
    GenericCommand<Object> {

    private static final long serialVersionUID = -758966367778702633L;
    
    private String name;

    public UnregisterChannelCommand(String name) {
        this.name = name;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        ksession.unregisterChannel( name );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.unregisterChannel( " + name + " );";
    }
}

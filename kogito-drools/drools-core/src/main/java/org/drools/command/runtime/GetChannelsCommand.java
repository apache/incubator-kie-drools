package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetChannelsCommand
    implements
    GenericCommand<Object> {

    private static final long serialVersionUID = -758966367778702633L;
    
    public GetChannelsCommand() {
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getChannels();
    }

    public String toString() {
        return "reteooStatefulSession.getChannels( );";
    }
}

package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.Channel;
import org.drools.runtime.StatefulKnowledgeSession;

public class RegisterChannelCommand
    implements
    GenericCommand<Object> {

    private static final long serialVersionUID = 9105151811053790544L;

    private String  name;
    private Channel channel;

    public RegisterChannelCommand(String name,
                                  Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();

        ksession.registerChannel( name,
                                  channel );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.registerChannel( " + name + ", " + channel + " );";
    }
}

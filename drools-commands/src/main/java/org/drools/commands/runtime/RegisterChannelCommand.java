package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class RegisterChannelCommand
    implements
    ExecutableCommand<Void> {

    private static final long serialVersionUID = 510l;

    private String  name;
    private Channel channel;
    
    public RegisterChannelCommand() {
    }

    public RegisterChannelCommand(String name,
                                  Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        ksession.registerChannel( name,
                                  channel );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.registerChannel( " + name + ", " + channel + " );";
    }
}

package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class UnregisterChannelCommand
    implements
    ExecutableCommand<Void> {

    private static final long serialVersionUID = 510l;
    
    private String name;
    
    public UnregisterChannelCommand() {
    }

    public UnregisterChannelCommand(String name) {
        this.name = name;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        ksession.unregisterChannel( name );

        return null;
    }

    public String toString() {
        return "reteooStatefulSession.unregisterChannel( " + name + " );";
    }
}

package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetChannelsCommand
    implements
    ExecutableCommand<Object> {

    private static final long serialVersionUID = 510l;
    
    public GetChannelsCommand() {
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getChannels();
    }

    public String toString() {
        return "reteooStatefulSession.getChannels( );";
    }
}

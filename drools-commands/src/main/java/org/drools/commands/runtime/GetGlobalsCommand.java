package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetGlobalsCommand
    implements
    ExecutableCommand<Globals> {

    public Globals execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getGlobals();
    }

    public String toString() {
        return "session.getGlobalResolver()";
    }
}

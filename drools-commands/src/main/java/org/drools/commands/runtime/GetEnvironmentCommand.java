package org.drools.commands.runtime;

import org.drools.commands.impl.NotTransactionalCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetEnvironmentCommand
    implements
    NotTransactionalCommand<Environment> {

    public Environment execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getEnvironment();
    }

    public String toString() {
        return "session.getEnvironment();";
    }

}

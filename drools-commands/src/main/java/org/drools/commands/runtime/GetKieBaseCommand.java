package org.drools.commands.runtime;

import org.kie.api.KieBase;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetKieBaseCommand
    implements
    ExecutableCommand<KieBase> {

    public GetKieBaseCommand() {
    }

    public KieBase execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getKieBase();
    }

    public String toString() {
        return "session.getKieBase();";
    }

}

package org.drools.commands.runtime.rule;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class HaltCommand
    implements
    ExecutableCommand<Void> {

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ksession.halt();
        return null;
    }

    public String toString() {
        return "session.halt();";
    }
}

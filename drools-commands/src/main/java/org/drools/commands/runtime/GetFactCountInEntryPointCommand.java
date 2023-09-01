package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetFactCountInEntryPointCommand
    implements
    ExecutableCommand<Long> {

    private String entryPoint;

    public GetFactCountInEntryPointCommand(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public Long execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        return ksession.getEntryPoint(entryPoint).getFactCount();
    }

    public String toString() {
        return "ksession.getEntryPoint( " + entryPoint + " ).getFactCount();";
    }

}

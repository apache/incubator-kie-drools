package org.drools.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.command.RegistryContext;

public class SetKieContainerCommand
        implements
        ExecutableCommand<KieContainer> {

    private static final long serialVersionUID = 2985535777825271597L;
    private final KieContainer kieContainer;

    public SetKieContainerCommand(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    public KieContainer execute(Context context) {
        ((RegistryContext) context).register(KieContainer.class, kieContainer);
        return kieContainer;
    }

    @Override
    public String toString() {
        return "SetKieContainerCommand{" +
                "kieContainer=" + kieContainer +
                '}';
    }
}

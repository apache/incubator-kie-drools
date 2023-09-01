package org.drools.commands;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.command.RegistryContext;

public class GetKieContainerCommand
        implements
        ExecutableCommand<KieContainer> {

    private static final long serialVersionUID = 8748826714594402049L;
    private ReleaseId releaseId;

    public GetKieContainerCommand(ReleaseId releaseId) {
        this.releaseId = releaseId;
    }

    public KieContainer execute(Context context) {
        // use the new API to retrieve the session by ID
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);

        ((RegistryContext) context).register(KieContainer.class, kieContainer);
        return kieContainer;
    }

    public ReleaseId getReleaseId() {
        return releaseId;
    }

    @Override
    public String toString() {
        return "GetKieContainerCommand{" +
                "releaseId=" + releaseId +
                '}';
    }
}

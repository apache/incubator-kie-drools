package org.drools.commands;

import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.command.RegistryContext;

public class AbstractNewKieContainerCommand {

    protected KieContainer getKieContainer(RegistryContext context, ReleaseId releaseId) {
        KieContainer kieContainer;
        if (releaseId != null) {
            // use the new API to retrieve the session by ID
            KieServices kieServices = KieServices.Factory.get();
            kieContainer = kieServices.newKieContainer(releaseId);
        } else {
            kieContainer = context.lookup(KieContainer.class);
            if (kieContainer == null) {
                throw new RuntimeException("ReleaseId was not specified, nor was an existing KieContainer assigned to the Registry");
            }
        }
        return kieContainer;
    }
}

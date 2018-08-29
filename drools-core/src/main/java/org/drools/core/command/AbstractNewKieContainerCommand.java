package org.drools.core.command;

import org.drools.core.command.impl.RegistryContext;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

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
                throw new RuntimeException("ReleaseId was not specfied, nor was an existing KieContainer assigned to the Registry");
            }
        }
        return kieContainer;
    }
}

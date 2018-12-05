package org.kie.dmn.core.fluent;

import org.kie.dmn.api.core.DMNResult;
import org.kie.internal.command.RegistryContext;

public abstract class AbstractDMNResultCommand {

    protected DMNResult extractDMNResult(RegistryContext context) {
        RegistryContext registryContext = context;
        DMNResult dmnResult = registryContext.lookup(DMNResult.class);
        if (dmnResult == null) {
            throw new IllegalStateException("There is no DMNResult available");
        }
        return dmnResult;
    }
}

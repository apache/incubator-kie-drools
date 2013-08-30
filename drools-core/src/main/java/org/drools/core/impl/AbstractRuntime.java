package org.drools.core.impl;

import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieRuntimeLogger;

public abstract class AbstractRuntime implements KieRuntimeEventManager {

    protected KieRuntimeLogger logger;

    public KieRuntimeLogger getLogger() {
        return logger;
    }

    public void setLogger(KieRuntimeLogger logger) {
        this.logger = logger;
    }
}

package org.drools.commands;


import org.kie.api.runtime.KieSession;

public interface SingleSessionCommandService extends InternalLocalRunner {
    KieSession getKieSession();
    Long getSessionId();

    void dispose();
    void destroy();
}

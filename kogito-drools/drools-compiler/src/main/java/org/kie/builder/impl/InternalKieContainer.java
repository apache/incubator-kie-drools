package org.kie.builder.impl;

import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;

public interface InternalKieContainer extends KieContainer {

    /**
     * Returns an already created defualt KieSession for this KieContainer or creates a new one
     * @throws a RuntimeException if this KieContainer doesn't have any defualt KieSession
     * @see org.kie.builder.model.KieSessionModel#setDefault(boolean)
     */
    KieSession getKieSession();

    /**
     * Returns an already created KieSession with the given name for this KieContainer or creates a new one
     * @throws a RuntimeException if this KieContainer doesn't have any defualt KieSession
     * @see org.kie.builder.model.KieSessionModel#setDefault(boolean)
     */
    KieSession getKieSession(String kSessionName);

    /**
     * Disposes all the KieSessions created in this KieContainer
     */
    void dispose();
}

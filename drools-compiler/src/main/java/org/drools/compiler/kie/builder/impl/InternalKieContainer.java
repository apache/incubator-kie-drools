package org.drools.compiler.kie.builder.impl;

import org.kie.builder.model.KieBaseModel;
import org.kie.builder.model.KieSessionModel;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

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

    StatelessKieSession getStatelessKieSession();

    StatelessKieSession getStatelessKieSession(String kSessionName);

    /**
     * Disposes all the KieSessions created in this KieContainer
     */
    void dispose();

    /**
     * Returns the KieBaseModel for the KieBase with the given name
     */
    KieBaseModel getKieBaseModel(String kBaseName);

    /**
     * Returns the KieSessionModel for the KieSession with the given name
     */
    KieSessionModel getKieSessionModel(String kSessionName);
}

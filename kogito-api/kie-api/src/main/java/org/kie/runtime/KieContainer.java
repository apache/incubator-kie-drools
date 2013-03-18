package org.kie.runtime;

import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;

/**
 * A container for all the KieBases of a given KieModule
 */
public interface KieContainer {

    /**
     * Returns the ReleaseId of the KieModule wrapped by this KieContainer
     */
    ReleaseId getReleaseId();

    /**
     * Builds all the KieBase in the KieModule wrapped by this KieContainer
     * and return te Results of this building process
     */
    Results verify();

    /**
     * Updates this KieContainer to a KieModule with the given ReleaseId
     */
    void updateToVersion(ReleaseId version);

    /**
     * Returns the defualt KieBase in this KieContainer.
     * @throws a RuntimeException if this KieContainer doesn't have any defualt KieBase
     * @see org.kie.api.builder.model.KieBaseModel#setDefault(boolean)
     */
    KieBase getKieBase();

    /**
     * Returns the KieBase with the given name in this KieContainer.
     * @throws a RuntimeException if this KieContainer doesn't have any KieBase with the given name
     */
    KieBase getKieBase(String kBaseName);

    /**
     * Creates the defualt KieSession for this KieContainer
     * @throws a RuntimeException if this KieContainer doesn't have any defualt KieSession
     * @see org.kie.api.builder.model.KieSessionModel#setDefault(boolean)
     */
    KieSession newKieSession();

    /**
     * Creates the defualt KieSession for this KieContainer using the given Environment
     * @throws a RuntimeException if this KieContainer doesn't have any defualt KieSession
     * @see org.kie.api.builder.model.KieSessionModel#setDefault(boolean)
     */
    KieSession newKieSession(Environment environment);

    /**
     * Creates the KieSession with the given name for this KieContainer
     * @throws a RuntimeException if this KieContainer doesn't have any KieSession with the given name
     */
    KieSession newKieSession(String kSessionName);

    /**
     * Creates the KieSession with the given name for this KieContainer using the given Environment
     * @throws a RuntimeException if this KieContainer doesn't have any KieSession with the given name
     */
    KieSession newKieSession(String kSessionName, Environment environment);

    /**
     * Creates the defualt StatelessKieSession for this KieContainer using the given Environment
     * @throws a RuntimeException if this KieContainer doesn't have any defualt StatelessKieSession
     * @see org.kie.api.builder.model.KieSessionModel#setDefault(boolean)
     */
    StatelessKieSession newStatelessKieSession();

    /**
     * Creates the StatelessKieSession with the given name for this KieContainer
     * @throws a RuntimeException if this KieContainer doesn't have any StatelessKieSession with the given name
     */
    StatelessKieSession newStatelessKieSession(String kSessionName);

    /**
     * Returns the ClassLoader used by this KieContainer
     */
    ClassLoader getClassLoader();
}

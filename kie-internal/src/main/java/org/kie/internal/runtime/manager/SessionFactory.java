package org.kie.internal.runtime.manager;

import org.kie.api.runtime.KieSession;

/**
 * Factory that produces <code>KieSession</code> instances.
 *
 */
public interface SessionFactory {

    /**
     * Produces new instance of <code>KieSession</code>
     * @return new instance of <code>KieSession</code>
     */
    KieSession newKieSession();

    /**
     * Loads <code>KieSession</code> form data store (such as db) based on given id.
     * @param sessionId identifier of ksession
     * @return loaded instance of <code>KieSession</code>
     * @throws RuntimeException in case session cannot be loaded
     */
    KieSession findKieSessionById(Long sessionId);

    /**
     * Closes the factory and releases all resources
     */
    void close();
    
    /**
     * Invoked when runtime engine is about to be disposed
     * @param sessionId ksession id that is disposed.
     */
    void onDispose(Long sessionId);
}

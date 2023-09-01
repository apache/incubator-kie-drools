 package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.api.PersistenceContext;
import org.drools.persistence.api.PersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;

/**
 * This class manages {@link JpaPersistenceContext} objects, and the underlying persistence context ({@link EntityManager}) 
 * instances for a persistent {@link KieSession} and other infrastructure classes that use persistence in KIE projects.
 * </p>
 * (For reference in the following documentation: the {@link EntityManager} is the class used to represent a persistence context)
 * </p>
 * There are 2 issues to take into account when looking at or modifying the code here: <ol>
 * <li>One of the features made available here is the ability for the user to supply their own (Command Scoped) persistence 
 *     context for use by the {@link KieSession}</li>
 * <li>However, significant race-conditions arise when a Command Scoped persistence context is used in one persistent
 * {@link KieSession} by multiple threads. In other words, when multiple threads call operations on a Singleton persistent 
 * {@link KieSession}.</li>
 * </ol>
 * 
 * This class uses {@link ThreadLocal} instances for two things:<ol>
 * <li>The internal Command Scoped {@link EntityManager} instance.</li>
 * <li></li>
 * </ol>
 */
public class JpaPersistenceContextManager extends AbstractPersistenceContextManager
    implements
    PersistenceContextManager {


    public JpaPersistenceContextManager(Environment env) {
        super(env);
    }
    
    public PersistenceContext getApplicationScopedPersistenceContext() {

        return new JpaPersistenceContext( getApplicationScopedEntityManager(), txm );
    }

    public PersistenceContext getCommandScopedPersistenceContext() {
        return new JpaPersistenceContext( getCommandScopedEntityManager(), txm );
    }

    public void beginCommandScopedEntityManager() {
        getCommandScopedPersistenceContext();
    }

}

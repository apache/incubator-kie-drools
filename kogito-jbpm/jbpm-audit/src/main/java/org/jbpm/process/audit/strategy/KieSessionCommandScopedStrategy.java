package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;

import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

/**
 * This strategy is used by instances that are<ul>
 * <li>used inside the {@link KieSession}</li>
 * <li>use the same (command-scoped) {@link EntityManager} instance as the {@link KieSession}</li>
 * </ul>
 */
public class KieSessionCommandScopedStrategy implements PersistenceStrategy {

    private Environment env;
    
    public KieSessionCommandScopedStrategy(Environment env) { 
        this.env = env;
    }
    
    @Override
    public EntityManager getEntityManager() {
        EntityManager em =  (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER); 
        if( em == null ) { 
            throw new IllegalStateException("The command scoped entity manager could not be found!");
        }
        return em;
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        // This is taken care of by the SingleSessionCommandService
        return false;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        // This is taken care of by the SingleSessionCommandService
    }

    @Override
    public void dispose() {
        env = null;
    }

}

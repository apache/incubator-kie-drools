package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneJtaSharedEntityManagerStrategy extends StandaloneJtaStrategy {

    private final EntityManager em;
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManagerFactory emf) {
        super(null);
        this.em = emf.createEntityManager();
    }
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManager em) {
        super(null);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        // do not close or clear the entity manager
        
        commitTransaction(transaction);
    }

}

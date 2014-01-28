package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneLocalSharedEntityManagerStrategy implements PersistenceStrategy {

    private EntityManager em;
    
    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManagerFactory emf) {
       this.em = emf.createEntityManager();
    }

    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManager em) {
       this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        em.getTransaction().begin();
        return true;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        em.getTransaction().commit();
    }

    @Override
    public void dispose() {
        // do nothing, because the em is SHARED.. 
        em = null;
    }

}

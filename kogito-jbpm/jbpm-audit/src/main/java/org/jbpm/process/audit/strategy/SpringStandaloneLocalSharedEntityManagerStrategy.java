package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneLocalSharedEntityManagerStrategy implements PersistenceStrategy {

    private EntityManager em;
    private boolean manageTx;
    
    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManagerFactory emf) {
       this.em = emf.createEntityManager();
       this.manageTx = true;
    }

    public SpringStandaloneLocalSharedEntityManagerStrategy(EntityManager em) {
       this.em = em;
       this.manageTx = false;
    }

    @Override
    public EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public Object joinTransaction(EntityManager em) {
        if (manageTx) {
        	em.getTransaction().begin();
        }
        return manageTx;
    }

    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        if (manageTx) {
        	em.getTransaction().commit();
        }
    }

    @Override
    public void dispose() {
        // do nothing, because the em is SHARED.. 
        em = null;
    }

}

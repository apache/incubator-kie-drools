package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneJtaSharedEntityManagerStrategy extends StandaloneJtaStrategy {

    private final EntityManager em;
    private boolean manageTx;
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManagerFactory emf) {
        super(null);
        this.em = emf.createEntityManager();
        this.manageTx = true;
    }
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManager em) {
        super(null);
        this.em = em;
        this.manageTx = false;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        // do not close or clear the entity manager
        if (manageTx) {
        	commitTransaction(transaction);
        }
    }

	@Override
	public Object joinTransaction(EntityManager em) {
		if (manageTx) {
			return super.joinTransaction(em);
		}
		
		return manageTx;
	}

}

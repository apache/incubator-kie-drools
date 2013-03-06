/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 *
 */
@ApplicationScoped
public class ExecutorDatabaseProducer {

    private EntityManagerFactory emf;
	
    @PersistenceUnit(unitName = "org.jbpm.executor")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
    	if (this.emf == null) {
    		// this needs to be here for non EE containers
    		this.emf = Persistence.createEntityManagerFactory("org.jbpm.executor");
    	}
    	return this.emf;
    }

    @Produces
    @ApplicationScoped
    public EntityManager getEntityManager() {
        EntityManager em = getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        return em;
    }

    @ApplicationScoped
    public void commitAndClose(@Disposes EntityManager em) {
        try {
            em.getTransaction().commit();
            em.close();
        } catch (Exception e) {

        }
    }
   
    @Produces
    public Logger createLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember()
                .getDeclaringClass().getName());
    }
    
}

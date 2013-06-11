/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.form.builder.services.internal;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

/**
 *
 */

public class FormDatabaseProducer {
        
    private EntityManagerFactory emf;
    
    @PersistenceUnit(unitName = "org.jbpm.form.builder")
    @ApplicationScoped
    @Produces
    public EntityManagerFactory getEntityManagerFactory() {
        if (this.emf == null) {
            // this needs to be here for non EE containers

            this.emf = Persistence.createEntityManagerFactory("org.jbpm.form.builder");

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
}

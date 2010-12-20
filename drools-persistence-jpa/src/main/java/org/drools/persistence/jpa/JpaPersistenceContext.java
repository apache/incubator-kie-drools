package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.PersistenceContext;

public class JpaPersistenceContext implements PersistenceContext {
    EntityManager em;
    
    public JpaPersistenceContext(EntityManager em) {
        this.em = em;
    }

    public void persist(Object entity) {
        this.em.persist( entity );
    }

    public <T> T find(Class<T> entityClass, 
                      Object primaryKey) {
        return this.em.find( entityClass, primaryKey );
    }

    public boolean isOpen() {
        return this.em.isOpen();
    }

    public void joinTransaction() {
        this.em.joinTransaction();
    }

    public void close() {
        this.em.close();
    }

}
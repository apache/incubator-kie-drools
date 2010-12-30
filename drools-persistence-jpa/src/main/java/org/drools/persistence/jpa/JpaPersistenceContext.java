package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;

public class JpaPersistenceContext implements PersistenceContext {
    EntityManager em;
    
    public JpaPersistenceContext(EntityManager em) {
        this.em = em;
    }

    public void persist(SessionInfo entity) {
        this.em.persist( entity );
    }

    public SessionInfo findSessionInfo(Long id) {
        return this.em.find( SessionInfo.class, id );
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
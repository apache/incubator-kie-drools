package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public class JpaPersistenceContext implements PersistenceContext {
    private EntityManager em;
    
    public JpaPersistenceContext(EntityManager em) {
        this.em = em;
    }

    public void persist(SessionInfo entity) {
        this.em.persist( entity );
    }

    public SessionInfo findSessionInfo(Integer id) {
        return this.em.find( SessionInfo.class, id );
    }

    public boolean isOpen() {
        return this.em.isOpen();
    }

    public void close() {
        this.em.close();
    }

    public void persist(WorkItemInfo workItemInfo) {
        em.persist( workItemInfo );
    }

    public WorkItemInfo findWorkItemInfo(Long id) {
        return em.find( WorkItemInfo.class, id );
    }

    public void remove(WorkItemInfo workItemInfo) {
        em.remove( workItemInfo );
    }

    public WorkItemInfo merge(WorkItemInfo workItemInfo) {
        return em.merge( workItemInfo );
    }
    
    public EntityManager getEntityManager() {
        return this.em;
    }
}

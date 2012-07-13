package org.drools.persistence.jpa;

import javax.persistence.EntityManager;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;

public class JpaPersistenceContext implements PersistenceContext {
    private EntityManager em;
    
    private boolean isJTA;
    
    public JpaPersistenceContext(EntityManager em) {
        this.em = em;
        isJTA = true;
    }

    public JpaPersistenceContext(EntityManager em, boolean isJTA) {
        this.em = em;
        this.isJTA = isJTA;
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

    public void joinTransaction() {
        if (isJTA) this.em.joinTransaction();
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
    
    protected EntityManager getEntityManager() {
        return this.em;
    }
}

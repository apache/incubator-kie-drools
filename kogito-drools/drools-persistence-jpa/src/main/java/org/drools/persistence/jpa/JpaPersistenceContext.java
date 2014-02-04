package org.drools.persistence.jpa;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPersistenceContext implements PersistenceContext {

    private static Logger logger = LoggerFactory.getLogger(JpaPersistenceContext.class);
    
    private EntityManager em;
    protected final boolean isJTA;
    protected final boolean pessimisticLocking;
    
    public JpaPersistenceContext(EntityManager em) {
        this(em, true, false);
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA) {
       this(em, isJTA, false); 
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA, boolean locking) {
        this.em = em;
        this.isJTA = isJTA;
        this.pessimisticLocking = locking;
    }

    public SessionInfo persist(SessionInfo entity) {
        this.em.persist( entity );
        if( this.pessimisticLocking ) { 
            return this.em.find(SessionInfo.class, entity.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT );
        }
        return entity;
    }

    public SessionInfo findSessionInfo(Integer id) {
        if( this.pessimisticLocking ) { 
            return this.em.find( SessionInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
        }
        return this.em.find( SessionInfo.class, id );
    }

    public void remove(SessionInfo sessionInfo) {
        em.remove( sessionInfo );
        em.flush();
    }
    
    public void lock(SessionInfo sessionInfo) {
         this.em.lock( sessionInfo, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
    }
    
    public boolean isOpen() {
        return this.em.isOpen();
    }

    public void joinTransaction() {
    	if (isJTA) {
    	    this.em.joinTransaction();
    	}
    }

    public void close() {
        this.em.close();
    }

    public WorkItemInfo persist(WorkItemInfo workItemInfo) {
        em.persist( workItemInfo );
        if( this.pessimisticLocking ) { 
            return em.find(WorkItemInfo.class, workItemInfo.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }
        return workItemInfo;
    }

    public WorkItemInfo findWorkItemInfo(Long id) {
        if( this.pessimisticLocking ) { 
            return this.em.find( WorkItemInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
        }
        return em.find( WorkItemInfo.class, id );
    }

    public void remove(WorkItemInfo workItemInfo) {
        em.remove( workItemInfo );
    }

    public WorkItemInfo merge(WorkItemInfo workItemInfo) {
        if( this.pessimisticLocking ) { 
            if( em.contains(workItemInfo) ) { 
                em.lock(workItemInfo, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            } else { 
                // Yes, this is a hack, but for detached entities, it's the only way to lock before merging
                WorkItemInfo dbWorkItemInfo = em.find(WorkItemInfo.class, workItemInfo.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                for( Field field : WorkItemInfo.class.getDeclaredFields() ) { 
                    boolean access = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        field.set(dbWorkItemInfo, field.get(workItemInfo));
                    } catch (Exception e) {
                        logger.error("Unable to set field " + field.getName() + " of unmerged WorkItemInfo instance!", e);
                    } 
                    field.setAccessible(access);
                }
            }
        }
        return em.merge( workItemInfo );
    }
    
    public void lock(WorkItemInfo workItemInfo) {
         this.em.lock( workItemInfo, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
    }
    
    protected EntityManager getEntityManager() {
        return this.em;
    }

 }  
package org.drools.persistence.jpa;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaPersistenceContext implements PersistenceContext {

    private static Logger logger = LoggerFactory.getLogger(JpaPersistenceContext.class);
    
    private EntityManager em;
    protected final boolean isJTA;
    protected final boolean pessimisticLocking;
    protected final TransactionManager txm;
    
    public JpaPersistenceContext(EntityManager em, TransactionManager txm) {
        this(em, true, false, txm);
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA, TransactionManager txm) {
       this(em, isJTA, false, txm);
    }
    
    public JpaPersistenceContext(EntityManager em, boolean isJTA, boolean locking, TransactionManager txm) {
        this.em = em;
        this.isJTA = isJTA;
        this.pessimisticLocking = locking;
        this.txm = txm;
    }

    public SessionInfo persist(SessionInfo entity) {
        this.em.persist( entity );
        TransactionManagerHelper.addToUpdatableSet(txm, entity);
        if( this.pessimisticLocking ) {
            this.em.flush();
            return this.em.find(SessionInfo.class, entity.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT );
        }
        return entity;
    }

    public SessionInfo findSessionInfo(Long id) {

        SessionInfo sessionInfo = null;
        if( this.pessimisticLocking ) {
            sessionInfo = this.em.find( SessionInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
            TransactionManagerHelper.addToUpdatableSet(txm, sessionInfo);

            return sessionInfo;
        }
        sessionInfo = this.em.find( SessionInfo.class, id );

        TransactionManagerHelper.addToUpdatableSet(txm, sessionInfo);

        return sessionInfo;
    }

    public void remove(SessionInfo sessionInfo) {
        if (!em.contains(sessionInfo)) {
            SessionInfo s = em.getReference(SessionInfo.class, sessionInfo.getId());
            em.remove( s );
        } else {
            em.remove(sessionInfo);
        }
        TransactionManagerHelper.removeFromUpdatableSet(txm, sessionInfo);
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
        TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);
        if( this.pessimisticLocking ) {
            this.em.flush();
            return em.find(WorkItemInfo.class, workItemInfo.getId(), LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        }

        return workItemInfo;
    }

    public WorkItemInfo findWorkItemInfo(Long id) {
        WorkItemInfo workItemInfo = null;
        if( this.pessimisticLocking ) {
            workItemInfo = this.em.find( WorkItemInfo.class, id, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
            TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);

            return workItemInfo;
        }
        workItemInfo = em.find( WorkItemInfo.class, id );

        TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);

        return workItemInfo;
    }

    public void remove(WorkItemInfo workItemInfo) {
        em.remove( workItemInfo );
        TransactionManagerHelper.removeFromUpdatableSet(txm, workItemInfo);
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
        TransactionManagerHelper.addToUpdatableSet(txm, workItemInfo);
        return em.merge( workItemInfo );
    }
    
    public void lock(WorkItemInfo workItemInfo) {
         this.em.lock( workItemInfo, LockModeType.PESSIMISTIC_FORCE_INCREMENT );
    }
    
    protected EntityManager getEntityManager() {
        return this.em;
    }


 }  